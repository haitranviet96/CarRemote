package haitran.controllers;

public interface BaseController {
    void onChangeLeftSignal();
    void onChangeRightSignal();
    void onChangeHeadLight();
    void openDoor(boolean status);

    void onChangeEngineStatus();
}
