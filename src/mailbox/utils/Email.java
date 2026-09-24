package mailbox.utils;

import java.io.Serializable;

public class Email extends Object implements Serializable {

    private int id;
    private String mittente;
    private String destinatario;
    private String testo;
    private String oggetto;
    private int state;

    public Email(int id, String mittente,String destinatario, String oggetto, String testo, int state) {
        this.id = id;
        this.oggetto = oggetto;
        this.destinatario = destinatario;
        this.mittente = mittente;
        this.testo = testo;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String toString(){
        return "" + getId() + ";" + getMittente() + ";" + getDestinatario() + ";" + getOggetto() +";" + getTesto() + ";" + getState();
    }
}
