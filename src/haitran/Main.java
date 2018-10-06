package haitran;

import haitran.controllers.BaseController;
import haitran.controllers.ControllerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../resources/sample.fxml"));

        primaryStage.setTitle("Car Remote");
        primaryStage.setScene(new Scene(root, 1920, 870));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}