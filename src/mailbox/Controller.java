package mailbox;

import Creators.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import mailbox.utils.Email;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class Controller implements Initializable {

    private Model model;
    private Controller controller;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @FXML
    private SplitPane mySplitPane;
    @FXML
    private AnchorPane myAnchorPane1;
    @FXML
    private AnchorPane myAnchorPane2;
    @FXML
    private BorderPane myBorderPane;

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_ricevute;

    @FXML
    private Button btn_inviate;

    @FXML
    private Button btn_cestino;

    @FXML
    private Button btn_scrivi;

    @FXML
    private Text user_name;
    @FXML
    private ImageView image_icon;


    @FXML
    public void handlerBtnLogin(final ActionEvent event) {
        Login login = new Login();
        myBorderPane.setCenter(login);
        loginUtente(login);


    }
    public void handlerBtnRicevute(final ActionEvent event) {
        if(model.getUser() != null) {
            PostaInArrivo postaInArrivo = new PostaInArrivo();
            myBorderPane.setCenter(postaInArrivo);
            receivedMail(postaInArrivo);
        }
    }
    public void handlerBtnInviate(final ActionEvent event) {
        if (model.getUser() != null) {
            PostaInviata postaInviata = new PostaInviata();
            myBorderPane.setCenter(postaInviata);
            sendedMail(postaInviata);
        }
    }
    public void handlerBtnCestino(final ActionEvent event) {
        if (model.getUser() != null) {
            Cestino cestino = new Cestino();
            myBorderPane.setCenter(cestino);
            mailCestino(cestino);
        }
    }
    public void handlerBtnScrivi(final ActionEvent event) {
        if (model.getUser() != null) {
            ScriviMail scriviMail = new ScriviMail();
            myBorderPane.setCenter(scriviMail);
            scriviMail(scriviMail);
        }
    }


    public void loginUtente(Login login) {
        ListView ListViewLogin = login.getListViewLogin();
        ListViewLogin.setItems(model.getUserList());
        login.getBtn_loginFXML_OK().setOnAction(actionEvent -> {
            if(login.getListViewLogin().getSelectionModel().getSelectedItems() != null ) {
                String selectedUser = login.getListViewLogin().getSelectionModel().getSelectedItems().toString();
                selectedUser = selectedUser.replaceAll("[\\[\\]]",""); //ELIMINA "[" "]"
                model.setUser(selectedUser); //SETTO L'UTENTE
                model.getEmailListRcv().clear();
                model.getEmailListInviate().clear();
                model.getEmailistCestino().clear();
                login.getTextAreaLogin().setText("Loggato come "+ model.getUser());
                String Path = "src/mailbox/fxml/icon.png";
                image_icon.setImage(new Image(new File(Path).toURI().toString()));
                user_name.setText(model.getUser());
            }
        });
    }

    public void receivedMail(PostaInArrivo received) {
        ListView listViewPostaInArrivo = received.getListViewPostaInArrivo();
        TextArea textAreaPostaInArrivo = received.getTextAreaPostaInArrivo();
        Button btn_postaInArrivoFXML_Rispondi = received.getBtn_postaInArrivoFXML_Rispondi();
        Button btn_postaInArrivoFXML_RispondiATutti = received.getBtn_postaInArrivoFXML_RispondiATutti();
        Button btn_postaInArrivoFXML_Forward = received.getBtn_postaInArrivoFXML_Forward();
        Button btn_postaInArrivoFXML_Elimina = received.getBtn_postaInArrivoFXML_Elimina();

        btn_postaInArrivoFXML_Rispondi.setOnAction(actionEvent -> {
            if(model.getCurrentEmail() != null) {
                String mailToResp = model.getCurrentEmail().toString();
                rispondiMail(mailToResp);

            }
        });

        btn_postaInArrivoFXML_RispondiATutti.setOnAction(actionEvent -> {
            if(model.getCurrentEmail() != null){
                String mailToRespAll = model.getCurrentEmail().toString();
                rispondiAtutti(mailToRespAll);
            }
        });

        btn_postaInArrivoFXML_Forward.setOnAction(actionEvent -> {
            if(model.getCurrentEmail() != null) {
                String mailToForward = model.getCurrentEmail().toString();
                forward(mailToForward);
            }
        });

        btn_postaInArrivoFXML_Elimina.setOnAction(actionEvent -> {
            if(model.getCurrentEmail() != null) {
                String current = model.getCurrentEmail().toString();
                String[] parts = current.split(";");
                if(Client.ServerIsUp()) {
                    Client.deleteMail(model,controller,Integer.parseInt(parts[0]));
                    Client.refresh(model,controller);
                }else {
                    makeAlert("ERRORE","SERVER DOWN");
                }
            }
        });

        if(model.getUser() != null) {
            listViewPostaInArrivo.setItems(model.getEmailListRcv());
            listViewPostaInArrivo.setCellFactory(listView -> new ListCell<Email>(){
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email,empty);
                    if(empty) {
                        setText(null);
                    }else {
                        setText("Mittente: "+email.getMittente()+ "Oggetto: "+email.getOggetto());
                    }
                }
            });

            listViewPostaInArrivo.getSelectionModel().selectedItemProperty().addListener((obs, oldSection, newSelection) -> {
                model.setCurrentEmail((Email) newSelection);
                if(newSelection != null) {
                    Email email = model.getCurrentEmail();
                    String e[] = email.toString().split(";");
                    String mittente = e[1];
                    String destinatario = e[2];
                    String oggetto = e[3];
                    String testo = e[4];

                    textAreaPostaInArrivo.setText(
                            "Mittente:       " + mittente + "\n" +
                            "Destinatari:    " + destinatario + "\n" +
                            "Argomento:      " + oggetto + "\n" +
                            "Testo:          " + testo
                    );

                }
            });
        }
    }



    public void sendedMail(PostaInviata mailInviata) {
        ListView listViewpostaInviata = mailInviata.getListViewPostaInviata();
        TextArea textAreaPostaIniviata = mailInviata.getTextAreaPostaInviata();
        Button btn_postaInviata_Elimina = mailInviata.getBtn_posta_inviata_eliminata();
        btn_postaInviata_Elimina.setOnAction(actionEvent -> {
            if(model.getCurrentMailInviate() != null) {
                String currentEmail = model.getCurrentMailInviate().toString();
                String[] parts = currentEmail.split(";");
                if(Client.ServerIsUp()) {
                    Client.deleteMail(model, controller,Integer.parseInt(parts[0]));
                    Client.refresh(model, controller);
                }else {
                    makeAlert("ERRORE","SERVER DOWN");
                }
            }
        });

        if(model.getUser() != null) {
            listViewpostaInviata.setItems(model.getEmailListInviate());
            listViewpostaInviata.setCellFactory(listView -> new ListCell<Email>() {
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    if(empty) {
                        setText(null);
                    }else {
                        setText("Destinatario: "+  email.getDestinatario()+" Oggetto: "+email.getOggetto());
                    }
                }
            });

            listViewpostaInviata.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection, newSelecetion) -> {
                model.setCurrentMailInviata((Email) newSelecetion);
                if(newSelecetion != null) {
                    Email email = model.getCurrentMailInviate();
                    String[] mailcestiino = email.toString().split(";");
                    String mittente = mailcestiino[1];
                    String destinatari = mailcestiino[2];
                    String oggetto = mailcestiino[3];
                    String testo = mailcestiino[4];

                    textAreaPostaIniviata.setText(
                            "Mittente:       " + mittente + "\n" +
                            "Destinatari:    " + destinatari + "\n" +
                            "Argomento:      " + oggetto + "\n" +
                            "Testo:          " + testo
                    );
                }
            });
        }


    }

    public void mailCestino(Cestino cestino) {
        ListView listViewCestino = cestino.getListViewCestino();
        TextArea textAreaCestino = cestino.getTextAreaCestino();
        Button btn_Cestino = cestino.getBtn_cestino_elimina();

        btn_Cestino.setOnAction(actionEvent -> {
            if(model.getCurrentEmailces() != null){
                String current = model.getCurrentEmailces().toString();
                String[] parts = current.split(";");
                if(Client.ServerIsUp()) {
                    Client.deleteMail(model, controller,Integer.parseInt(parts[0]) );
                    Client.refresh(model,controller);
                }else {
                    makeAlert("ERRORE","SERVER DOWN");
                }
            }
        });

        if(model.getUser() != null) {
            listViewCestino.setItems(model.getEmailistCestino());
            listViewCestino.setCellFactory(listView -> new ListCell<Email>(){
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email,empty);
                    if(empty) {
                        setText(null);
                    }else {
                        setText("Mittente: " + email.getMittente()+ "Destinatario: " + email.getDestinatario() + " Oggetto: "+email.getOggetto());
                    }
                }
            });
            listViewCestino.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection,newSelection) -> {
                model.setCurrentEmailces((Email) newSelection);
                if(newSelection != null) {
                    Email mail = model.getCurrentEmailces();
                    String mails[] = mail.toString().split(";");
                    int id = Integer.parseInt(mails[0]);
                    String mittente = mails[1];
                    String destinatario = mails[2];
                    String oggetto = mails[3];
                    String testo = mails[4];

                    textAreaCestino.setText(
                            "Mittente:       " + mittente + "\n" +
                            "Destinatari:    " + destinatario + "\n" +
                            "Argomento:      " + oggetto + "\n" +
                            "Testo:          " + testo
                    );
                }
            });
        }
    }

    public void scriviMail(ScriviMail scrivimail) {
        TextArea getTextAreascriviMail_A = scrivimail.getTextAreaScriviMailDest();
        TextArea getTextAreascriviMail_Oggetto = scrivimail.getTextAreaScriviMailOggetto();
        TextArea getTextAreascriviMail_Testo = scrivimail.getTextAreaScriviMailTesto();
        Button getBtnscriviMail_Invia = scrivimail.getBtn_scriviMail_invia();

        getBtnscriviMail_Invia.setOnAction(actionEvent -> {
            String to = getTextAreascriviMail_A.getText();
            String oggetto = getTextAreascriviMail_Oggetto.getText();
            String text = getTextAreascriviMail_Testo.getText();

            if(model.getUser() != null) {
                String[] parts = to.split(",");
                boolean okuser = true;
                for (String part : parts) {
                    if (okuser) {
                        okuser = validate(part);
                    }
                }
                if(!to.isBlank() && oggetto != null && !oggetto.isBlank() && text != null && !text.isBlank()){
                    if(okuser) {
                        to = getTextAreascriviMail_A.getText();
                        oggetto = getTextAreascriviMail_Oggetto.getText();
                        text = getTextAreascriviMail_Testo.getText();

                        if(Client.ServerIsUp()) {
                            Client.sendMail(model, controller, to, oggetto, text);
                            Client.refresh(model,controller);
                        }else {
                            makeAlert("ERRORE","SERVER DOWN");
                        }
                    }else{
                        makeAlert("ERRORE IN SCRIVI MAIL", "VERIFICA I DESTINATARI!");
                    }
                }else{
                    makeAlert("ERRORE IN SCRIVI MAIL", "COMPILA TUTTI I CAMPI!");
                }
            }else {
                makeAlert("ERRORE IN SCRIVI MAIL", "UTENTE NON LOGGATO!");
            }
        });
    }

    public void rispondiMail(String mailToResp) {
        ScriviMail scrivi = new ScriviMail();
        myBorderPane.setCenter(scrivi);
        TextArea getTextAreaScriviMail_dest = scrivi.getTextAreaScriviMailDest();
        TextArea getTextScriviMail_ogg = scrivi.getTextAreaScriviMailOggetto();
        TextArea getTextScriviMail_Testo = scrivi.getTextAreaScriviMailTesto();
        Button getBtn_scriviMail_Invia = scrivi.getBtn_scriviMail_invia();

        String[] mailPart = mailToResp.split(";");
        getTextAreaScriviMail_dest.setText(mailPart[1]);
        getTextAreaScriviMail_dest.setEditable(false);
        getTextScriviMail_ogg.setText("Re: "+ mailPart[3]);
        getTextScriviMail_ogg.setEditable(false);

        getBtn_scriviMail_Invia.setOnAction(actionEvent -> {
            String text = getTextScriviMail_Testo.getText();
            if(model.getUser() != null) {
                String dest = null;
                String[] parts = mailPart[1].split(",");
                boolean check = true;
                for (String part : parts) {
                    if (check) {
                        check = validate(part);
                    }
                    if(!part.equals(model.getUser())){
                        dest = part;
                    }
                }
                if(dest != null && !mailPart[1].isBlank() && mailPart[3] != null && !mailPart[3].isBlank() && text != null && !text.isBlank()){
                    if(check){
                        if(Client.ServerIsUp()) {
                            Client.sendMail(model, controller, dest, "Re: "+mailPart[3], text);
                            Client.refresh(model, controller);
                        }else{
                            makeAlert("ERRORE","SERVER DOWN");
                        }
                    }else{
                        makeAlert("ERRORE IN RISPONDI", "VERIFICA TUTTI I CAMPI");
                    }
                }else {
                    makeAlert("ERRORE IN RISPONDI", "COMPILA TUTTI I CAMPI");
                }
            }else {
                makeAlert("ERRORE IN RISPONDI", "UTENTE NON LOGGATO");
            }
        });
    }

    public void rispondiAtutti(String mailToRespAll){
        ScriviMail scrivimail = new ScriviMail();
        myBorderPane.setCenter(scrivimail);
        TextArea getTextAreaScriviMail_dest = scrivimail.getTextAreaScriviMailDest();
        TextArea getTextAreaScriviMail_ogg = scrivimail.getTextAreaScriviMailOggetto();
        TextArea getTextAreaScriviMail_testo = scrivimail.getTextAreaScriviMailTesto();
        Button getBtn_scriviMail_Invia = scrivimail.getBtn_scriviMail_invia();

        String[] mailParted = mailToRespAll.split(";");
        String[] parts = mailParted[2].split(",");
        for(int element = 0; element < parts.length-1; element++){
            if(model.getUser().equals(parts[element])){
                parts[element] = parts[element + 1];
            }
        }
        mailParted[2] = parts[0];
        getTextAreaScriviMail_dest.setText(mailParted[1]+","+mailParted[2]);
        getTextAreaScriviMail_dest.setEditable(false);
        getTextAreaScriviMail_ogg.setText("Re: "+mailParted[3]);
        getTextAreaScriviMail_ogg.setEditable(false);

        getBtn_scriviMail_Invia.setOnAction(actionEvent -> {
            String text = getTextAreaScriviMail_testo.getText();
            if(model.getUser() != null){
                boolean check = true;
                for (String part : parts) {
                    if (check) {
                        check = validate(part);
                    }
                }
                if(mailParted[2] != null && !mailParted[2].isBlank() && mailParted[3] != null && !mailParted[3].isBlank() && text != null && !text.isBlank()){
                    if(check) {
                        if(Client.ServerIsUp()) {
                            Client.sendMail(model,controller,mailParted[1]+","+mailParted[2], "Re: "+mailParted[3], text);
                            Client.refresh(model, controller);
                        }else {
                            makeAlert("ERRORE IN RISPONDI A TUTTI","SERVER DOWN");
                        }
                    }else{
                        makeAlert("ERRORE IN RISPPONDI A TUTTI", "CONTROLLA I DESTINATARI");
                    }
                }else {
                    makeAlert("ERRORE IN RISPONDI A TUTTI", "COMPILA TUTTI I CAMPI");
                }
            }else {
                makeAlert("ERRORE IN RISPONDI A TUTTI", "ERRORE UTENTE NON LOGGATO");
            }
        });
    }

    public void forward(String mailToForward) {
        ScriviMail scriviMail = new ScriviMail();
        myBorderPane.setCenter(scriviMail);
        TextArea getTextAreaScriviMail_destinatario = scriviMail.getTextAreaScriviMailDest();
        TextArea getTextAreaScriviMail_Oggetto = scriviMail.getTextAreaScriviMailOggetto();
        TextArea getTextAreaScriviMail_Testo = scriviMail.getTextAreaScriviMailTesto();
        Button getBtnScriviMail_Invia = scriviMail.getBtn_scriviMail_invia();

        String[] mailParted = mailToForward.split(";");
        getTextAreaScriviMail_Oggetto.setText("Forward "+ mailParted[3]);
        getTextAreaScriviMail_Oggetto.setEditable(false);
        getTextAreaScriviMail_Testo.setText(mailParted[4]);
        getTextAreaScriviMail_Testo.setEditable(false);

        getBtnScriviMail_Invia.setOnAction(actionEvent -> {
            String destinatari = getTextAreaScriviMail_destinatario.getText();
            if(model.getUser() != null){
                String[] parts = destinatari.split(",");
                boolean check = true;
                for (String part : parts) {
                    if (check) {
                        check = validate(part);
                    }
                }
                if(!destinatari.isBlank() && mailParted[3] != null && !mailParted[3].isBlank() && mailParted[4] != null && !mailParted[4].isBlank()){
                    if(check){
                        if(Client.ServerIsUp()){
                            Client.sendMail(model, controller,destinatari,"Forward: "+mailParted[3], mailParted[4]);
                            Client.refresh(model,controller);
                        }else {
                            makeAlert("ERRORE IN FORWARD", "SERVER DOWN");
                        }
                    }else {
                        makeAlert("ERRORE IN FORWARD", "VERIFICA I DESTINATARI");
                    }
                }else {
                    makeAlert("ERRORE IN FORWARD", "COMPILA TUTTI I CAMPI");
                }
            }else {
                makeAlert("ERRORE IN FORWARD", "UTENTE NON ANCORA LOGGATO");
            }
        });
    }

    public void makeAlert(String titolo, String testo){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titolo);
            alert.setHeaderText(null);
            alert.setContentText(testo);
            alert.showAndWait();
        });
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initModel(Model model,Controller controller) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.controller = controller;
        this.model = model;
    }
}
