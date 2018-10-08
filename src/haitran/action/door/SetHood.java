package haitran.action.door;

import haitran.Constants;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetHood extends ActionInvocation {
    public SetHood(Service service, boolean status) {
        super(service.getAction(Constants.SET_HOOD));

        try {
            setInput(Constants.NEW_HOOD_VALUE, status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
