package haitran.action;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetLeftSignal extends ActionInvocation {
    public SetLeftSignal(Service service, boolean status) {
        super(service.getAction(Constants.SET_LEFT_SIGNAL));

        try {
            setInput(Constants.NEW_LEFT_SIGNAL_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
