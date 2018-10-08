package haitran.controllers;

import haitran.Constants;
import haitran.action.SetEngineStatus;
import haitran.action.door.SetHood;
import haitran.action.door.SetLeftDoor;
import haitran.action.door.SetRightDoor;
import haitran.action.door.SetTrunk;
import haitran.action.light.SetHeadLight;
import haitran.action.light.SetLeftSignal;
import haitran.action.light.SetRightSignal;
import haitran.services.DoorSwitch;
import haitran.services.EngineSwitch;
import haitran.services.LightSwitch;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionInvocation;
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

public class ControllerImpl implements BaseController {
    private static boolean ENGINE_STATUS = false;

    private static boolean LEFT_SIGNAL_STATUS = false;
    private static boolean RIGHT_SIGNAL_STATUS = false;
    private static boolean HEAD_LIGHT_STATUS = false;

    private static boolean LEFT_DOOR_STATUS = false;
    private static boolean RIGHT_DOOR_STATUS = false;
    private static boolean HOOD_STATUS = false;
    private static boolean TRUNK_STATUS = false;

    @FXML
    private Button engineButton;
    @FXML
    private ImageView leftSignal;
    @FXML
    private ImageView rightSignal;
    @FXML
    private ImageView headLight;
    @FXML
    private ImageView leftDoor;
    @FXML
    private ImageView rightDoor;
    @FXML
    private ImageView trunk;
    @FXML
    private ImageView hood;

    private Device device;
    private UpnpService upnpService;
    private RegistryListener registryListener = new DefaultRegistryListener() {
        @Override
        public void localDeviceAdded(Registry registry, LocalDevice localDevice) {
            System.out.println("Local device detected.");
            if (localDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                System.out.println("Car system detected.");
                device = localDevice;
                upnpService.getControlPoint().execute(createEngineSwitchSubscriptionCallBack(getServiceById(device, Constants.ENGINE_SWITCH)));
                upnpService.getControlPoint().execute(createLightSwitchSubscriptionCallBack(getServiceById(device, Constants.LIGHT_SWITCH)));
                upnpService.getControlPoint().execute(createDoorSwitchSubscriptionCallBack(getServiceById(device, Constants.DOOR_SWITCH)));
            }
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice localDevice) {
            if (localDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                System.out.println("Car system removed.");
                device = null;
            }
        }
    };

    public ControllerImpl() throws Exception {
        initialize();
    }

