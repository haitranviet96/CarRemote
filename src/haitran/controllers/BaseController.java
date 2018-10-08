package haitran.controllers;

public interface BaseController {
    void onChangeEngineStatus();

    void onChangeLeftSignal();
    void onChangeRightSignal();
    void onChangeHeadLight();

    void onChangeLeftDoorStatus();
    void onChangeRightDoorStatus();
    void onChangeHoodStatus();
    void onChangeTrunkStatus();
}
