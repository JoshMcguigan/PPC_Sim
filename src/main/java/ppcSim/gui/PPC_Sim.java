package ppcSim.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PPC_Sim extends Application {

    Controller controller;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/Views/View.fxml"
                )
        );


        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("PPC Sim");
        stage.setScene(scene);
        stage.show();

        controller = loader.<Controller>getController();
    }

    @Override
    public void stop() throws InterruptedException {
        controller.shutdown();
        Thread.sleep(1000);
        Platform.exit();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}