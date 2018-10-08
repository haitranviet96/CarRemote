package haitran;

import org.fourthline.cling.model.meta.Device;

public class Car {
    private Device device;

    private boolean ENGINE_STATUS = false;

    private boolean LEFT_SIGNAL_STATUS = false;
    private boolean RIGHT_SIGNAL_STATUS = false;
    private boolean HEAD_LIGHT_STATUS = false;

    private boolean LEFT_DOOR_STATUS = false;
    private boolean RIGHT_DOOR_STATUS = false;
    private boolean HOOD_STATUS = false;
    private boolean TRUNK_STATUS = false;

    private Car() {
    }

    private static Car instance = new Car();

    public static Car getInstace() {
        return instance;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean getEngineStatus() {
        return ENGINE_STATUS;
    }

    public void setEngineStatus(boolean engineStatus) {
        ENGINE_STATUS = engineStatus;
    }

    public boolean getLeftSignalStatus() {
        return LEFT_SIGNAL_STATUS;
    }

    public void setLeftSignalStatus(boolean leftSignalStatus) {
        LEFT_SIGNAL_STATUS = leftSignalStatus;
    }

    public boolean getRightSignalStatus() {
        return RIGHT_SIGNAL_STATUS;
    }

    public void setRightSignalStatus(boolean rightSignalStatus) {
        RIGHT_SIGNAL_STATUS = rightSignalStatus;
    }

    public boolean getHeadLightStatus() {
        return HEAD_LIGHT_STATUS;
    }

    public void setHeadLightStatus(boolean headLightStatus) {
        HEAD_LIGHT_STATUS = headLightStatus;
    }

    public boolean getLeftDoorStatus() {
        return LEFT_DOOR_STATUS;
    }

    public void setLeftDoorStatus(boolean leftDoorStatus) {
        LEFT_DOOR_STATUS = leftDoorStatus;
    }

    public boolean getRightDoorStatus() {
        return RIGHT_DOOR_STATUS;
    }

    public void setRightDoorStatus(boolean rightDoorStatus) {
        RIGHT_DOOR_STATUS = rightDoorStatus;
    }

    public boolean getHoodStatus() {
        return HOOD_STATUS;
    }

    public void setHoodStatus(boolean hoodStatus) {
        HOOD_STATUS = hoodStatus;
    }

    public boolean getTrunkStatus() {
        return TRUNK_STATUS;
    }

    public void setTrunkStatus(boolean trunkStatus) {
        TRUNK_STATUS = trunkStatus;
    }
}
