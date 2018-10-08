package haitran.action.light;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetRightSignal extends ActionInvocation {
    public SetRightSignal(Service service, boolean status) {
        super(service.getAction(Constants.SET_RIGHT_SIGNAL));

        try {
            setInput(Constants.NEW_RIGHT_SIGNAL_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
