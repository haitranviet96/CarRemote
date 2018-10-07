package haitran.action;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetHeadLight extends ActionInvocation {
    public SetHeadLight(Service service, boolean status) {
        super(service.getAction(Constants.SET_HEAD_LIGHT));

        try {
            setInput(Constants.NEW_HEAD_LIGHT_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
