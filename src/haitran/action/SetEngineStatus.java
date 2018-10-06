package haitran.action;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetEngineStatus extends ActionInvocation {
    public SetEngineStatus(Service service, boolean status) {
        super(service.getAction(Constants.SET_TARGET));

        try {
            setInput(Constants.NEW_TARGET_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
