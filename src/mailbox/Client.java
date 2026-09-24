package mailbox;

import javafx.application.Platform;
import mailbox.utils.Email;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public static void sendMail(Model model, Controller controller, String dest, String oggetto, String testo) {
        try {
            System.out.println("[Client] Invio mail");
            String host = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(host, 8189);
            try{

                OutputStream outputStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println(model.getUser());
                out.println("3");
                out.println(dest);
                out.println(oggetto);
                out.println(testo);

                InputStream inputStream = s.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                String received = scanner.nextLine();
                if(received.equals("ok")) {
                    controller.makeAlert("SUCCESSO","MAIL INVIATA CON SUCCESSO");
                }else {
                    controller.makeAlert("ERRORE","ERRORE INVIO MAIL");
                }
            }finally{
                s.close();
            }


        } catch (IOException e) {
           e.printStackTrace();
        }

    }

    public static void deleteMail(Model model, Controller controller,int id){
        try {
            System.out.println("[Client] Cancellazione mail");
            String host = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(host,8189);
            try {
                OutputStream outputStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println(model.getUser());
                out.println("4");
                out.println(id);

                InputStream inputStream = s.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                String received = scanner.nextLine();
                if(received.equals("ok")) {
                    controller.makeAlert("SUCCESSO","EMAIL CANCELLATA CON SUCCESSO!");
                }else {
                    controller.makeAlert("ERRORE","ERRORE NELLA CANCELLAZIONE DELLA MAIL!");
                }

            }finally {
                s.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void refresh(Model model, Controller controller) {
        ArrayList<Email> mailServer = new ArrayList<>();
        try {
            System.out.println("[Client] Richiedo gli aggiornamenti al Server");
            String host = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(host, 8189);
            OutputStream outputStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            out.println(model.getUser());
            out.println("2");

            ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
            try {
                mailServer = ((ArrayList<Email>)inStream.readObject());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }finally {
                s.close();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Email> emailRicevute = new ArrayList<>();
        ArrayList<Email> emailInviate = new ArrayList<>();
        ArrayList<Email> emailCancellate = new ArrayList<>();
        for(Email email : mailServer ) {
            int state = email.getState();
            switch(state){
                case 1:
                    emailRicevute.add(email);
                    break;
                case 2:
                    emailInviate.add(email);
                    break;
                case 3:
                    emailCancellate.add(email);
                    break;
            }
        }
        int lengthNewMailRicevute = emailRicevute.size();
        int lengthOldMailRicevute = model.getEmailListRcv().size();
        if(lengthNewMailRicevute != 0){
            if(lengthNewMailRicevute > lengthOldMailRicevute){
                controller.makeAlert("HEYY", "HAI RICEVUTO UNA NUOVA MAIL!");

            }else if(lengthNewMailRicevute < lengthOldMailRicevute){
                /* Potrebbero non esserci mail nuove. Oppure sono state cancellate delle mail il
                 * che la lunghezza dell'array è rimasta invariata */
                String lastNewMail = emailRicevute.get(lengthNewMailRicevute - 1).toString();
                boolean ok = true;
                for(Email email : model.getEmailListRcv()){
                    if(email.toString().equals(lastNewMail)){
                        ok = false;
                    }
                }
                System.out.println(ok);
                if(ok){
                    controller.makeAlert("HEYY", "HAI RICEVUTO UNA  NUOVA MAIL!");
                }
            }else {
                if( !(emailRicevute.get(lengthNewMailRicevute-1).toString().equals(model.getEmailListRcv().get(lengthNewMailRicevute-1).toString())) ){
                    controller.makeAlert("HEYY", "HAI RICEVUTO UNA  NUOVA MAIL!");
                }
            }
        }

        Platform.runLater(() -> {
            model.getEmailListRcv().setAll(emailRicevute);
            model.getEmailListInviate().setAll(emailInviate);
            model.getEmailistCestino().setAll(emailCancellate);
        });
    }



    public static void getLogins(Model model,Controller controller) {
        try {
            System.out.println("[Client] Richiedo i login al Server");
            String host = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(host, 8189);
            OutputStream outputStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            out.println("null");
            out.println("1");
            ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
            ArrayList<String> logins = new ArrayList<>();

            try {
                logins = ((ArrayList<String>)inStream.readObject());
                ArrayList<String> finalLogins = logins;
                Platform.runLater(() -> model.getUserList().setAll(finalLogins));
            }catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                s.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean ServerIsUp() {
        try {
            System.out.println("[Client] Server is Up");
            String host = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(host, 8189);
            OutputStream outputStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            out.println("null");
            out.println("null");

            return true;
        }catch(IOException e) {
            System.out.println("Server Down");
            return false;
        }
    }



}
