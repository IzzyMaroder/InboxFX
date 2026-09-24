package mailbox;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import mailbox.utils.Email;

import java.util.ArrayList;
import java.util.List;

public class Model {

    List<Email> email = new ArrayList<>();
    ObservableList<Email> emailList = FXCollections.observableArrayList(email);

    public ObservableList<Email> getEmailListRcv() {
        return emailList;
    }

    public void setEmailList(ObservableList<Email> emailList) {
        this.emailList = emailList;
    }

    public ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);


    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail;
    }

    public Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public void setCurrentEmail(Email currentEmail) {
        this.currentEmailProperty().set(currentEmail);
    }






    List<Email> emailinvitate = new ArrayList<>();
    ObservableList<Email> emailListInviate = FXCollections.observableArrayList(emailinvitate);

    public ObservableList<Email> getEmailListInviate() {
        return emailListInviate;
    }

    public void setEmailListInviate(ObservableList<Email> emailListInviate) {
        this.emailListInviate = emailListInviate;
    }

    public ObjectProperty<Email> currentMailInviata = new SimpleObjectProperty<>(null);
    public ObjectProperty<Email> currentMailInviataProperty() {
        return currentMailInviata;
    }

    public Email getCurrentMailInviate() {
        return currentMailInviata.get();
    }

    public void setCurrentMailInviata(Email currentMailInviata) {
        this.currentMailInviata.set(currentMailInviata);
    }




    List<Email> emailCest = new ArrayList<>();
    ObservableList<Email> emailistCestino = FXCollections.observableArrayList(emailCest);

    public ObservableList<Email> getEmailistCestino() {
        return emailistCestino;
    }

    public void setEmailistCestino(ObservableList<Email> emailistCestino) {
        this.emailistCestino = emailistCestino;
    }

    public ObjectProperty<Email> currentEmailces = new SimpleObjectProperty<>(null);



    public ObjectProperty<Email> currentEmailcesProperty() {
        return currentEmailces;
    }

    public Email getCurrentEmailces() {
        return currentEmailcesProperty().get();
    }

    public void setCurrentEmailces(Email currentEmailces) {
        currentEmailcesProperty().set(currentEmailces);
    }





    List<String> users = new ArrayList<>();
    ObservableList<String> userList = FXCollections.observableArrayList(users);
    public ObservableList<String> getUserList(){ return userList; }

    public ObjectProperty<String> currentUsr = new SimpleObjectProperty<>(null);
    public ObjectProperty<String> currentlyUsrProperty() { return  currentUsr; }
    public String getUser() { return currentlyUsrProperty().get(); }
    public void setUser(String usr) { currentlyUsrProperty().set(usr); }

}
