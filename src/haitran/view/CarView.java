package haitran.view;

import haitran.controllers.BaseController;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CarView implements Initializable {
    private BaseController controller;

    public CarView(BaseController controller){
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
