package haitran.action.door;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetLeftDoor extends ActionInvocation {
    public SetLeftDoor(Service service, boolean status) {
        super(service.getAction(Constants.SET_LEFT_DOOR));

        try {
            setInput(Constants.NEW_LEFT_DOOR_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
