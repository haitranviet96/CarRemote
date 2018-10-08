package haitran.controllers;

public interface BaseController {
    void changeEngineButton(boolean newStatus);

    void changeLeftSignal(boolean newStatus);
    void changeRightSignal(boolean newStatus);
    void changeHeadLightSignal(boolean newStatus);

    void changeLeftDoorStatus(boolean newStatus);
    void changeRightDoorStatus(boolean newStatus);
    void changeHoodStatus(boolean newStatus);
    void changeTrunkStatus(boolean newStatus);
}
