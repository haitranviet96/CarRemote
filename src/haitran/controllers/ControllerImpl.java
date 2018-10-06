package haitran.controllers;

import haitran.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControllerImpl implements BaseController {
    private static boolean ENGINE_STATUS = false;

    @FXML
    private Button engineButton;

    private Device device;


    private SubscriptionCallback createEngineSwitchSubscriptionCallBack(Service service) {
        return new SubscriptionCallback(service, Integer.MAX_VALUE) {
            @Override
            protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
                System.err.println(s);
            }

            @Override
            protected void established(GENASubscription genaSubscription) {
                System.out.println("Established: " + genaSubscription.getSubscriptionId() + ".Power switch subscription created.");
//                setPowerStatus(Constants.POWER_STATUS_DEFAULT);
            }

            @Override
            protected void ended(GENASubscription genaSubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {

            }

            @Override
            public void eventReceived(GENASubscription sub) {
                System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();

                if (values.containsKey(Constants.STATUS)) {
                    boolean value = (boolean) values.get(Constants.STATUS).getValue();
                    onEngineStatusChange();
                    System.out.println("New value: " + value);
                }
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
            }
        };
    }

    @FXML
    private void onEngineStatusChange() {
        if (ENGINE_STATUS) {
            // engine shutdown
            ENGINE_STATUS = false;
            engineButton.setStyle("-fx-background-image: url('" + getClass().getResource("../../resources/start.png").toExternalForm() + "')");
        } else {
            // engine start
            ENGINE_STATUS = true;
            engineButton.setStyle("-fx-background-image: url('" + getClass().getResource("../../resources/stop.png").toExternalForm() + "')");
        }
    }

    public ControllerImpl() throws Exception {
        initialize();
    }

    void initialize() throws Exception {
        // server and upnpService init
        UpnpService upnpService = new UpnpServiceImpl();


        Runtime.getRuntime().addShutdownHook(new Thread(upnpService::shutdown));
        upnpService.getRegistry().addDevice(createDevice());

        // Add a listener for device registration events
        upnpService.getRegistry().addListener(createRegistryListener(upnpService));
        // Broadcast a search message for car remote device
        UDADeviceTypeHeader header = new UDADeviceTypeHeader(new UDADeviceType(Constants.DEVICE_NAME));
        upnpService.getControlPoint().search(header);
    }

    private RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {
            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice remoteDevice) {
                System.out.println("Remote device detected.");
                if (remoteDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                    System.out.println("Car system detected.");
                    device = remoteDevice;
                    upnpService.getControlPoint().execute(createEngineSwitchSubscriptionCallBack(getServiceById(device, Constants.SWITCH_POWER)));
                }
            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice remoteDevice) {
                if (remoteDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                    System.out.println("Car system removed.");
                    device = null;
                }
            }
        };
    }

    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(Constants.DEVICE_NAME));

        DeviceType type = new UDADeviceType(Constants.DEVICE_TYPE, 1);

        DeviceDetails details = new DeviceDetails(Constants.DEVICE_FRIENDLY_NAME,
                new ManufacturerDetails(Constants.MANUFACTURER_DETAILS),
                new ModelDetails(Constants.MODEL_NAME, Constants.MODEL_DESCRIPTION, Constants.MODEL_NUMBER));

        Icon icon = new Icon("image/png", 48, 48, 8, getClass().getResource(Constants.AUDIO_SYSTEM_IMAGE));

        LocalService<SwitchPower> switchPowerService = new AnnotationLocalServiceBinder().read(SwitchPower.class);
        switchPowerService.setManager(new DefaultServiceManager(switchPowerService, SwitchPower.class));


        return new LocalDevice(
                identity, type, details, icon,
                new LocalService[]{
                        switchPowerService,
                        audioControlService,
                        playMusicService
                }
        );
    }

    @Override
    public void leftSignal(boolean value) {

    }

    @Override
    public void rightSignal(boolean value) {

    }

    @Override
    public void light(boolean value) {

    }

    @Override
    public void openDoor(boolean status) {

    }

    private Service getServiceById(Device device, String serviceId) {
        if (device == null) {
            return null;
        }
        return device.findService(new UDAServiceId(serviceId));
    }
}
