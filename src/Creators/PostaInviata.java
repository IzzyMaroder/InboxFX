package Creators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PostaInviata extends AnchorPane {
    @FXML
    private ListView listViewPostaInviata;
    @FXML
    private TextArea textAreaPostaInviata;
    @FXML
    private Button btn_postaInviataFXML_Elimina;

    public ListView getListViewPostaInviata() {
        return listViewPostaInviata;
    }

    public TextArea getTextAreaPostaInviata() {
        return textAreaPostaInviata;
    }

    public Button getBtn_posta_inviata_eliminata() {
        return btn_postaInviataFXML_Elimina;
    }

    public PostaInviata() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../mailbox/fxml/postaInviata.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
