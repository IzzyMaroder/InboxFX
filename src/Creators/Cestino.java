package Creators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Cestino extends AnchorPane {
    @FXML
    private ListView listViewCestino;
    @FXML
    private TextArea textAreaCestino;
    @FXML
    private Button btn_cestinoFXML_Elimina;

    public ListView getListViewCestino() {
        return listViewCestino;
    }

    public TextArea getTextAreaCestino() {
        return textAreaCestino;
    }

    public Button getBtn_cestino_elimina() {
        return btn_cestinoFXML_Elimina;
    }

    public Cestino() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../mailbox/fxml/cestino.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
