package haitran.services;

import haitran.Constants;
import org.fourthline.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId(Constants.LIGHT_SWITCH),
        serviceType = @UpnpServiceType(value = Constants.LIGHT_SWITCH, version = 1)
)
public class LightSwitch {

    private final PropertyChangeSupport propertyChangeSupport;

    public LightSwitch() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0")
    private boolean leftSignal = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean rightSignal = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean headLight = false;

    @UpnpAction
    public void setLeftSignal(@UpnpInputArgument(name = Constants.NEW_LEFT_SIGNAL_VALUE) boolean newLeftSignalValue) {

        boolean leftSignalOldValue = leftSignal;
        leftSignal = newLeftSignalValue;
        getPropertyChangeSupport().firePropertyChange(Constants.LEFT_SIGNAL, null, null);
    }

    @UpnpAction
    public void setRightSignal(@UpnpInputArgument(name = Constants.NEW_RIGHT_SIGNAL_VALUE) boolean newRightSignalValue) {

        boolean rightSignalOldValue = rightSignal;
        rightSignal = newRightSignalValue;
        getPropertyChangeSupport().firePropertyChange(Constants.RIGHT_SIGNAL, null, null);
    }

    @UpnpAction
    public void setHeadLight(@UpnpInputArgument(name = Constants.NEW_HEAD_LIGHT_VALUE) boolean newHeadLightValue) {

        boolean headLightOldValue = headLight;
        headLight = newHeadLightValue;
        getPropertyChangeSupport().firePropertyChange(Constants.HEAD_LIGHT, null, null);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_LEFT_SIGNAL_VALUE))
    public boolean getLeftSignal() {
        return leftSignal;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_RIGHT_SIGNAL_VALUE))
    public boolean getRightSignal() {
        return rightSignal;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = Constants.RETURN_HEAD_LIGHT_VALUE))
    public boolean getHeadLight() {
        return headLight;
    }
}
