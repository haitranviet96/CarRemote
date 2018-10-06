package haitran.services;

import org.fourthline.cling.binding.annotations.*;
import raijin.audio_system.Constants;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId(Constants.SWITCH_POWER),
        serviceType = @UpnpServiceType(value = Constants.SWITCH_POWER, version = 1)
)
public class SwitchPower {

    private final PropertyChangeSupport propertyChangeSupport;

    public SwitchPower() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private boolean target = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = Constants.NEW_TARGET_VALUE) boolean newTargetValue) {

        boolean targetOldValue = target;
        target = newTargetValue;
        boolean statusOldValue = status;
        status = newTargetValue;
        getPropertyChangeSupport().firePropertyChange(Constants.STATUS, null, null);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RET_TARGET_VALUE))
    public boolean getTarget() {
        return target;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RESULT_STATUS))
    public boolean getStatus() {
        return status;
    }
}
