package haitran.services;

import haitran.Constants;
import org.fourthline.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId(Constants.DOOR_SWITCH),
        serviceType = @UpnpServiceType(value = Constants.DOOR_SWITCH, version = 1)
)
public class DoorSwitch {

    private final PropertyChangeSupport propertyChangeSupport;

    public DoorSwitch() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0")
    private boolean leftDoor = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean rightDoor = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean hood = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean trunk = false;

    @UpnpAction
    public void setLeftDoor(@UpnpInputArgument(name = Constants.NEW_LEFT_DOOR_VALUE) boolean newLeftDoorValue) {
        boolean leftDoorOldValue = leftDoor;
        leftDoor = newLeftDoorValue;
        getPropertyChangeSupport().firePropertyChange(Constants.LEFT_DOOR, leftDoorOldValue, newLeftDoorValue);
    }

    @UpnpAction
    public void setRightDoor(@UpnpInputArgument(name = Constants.NEW_RIGHT_DOOR_VALUE) boolean newRightDoorValue) {
        boolean rightDoorOldValue = rightDoor;
        rightDoor = newRightDoorValue;
        getPropertyChangeSupport().firePropertyChange(Constants.RIGHT_DOOR, rightDoorOldValue, newRightDoorValue);
    }

    @UpnpAction
    public void setHood(@UpnpInputArgument(name = Constants.NEW_HOOD_VALUE) boolean newHoodValue) {
        boolean hoodOldValue = hood;
        hood = newHoodValue;
        getPropertyChangeSupport().firePropertyChange(Constants.HOOD, hoodOldValue, newHoodValue);
    }

    @UpnpAction
    public void setTrunk(@UpnpInputArgument(name = Constants.NEW_TRUNK_VALUE) boolean newTrunkValue) {
        boolean trunkOldValue = trunk;
        trunk = newTrunkValue;
        getPropertyChangeSupport().firePropertyChange(Constants.TRUNK, trunkOldValue, newTrunkValue);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_LEFT_DOOR_VALUE))
    public boolean getLeftDoor() {
        return leftDoor;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_RIGHT_DOOR_VALUE))
    public boolean getRightDoor() {
        return rightDoor;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_HOOD_VALUE))
    public boolean getHood() {
        return hood;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_TRUNK_VALUE))
    public boolean getTrunk() {
        return trunk;
    }
}
