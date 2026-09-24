package mailbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientMain extends Application {

    private static Controller controller;
    private static Model model;
    public static Controller getController() { return controller;}

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/emailbox.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Client");
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller = loader.getController();
        model = new Model();
        controller.initModel(model,controller);
        Thread mainThreadClient = new Thread(()-> {
            boolean exit = false;
            while(!exit){
                if(Client.ServerIsUp()){
                    Client.getLogins(model,controller);
                    exit=true;
                    try { Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
                }else {
                    controller.makeAlert("ERRORE","SERVER DOWN");
                }
                try { Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
            }
            while(true) {
                if(Client.ServerIsUp()) {
                    if(model.getUser() != null) {
                        Client.refresh(model,controller);
                    }
                }else {
                    controller.makeAlert("ERRORE","SERVER DOWN");
                }
                try { Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        mainThreadClient.start();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
