package haitran.action.door;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetRightDoor extends ActionInvocation {
    public SetRightDoor(Service service, boolean status) {
        super(service.getAction(Constants.SET_RIGHT_DOOR));

        try {
            setInput(Constants.NEW_RIGHT_DOOR_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
