package haitran.controllers;

public interface BaseController {
    void leftSignal(boolean status);
    void rightSignal(boolean status);
    void light(boolean value);
    void openDoor(boolean status);

    void setEngineStatus(boolean status);
}
