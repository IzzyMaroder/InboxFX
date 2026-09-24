package Creators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ScriviMail extends AnchorPane {

    @FXML
    private TextArea textAreaScriviMailDest;
    @FXML
    private TextArea textAreaScriviMailOggetto;
    @FXML
    private TextArea textAreaScriviMailTesto;
    @FXML
    private Button btn_scriviMailFXML_Invia;

    public TextArea getTextAreaScriviMailDest() {
        return textAreaScriviMailDest;
    }

    public TextArea getTextAreaScriviMailOggetto() {
        return textAreaScriviMailOggetto;
    }

    public TextArea getTextAreaScriviMailTesto() {
        return textAreaScriviMailTesto;
    }

    public Button getBtn_scriviMail_invia() {
        return btn_scriviMailFXML_Invia;
    }

    public ScriviMail() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../mailbox/fxml/scriviMail.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
