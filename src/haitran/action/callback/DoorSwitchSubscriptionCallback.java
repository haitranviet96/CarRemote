package haitran.action.callback;

import haitran.Car;
import haitran.Constants;
import haitran.controllers.BaseController;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;

import java.util.Map;

public class DoorSwitchSubscriptionCallback extends SubscriptionCallback {
    private BaseController controller;

    public DoorSwitchSubscriptionCallback(Service service, int requestedDurationSeconds, BaseController controller) {
        super(service, requestedDurationSeconds);
        this.controller = controller;
    }

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
        Car car = Car.getInstace();
        if (values.containsKey(Constants.LEFT_DOOR)) {
            boolean value = (boolean) values.get(Constants.LEFT_DOOR).getValue();
            controller.changeLeftDoorStatus(value);
            System.out.println("New value: " + car.getLeftDoorStatus());
        }
        if (values.containsKey(Constants.RIGHT_DOOR)) {
            boolean value = (boolean) values.get(Constants.RIGHT_DOOR).getValue();
            controller.changeRightDoorStatus(value);
            System.out.println("New value: " + car.getRightDoorStatus());
        }
        if (values.containsKey(Constants.HOOD)) {
            boolean value = (boolean) values.get(Constants.HOOD).getValue();
            controller.changeHoodStatus(value);
            System.out.println("New value: " + car.getHoodStatus());
        }
        if (values.containsKey(Constants.TRUNK)) {
            boolean value = (boolean) values.get(Constants.TRUNK).getValue();
            controller.changeTrunkStatus(value);
            System.out.println("New value: " + car.getTrunkStatus());
        }
    }

    @Override
    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
        System.out.println("Missed events: " + numberOfMissedEvents);
    }

}
