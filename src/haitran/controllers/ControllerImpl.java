package haitran.controllers;

import haitran.Car;
import haitran.Constants;
import haitran.action.SetEngineStatus;
import haitran.action.callback.EngineSwitchSubscriptionCallback;
import haitran.action.callback.DoorSwitchSubscriptionCallback;
import haitran.action.callback.LightSwitchSubscriptionCallback;
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
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.io.IOException;

public class ControllerImpl implements BaseController {
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

    private Car car = Car.getInstace();
    private UpnpService upnpService;
    private RegistryListener registryListener = new DefaultRegistryListener() {
        @Override
        public void localDeviceAdded(Registry registry, LocalDevice localDevice) {
            System.out.println("Local car detected.");
            if (localDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                System.out.println("Car system detected.");
                car.setDevice(localDevice);
                upnpService.getControlPoint().execute(
                        new EngineSwitchSubscriptionCallback(getServiceById(car, Constants.ENGINE_SWITCH),
                                Integer.MAX_VALUE, ControllerImpl.this));
                upnpService.getControlPoint().execute(
                        new LightSwitchSubscriptionCallback(getServiceById(car, Constants.LIGHT_SWITCH),
                                Integer.MAX_VALUE, ControllerImpl.this));
                upnpService.getControlPoint().execute(
                        new DoorSwitchSubscriptionCallback(getServiceById(car, Constants.DOOR_SWITCH),
                                Integer.MAX_VALUE, ControllerImpl.this));
            }
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice localDevice) {
            if (localDevice.getDetails().getModelDetails().getModelName().equals(Constants.MODEL_NAME)) {
                System.out.println("Car system removed.");
                car = null;
            }
        }
    };

    public ControllerImpl() throws Exception {
        initialize();
    }

    private void initialize() throws Exception {
        // server and upnpService init
        upnpService = new UpnpServiceImpl();

        // Add a listener for car registration events
        upnpService.getRegistry().addListener(registryListener);

        upnpService.getRegistry().addDevice(createDevice());

        // Broadcast a search message for car remote car
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

    // change view methods
    @Override
    public void changeEngineButton(boolean newStatus) {
        if (engineButton != null && newStatus != car.getEngineStatus()) {
            if (!newStatus) {
                if (car.getLeftSignalStatus()) onChangeLeftSignal();
                if (car.getRightSignalStatus()) onChangeRightSignal();
                if (car.getHeadLightStatus()) onChangeHeadLight();
            }
            car.setEngineStatus(newStatus);
            engineButton.setStyle("-fx-background-image: url('" +
                    (car.getEngineStatus() ? getClass().getResource("../../resources/stop.png").toExternalForm() :
                            getClass().getResource("../../resources/start.png").toExternalForm()) + "')");
        }
    }

    @Override
    public void changeLeftSignal(boolean newStatus) {
        if (leftSignal != null && newStatus != car.getLeftSignalStatus()) {
            car.setLeftSignalStatus(car.getEngineStatus() && newStatus);
            leftSignal.setOpacity(car.getLeftSignalStatus() ? 1.0 : 0.0);
        }
    }

    @Override
    public void changeRightSignal(boolean newStatus) {
        if (rightSignal != null && newStatus != car.getRightSignalStatus()) {
            car.setRightSignalStatus(car.getEngineStatus() && newStatus);
            rightSignal.setOpacity(car.getRightSignalStatus() ? 1.0 : 0.0);
        }
    }

    @Override
    public void changeHeadLightSignal(boolean newStatus) {
        if (headLight != null && newStatus != car.getHeadLightStatus()) {
            car.setHeadLightStatus(car.getEngineStatus() && newStatus);
            headLight.setOpacity(car.getHeadLightStatus() ? 1.0 : 0.0);
        }
    }

    @Override
    public void changeLeftDoorStatus(boolean newStatus) {
        if (leftDoor != null && newStatus != car.getLeftDoorStatus()) {
            car.setLeftDoorStatus(newStatus);
            leftDoor.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (car.getLeftDoorStatus() ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    @Override
    public void changeRightDoorStatus(boolean newStatus) {
        if (rightDoor != null && newStatus != car.getRightDoorStatus()) {
            car.setRightDoorStatus(newStatus);
            rightDoor.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (car.getRightDoorStatus() ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    @Override
    public void changeHoodStatus(boolean newStatus) {
        if (hood != null && newStatus != car.getHoodStatus()) {
            car.setHoodStatus(newStatus);
            hood.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (car.getHoodStatus() ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    @Override
    public void changeTrunkStatus(boolean newStatus) {
        if (trunk != null && newStatus != car.getTrunkStatus()) {
            car.setTrunkStatus(newStatus);
            trunk.setImage(
                    new Image(getClass().getResource("/resources/" +
                            (car.getTrunkStatus() ? "lock_off.png" : "lock_on.png")).toString()));
        }
    }

    // controller method
    @FXML
    public void onChangeEngineStatus() {
        Service service = getServiceById(car, Constants.ENGINE_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetEngineStatus(service, !car.getEngineStatus()));
        }
    }

    @FXML
    public void onChangeLeftSignal() {
        Service service = getServiceById(car, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetLeftSignal(service, !car.getLeftSignalStatus()));
        }
    }

    @FXML
    public void onChangeRightSignal() {
        Service service = getServiceById(car, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetRightSignal(service, !car.getRightSignalStatus()));
        }
    }

    @FXML
    public void onChangeHeadLight() {
        Service service = getServiceById(car, Constants.LIGHT_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetHeadLight(service, !car.getHeadLightStatus()));
        }
    }

    @FXML
    public void onChangeLeftDoorStatus() {
        Service service = getServiceById(car, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetLeftDoor(service, !car.getLeftDoorStatus()));
        }
    }

    @FXML
    public void onChangeRightDoorStatus() {
        Service service = getServiceById(car, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetRightDoor(service, !car.getRightDoorStatus()));
        }
    }

    @FXML
    public void onChangeHoodStatus() {
        Service service = getServiceById(car, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetHood(service, !car.getHoodStatus()));
        }
    }

    @FXML
    public void onChangeTrunkStatus() {
        Service service = getServiceById(car, Constants.DOOR_SWITCH);
        if (service != null) {
            executeAction(upnpService, new SetTrunk(service, !car.getTrunkStatus()));
        }
    }

    private Service getServiceById(Car car, String serviceId) {
        if (car == null) {
            return null;
        }
        return car.getDevice().findService(new UDAServiceId(serviceId));
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
