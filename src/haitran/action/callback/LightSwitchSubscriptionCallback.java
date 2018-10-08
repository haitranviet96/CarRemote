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

public class LightSwitchSubscriptionCallback extends SubscriptionCallback {
    private BaseController controller;

    public LightSwitchSubscriptionCallback(Service service, int requestedDurationSeconds, BaseController controller) {
        super(service, requestedDurationSeconds);
        this.controller = controller;
    }

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
        Car car = Car.getInstace();
        if (values.containsKey(Constants.LEFT_SIGNAL)) {
            boolean value = (boolean) values.get(Constants.LEFT_SIGNAL).getValue();
            controller.changeLeftSignal(value);
            System.out.println("New value: " + car.getLeftSignalStatus());
        }
        if (values.containsKey(Constants.RIGHT_SIGNAL)) {
            boolean value = (boolean) values.get(Constants.RIGHT_SIGNAL).getValue();
            controller.changeRightSignal(value);
            System.out.println("New value: " + car.getRightSignalStatus());
        }
        if (values.containsKey(Constants.HEAD_LIGHT)) {
            boolean value = (boolean) values.get(Constants.HEAD_LIGHT).getValue();
            controller.changeHeadLightSignal(value);
            System.out.println("New value: " + car.getHeadLightStatus());
        }
    }

    @Override
    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
        System.out.println("Missed events: " + numberOfMissedEvents);
    }
}
