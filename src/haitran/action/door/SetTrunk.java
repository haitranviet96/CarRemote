package haitran.action.door;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetTrunk extends ActionInvocation {
    public SetTrunk(Service service, boolean status) {
        super(service.getAction(Constants.SET_TRUNK));

        try {
            setInput(Constants.NEW_TRUNK_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