    private void initialize() throws Exception {
        // server and upnpService init
        upnpService = new UpnpServiceImpl();

        // Add a listener for device registration events
        upnpService.getRegistry().addListener(registryListener);

        upnpService.getRegistry().addDevice(createDevice());

        // Broadcast a search message for car remote device
        UDADeviceTypeHeader header = new UDADeviceTypeHeader(new UDADeviceType(Constants.DEVICE_TYPE));

        upnpService.getControlPoint().search(header);

        Runtime.getRuntime().addShutdownHook(new Thread(upnpService::shutdown));
    }

    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(Constants.DEVICE_NAME));

        DeviceType type = new UDADeviceType(Constants.DEVICE_TYPE, 1);

        DeviceDetails details = new DeviceDetails(Constants.DEVICE_FRIENDLY_NAME,
                new ManufacturerDetails(Constants.MANUFACTURER_DETAILS),
                new ModelDetails(Constants.MODEL_NAME, Constants.MODEL_DESCRIPTION, Constants.MODEL_NUMBER));

        Icon icon = new Icon("image/png", 48, 48, 8, getClass().getResource("../../resources/icon.png"));

        LocalService<EngineSwitch> engineSwitchService = new AnnotationLocalServiceBinder().read(EngineSwitch.class);
        engineSwitchService.setManager(new DefaultServiceManager(engineSwitchService, EngineSwitch.class));
        LocalService<LightSwitch> lightSwitchService = new AnnotationLocalServiceBinder().read(LightSwitch.class);
        lightSwitchService.setManager(new DefaultServiceManager(lightSwitchService, LightSwitch.class));
        LocalService<DoorSwitch> doorSwitchService = new AnnotationLocalServiceBinder().read(DoorSwitch.class);
        doorSwitchService.setManager(new DefaultServiceManager(doorSwitchService, DoorSwitch.class));

        return new LocalDevice(identity, type, details, icon,
                new LocalService[]{
                        engineSwitchService, lightSwitchService, doorSwitchService
                }
        );
    }

    // create subscription methods
    private SubscriptionCallback createEngineSwitchSubscriptionCallBack(Service service) {
        return new SubscriptionCallback(service, Integer.MAX_VALUE) {
            @Override
            protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
                e.printStackTrace();
            }

            @Override
            protected void established(GENASubscription genaSubscription) {
                System.out.println("Established: " + genaSubscription.getSubscriptionId() + ".Engine switch subscription created.");
//                onEngineStatusChange();
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
                    changeEngineButton(value);
                    System.out.println("New value: " + value);
                }
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
            }
        };
    }

    private SubscriptionCallback createLightSwitchSubscriptionCallBack(Service service) {
        return new SubscriptionCallback(service, Integer.MAX_VALUE) {
            @Override
            protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
                e.printStackTrace();
            }

            @Override
            protected void established(GENASubscription genaSubscription) {
                System.out.println("Established: " + genaSubscription.getSubscriptionId() + ".Light switch subscription created.");
            }

            @Override
            protected void ended(GENASubscription genaSubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {

            }

            @Override
            public void eventReceived(GENASubscription sub) {
                System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                for (String key : values.keySet()) {
                    System.out.println(key + " changed.");
                }
                if (values.containsKey(Constants.LEFT_SIGNAL)) {
                    boolean value = (boolean) values.get(Constants.LEFT_SIGNAL).getValue();
                    changeLeftSignal(value);
                    System.out.println("New value: " + LEFT_SIGNAL_STATUS);
                }
                if (values.containsKey(Constants.RIGHT_SIGNAL)) {
                    boolean value = (boolean) values.get(Constants.RIGHT_SIGNAL).getValue();
                    changeRightSignal(value);
                    System.out.println("New value: " + RIGHT_SIGNAL_STATUS);
                }
                if (values.containsKey(Constants.HEAD_LIGHT)) {
                    boolean value = (boolean) values.get(Constants.HEAD_LIGHT).getValue();
                    changeHeadLightSignal(value);
                    System.out.println("New value: " + HEAD_LIGHT_STATUS);
                }
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
            }
        };
    }


    private SubscriptionCallback createDoorSwitchSubscriptionCallBack(Service service) {
        return new SubscriptionCallback(service, Integer.MAX_VALUE) {
            @Override
            protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
                e.printStackTrace();
            }

            @Override
            protected void established(GENASubscription genaSubscription) {
                System.out.println("Established: " + genaSubscription.getSubscriptionId() + ".Door switch subscription created.");
            }

            @Override
            protected void ended(GENASubscription genaSubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {

            }

            @Override
            public void eventReceived(GENASubscription sub) {
                System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                for (String key : values.keySet()) {
                    System.out.println(key + " changed.");
                }
                if (values.containsKey(Constants.LEFT_DOOR)) {
                    boolean value = (boolean) values.get(Constants.LEFT_DOOR).getValue();
                    changeLeftDoorStatus(value);
                    System.out.println("New value: " + LEFT_DOOR_STATUS);
                }
                if (values.containsKey(Constants.RIGHT_DOOR)) {
                    boolean value = (boolean) values.get(Constants.RIGHT_DOOR).getValue();
                    changeRightDoorStatus(value);
                    System.out.println("New value: " + RIGHT_DOOR_STATUS);
                }
                if (values.containsKey(Constants.HOOD)) {
                    boolean value = (boolean) values.get(Constants.HOOD).getValue();
                    changeHoodStatus(value);
                    System.out.println("New value: " + HOOD_STATUS);
                }
                if (values.containsKey(Constants.TRUNK)) {
                    boolean value = (boolean) values.get(Constants.TRUNK).getValue();
                    changeTrunkStatus(value);
                    System.out.println("New value: " + TRUNK_STATUS);
                }
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
            }
        };
    }

    // change view methods
    private void changeEngineButton(boolean newStatus) {
        if (engineButton != null && newStatus != ENGINE_STATUS) {
            if (!newStatus) {
                if (LEFT_SIGNAL_STATUS) onChangeLeftSignal();
                if (RIGHT_SIGNAL_STATUS) onChangeRightSignal();
                if (HEAD_LIGHT_STATUS) onChangeHeadLight();
            }
            ENGINE_STATUS = newStatus;
            engineButton.setStyle("-fx-background-image: url('" +
                    (ENGINE_STATUS ? getClass().getResource("../../resources/stop.png").toExternalForm() :
                            getClass().getResource("../../resources/start.png").toExternalForm()) + "')");
        }
    }

    private void changeLeftSignal(boolean newStatus) {
        if (leftSignal != null && newStatus != LEFT_SIGNAL_STATUS) {
            LEFT_SIGNAL_STATUS = ENGINE_STATUS && newStatus;
            leftSignal.setOpacity(LEFT_SIGNAL_STATUS ? 1.0 : 0.0);
        }
    }

    private void changeRightSignal(boolean newStatus) {
        if (rightSignal != null && newStatus != RIGHT_SIGNAL_STATUS) {
            RIGHT_SIGNAL_STATUS = ENGINE_STATUS && newStatus;
            rightSignal.setOpacity(RIGHT_SIGNAL_STATUS ? 1.0 : 0.0);
        }
    }

    private void changeHeadLightSignal(boolean newStatus) {
        if (headLight != null && newStatus != HEAD_LIGHT_STATUS) {
            HEAD_LIGHT_STATUS = ENGINE_STATUS && newStatus;
            headLight.setOpacity(HEAD_LIGHT_STATUS ? 1.0 : 0.0);
        }
    }

    private void changeLeftDoorStatus(boolean newStatus) {
        if (leftDoor != null && newStatus != LEFT_DOOR_STATUS) {
            LEFT_DOOR_STATUS = newStatus;
            leftDoor.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (LEFT_DOOR_STATUS ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    private void changeRightDoorStatus(boolean newStatus) {
        if (rightDoor != null && newStatus != RIGHT_DOOR_STATUS) {
            RIGHT_DOOR_STATUS = newStatus;
            rightDoor.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (RIGHT_DOOR_STATUS ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    private void changeHoodStatus(boolean newStatus) {
        if (hood != null && newStatus != HOOD_STATUS) {
            HOOD_STATUS = newStatus;
            hood.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (HOOD_STATUS ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    private void changeTrunkStatus(boolean newStatus) {
        if (trunk != null && newStatus != TRUNK_STATUS) {
            TRUNK_STATUS = newStatus;
            trunk.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (TRUNK_STATUS ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    // controller method
    @Override
    @FXML
    public void onChangeEngineStatus() {
        Service service = getServiceById(device, Constants.ENGINE_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetEngineStatus(service, !ENGINE_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeLeftSignal() {
        Service service = getServiceById(device, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetLeftSignal(service, !LEFT_SIGNAL_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeRightSignal() {
        Service service = getServiceById(device, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetRightSignal(service, !RIGHT_SIGNAL_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeHeadLight() {
        Service service = getServiceById(device, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetHeadLight(service, !HEAD_LIGHT_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeLeftDoorStatus(){
        Service service = getServiceById(device, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetLeftDoor(service, !LEFT_DOOR_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeRightDoorStatus(){
        Service service = getServiceById(device, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetRightDoor(service, !RIGHT_DOOR_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeHoodStatus(){
        Service service = getServiceById(device, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetHood(service, !HOOD_STATUS));
        }
    }

    @Override
    @FXML
    public void onChangeTrunkStatus(){
        Service service = getServiceById(device, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetTrunk(service, !TRUNK_STATUS));
        }
    }

    private Service getServiceById(Device device, String serviceId) {
        if (device == null) {
            return null;
        }
        return device.findService(new UDAServiceId(serviceId));
    }

    protected void executeAction(UpnpService upnpService, ActionInvocation action) {
        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(
                new ActionCallback(action) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
                        System.out.println("Successfully called action " + invocation.getClass().getSimpleName());
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        System.err.println(defaultMsg);
                    }
                }
        );
    }
}
