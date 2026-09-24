package mailbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mailbox.utils.Email;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

class ServerMethods {

   public static void sendMailtoClient(Socket incoming, String user) {
        ArrayList<String> mailList = new ArrayList<>();
        ArrayList<Email> emails = new ArrayList<>();
        String path = "src/mailbox/files/mailbox_"+user+".txt";
        try {
            BufferedReader reader = new BufferedReader((new FileReader(path)));
            String line = reader.readLine();
            mailList.add(line);
            while(line != null) {
                line = reader.readLine();
                mailList.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailList.remove(mailList.size()-1);
        int i = mailList.size()-1;
        for(int j = 0; j <= i; j++){
            String splitPart[] = mailList.get(j).split(";");
            int id = Integer.parseInt(splitPart[0]);
            String mittente = splitPart[1];
            String destinatari = splitPart[2];
            String oggetto = splitPart[3];
            String testo = splitPart[4];
            int state = Integer.parseInt(splitPart[5]);
            emails.add(new Email(id,mittente,destinatari,oggetto,testo,state));
        }
        try {
            ObjectOutputStream myOutputStream = new ObjectOutputStream(incoming.getOutputStream());
            myOutputStream.writeObject(emails);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getLogin(Socket incoming) {
        ArrayList<String> logins = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/mailbox/files/login.txt"));
            String line = reader.readLine();
            logins.add(line);
            while(line != null){
                line = reader.readLine();
                logins.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logins.remove(logins.size()-1);
        try {
            ObjectOutputStream myOutputStream = new ObjectOutputStream(incoming.getOutputStream());
            myOutputStream.writeObject(logins);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static synchronized void mailboxWork(String user, int action,  int ID, String Destinatari, String Oggetto, String Testo, Socket incoming){
        switch(action) {
            case 3:
                String mittente = user;
                String destinatari = Destinatari;
                String oggetto = Oggetto;
                String testo = Testo;
                String path = "src/mailbox/files/login.txt";
                ArrayList<String> userList = new ArrayList<>();
                try
                {
                    BufferedReader reader = new BufferedReader(new FileReader(path));
                    String line = reader.readLine();
                    userList.add(line);
                    while(line != null) {
                        line = reader.readLine();
                        userList.add(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                boolean ok = true;
                String[] dest = destinatari.split(",");
                for(String d: dest) {
                    if(!userList.contains(d)) {
                        ok = false;
                    }
                }
                if(ok) {
                    ArrayList<String> mailList = new ArrayList<>();
                    try {
                        BufferedReader reader = new BufferedReader((new FileReader("src/mailbox/files/mailbox_"+mittente+".txt")));
                        String line = reader.readLine();
                        mailList.add(line);
                        while(line != null) {
                            line = reader.readLine();
                            mailList.add(line);
                        }
                        reader.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    int lastIdMittente;
                    if(mailList.size() == 1) {//Significa solo email null
                        lastIdMittente = 0;
                    }else {
                        String lastMail = mailList.get(mailList.size()-2);
                        String[] t = lastMail.split(";");
                        lastIdMittente = Integer.parseInt(t[0]);
                    }
                    Email emailMittente = new Email(lastIdMittente+1,mittente,destinatari,oggetto,testo,2);
                    String insertEmail = emailMittente.toString();
                    BufferedWriter writerMittente;
                    try {
                        writerMittente = new BufferedWriter( new FileWriter("src/mailbox/files/mailbox_"+mittente+".txt",true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        if(lastIdMittente+1 == 1) {
                            writerMittente.write(insertEmail);
                        }else {
                            writerMittente.write("\n"+insertEmail);
                        }
                        writerMittente.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    for(String d:dest) {
                        ArrayList<String> mailBoxDest = new ArrayList<>();
                        try {
                            BufferedReader readerDest = new BufferedReader((new FileReader("src/mailbox/files/mailbox_"+d+".txt")));
                            String lineDest = readerDest.readLine();
                            mailBoxDest.add(lineDest);
                            while(lineDest != null) {
                                lineDest = readerDest.readLine();
                                mailBoxDest.add(lineDest);
                            }
                            readerDest.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        int lastIdDest;
                        if(mailBoxDest.size() == 1) {
                            lastIdDest = 0;
                        }else {
                            String lastMailDest = mailBoxDest.get(mailBoxDest.size()-2);
                            String[] temporaryDest = lastMailDest.split(";");
                            lastIdDest = Integer.parseInt(temporaryDest[0]);
                        }

                        Email emailDest = new Email(lastIdDest+1,mittente,destinatari,oggetto,testo,1);
                        String insertDest = emailDest.toString();

                        BufferedWriter writerDest;
                        try {
                            writerDest = new BufferedWriter( new FileWriter("src/mailbox/files/mailbox_"+d+".txt",true));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            if (lastIdDest + 1 == 1) {
                                writerDest.write(insertDest);
                            }else {
                                writerDest.write("\n"+insertDest);
                            }
                            writerDest.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    OutputStream outputStream;
                    try {
                        outputStream = incoming.getOutputStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    PrintWriter out = new PrintWriter(outputStream,true);
                    out.println("ok");

                }else {
                    OutputStream outputStream;
                    try {
                        outputStream = incoming.getOutputStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    PrintWriter out = new PrintWriter(outputStream, true);
                    out.println("ok");
                }
                break;
            case 4:
                String userName = user;
                int idToDelete = ID;
                ArrayList<String> mailList = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader((new FileReader("src/mailbox/files/mailbox_"+userName+".txt")));
                    String line = reader.readLine();
                    mailList.add(line);
                    while(line != null) {
                        line = reader.readLine();
                        mailList.add(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int length = mailList.size()-2;
                ArrayList<Email> email = new ArrayList<>();
                for(int i = 0; i <= length; i++) {
                    String[] part = mailList.get(i).split(";");
                    int id = Integer.parseInt(part[0]);
                    String mitt = part[1];
                    String desti = part[2];
                    String ogg = part[3];
                    String test = part[4];
                    int st = Integer.parseInt(part[5]);
                    if(id == idToDelete && (st == 1 || st == 2)) {
                        email.add(new Email(id,mitt,desti,ogg,test,3));
                    }else if(id == idToDelete && st == 3){
                        email.add(new Email(id,mitt,desti,ogg,test,4));
                    }else {
                        email.add(new Email(id,mitt,desti,ogg,test,st));
                    }
                }

                try {
                    BufferedWriter writer1 = new BufferedWriter(new FileWriter("src/mailbox/files/mailbox_"+userName+".txt", false));
                    writer1.write("");
                    writer1.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    BufferedWriter writer2 = new BufferedWriter(new FileWriter("src/mailbox/files/mailbox_"+userName+".txt", true));
                    for(int i = 0; i < email.size();i++){
                        if(i != email.size()-1){
                            writer2.write(email.get(i).toString()+"\n");
                        }else{
                            writer2.write(email.get(i).toString());
                        }
                    }
                    writer2.close();
                    OutputStream outputStream = null;
                    try{
                        outputStream = incoming.getOutputStream();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                    PrintWriter out = new PrintWriter(outputStream, true);
                    out.println("ok");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

}

class ThreadEchoHandler implements Runnable {
    private Socket incoming;
    private Parent root;
    public ThreadEchoHandler(Socket incoming, Parent root) {
        this.incoming = incoming;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            try{
                InputStream inStream =  incoming.getInputStream();
                Scanner in = new Scanner(inStream);
                String userLogin = in.nextLine();
                String a = in.nextLine();
                switch (a) {
                    case "1":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per aggiornamento logins\n");
                        ServerMethods.getLogin(incoming);
                        break;
                    case "2":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per aggiornamento MailBox\n");
                        ServerMethods.sendMailtoClient(incoming,userLogin);
                        break;
                    case "3":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per invio mail\n");
                        String destinatari = in.nextLine();
                        String oggetto = in.nextLine();
                        String testo = in.nextLine();
                        ServerMethods.mailboxWork(userLogin,3,-1,destinatari,oggetto,testo,incoming);
                        break;
                    case "4":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per eliminazione mail\n");
                        String idToDelete = in.nextLine();
                        ServerMethods.mailboxWork(userLogin, 4, Integer.parseInt(idToDelete),null, null, null, incoming);
                        break;
                    default:
                }
            }
            finally {
                incoming.close();
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
public class Server extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader ServerLoader = new FXMLLoader(getClass().getResource("fxml/server.fxml"));
        Parent root = ServerLoader.load();
        primaryStage.setTitle("Server");
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread mainThreadServer = new Thread(() -> {
           try {
               ServerSocket server = new ServerSocket(8189);
               while(true) {
                   Socket incoming = server.accept();
                   Runnable r = new ThreadEchoHandler(incoming, root);
                   Thread t = new Thread(r);
                   t.start();
               }
           }catch(IOException e){
               e.printStackTrace();
           }
        });
        mainThreadServer.start();
    }
}
