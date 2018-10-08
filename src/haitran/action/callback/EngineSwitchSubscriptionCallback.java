package haitran.action.callback;

import haitran.Constants;
import haitran.controllers.BaseController;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;

import java.util.Map;

public class EngineSwitchSubscriptionCallback extends SubscriptionCallback {
    private BaseController controller;

    public EngineSwitchSubscriptionCallback(Service service, int requestedDurationSeconds, BaseController controller) {
        super(service, requestedDurationSeconds);
        this.controller = controller;
    }

    @Override
    protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
        e.printStackTrace();
    }

    @Override
    protected void established(GENASubscription genaSubscription) {
        System.out.println("Established: " + genaSubscription.getSubscriptionId() + ".Engine switch subscription created.");
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
            controller.changeEngineButton(value);
            System.out.println("New value: " + value);
        }
    }

    @Override
    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
        System.out.println("Missed events: " + numberOfMissedEvents);
    }
}
