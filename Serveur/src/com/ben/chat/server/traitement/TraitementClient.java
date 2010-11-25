package com.ben.chat.server.traitement;

import com.ben.chat.server.constants.ChatConstants;
import com.ben.chat.server.report.ReportList;
import com.ben.chat.server.report.ErrorLogger;
import com.ben.chat.server.*;
import com.ben.chat.userdata.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JFileChooser;
/**
 * classe qui met le client dans un thread
 * @author daccachb
 */
public class TraitementClient extends Thread implements Runnable {

    private Socket socket = null;
    private String nickname;
    private String username;
    private Serveur serveur;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private boolean connected = true;
    private User user=null; 

    /**
     * Le constructeur recupere le socket de transfert et appelle le constructeur de la
     * classe Thread.
     * @param socket socket de transfert
     */
    public TraitementClient(Socket socket, Serveur serveur) {
        super("TraitementClient");
        this.socket = socket;
        this.serveur = serveur;
    }
    /**
     * Affiche que le client s'est connecte sur le serveur
     * Cree un fluc d'entree et de sortie
     * Recoit le nom d'utilisateur
     * Met a jour la hashtable
     * et attend la reception de donnees sur le flux d'entree
     *
     */
    @Override
    public void run() {
        try {
            serveur.afficherConnected(socket);
            OutputStream out = socket.getOutputStream();
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            byte res = -1;
            username = receiveText();
            String encodedPassword = receiveText();
            /*do {
                username = receiveText();
                String password = receiveText();
                username = username.toLowerCase();
                res = serveur.getUsers(username, password);
                //res = serveur.updateHashtable(this, username);//retourne -1 si le nom d'utilisateur existe deja.
                sendByte(res);
            } while (res == ChatConstants.INVALID_USERNAME);*/

            res = serveur.getUsers(username, encodedPassword);
            sendByte(res);
            if(res==0){
                user  = serveur.getUser(username);
                nickname = user.getNickname();
                byte res2 = ChatConstants.INVALID_USERNAME;
                while(res2==ChatConstants.INVALID_USERNAME){
                    res2 = serveur.updateHashtable(this, nickname);
                    sendByte(res2);
                    if(res2==ChatConstants.INVALID_USERNAME)
                        nickname = receiveText();
                    else{
                        sendText(nickname);
                        serveur.updateHashtable(this, nickname);
                        serveur.setNickname(username,nickname );
                    }

               }

                sendConnectedStatusToAll();
                updateContactList();
                sendAllDirectories();
                sendUserInfo();
            }else{
                //res = serveur.updateHashtable(this, nickname);

                out.close();
                sortie.close();
                serveur.removeClient(this);
                connected = false;
            }

            while (connected) {
                int action = receiveInt();
                ForwardAction(action);
            }
        } catch (IOException e) {
            connected=false;
            serveur.removeClient(username ,nickname);
            serveur.afficherDisconnected(socket);
            sendDisconnectStatusToAll();
            updateContactList();

        }
    }

    public void sendAdministratorMessage(String msg) {
        sendInt(ChatConstants.ADMINISTRATOR_MESSAGE);
        sendText(msg);
    }

    public void disconnect(){
        try {
            TraitementClient.sleep(10000);
        } catch (InterruptedException ex) {ErrorLogger.getLogger().logError("disconection" ,ex);}
        try {
            socket.close();
        } catch (IOException ex) {ErrorLogger.getLogger().logError("disconnection",ex);}
    }

    /**
     * execute en fonction de la valeur de l'action la methode adequate
     * @param action entier de l'action:
     * 
     * @throws java.io.IOException
     */
    private void ForwardAction(int action) throws IOException {
        switch (action) {
            case ChatConstants.DISCONNECTION:
                serveur.removeClient(username, nickname);
                serveur.afficherDisconnected(socket);
                socket.close();
                break;
            case ChatConstants.PRIVATE_CONV:
                transfertPrivateConv();
                break;
            case ChatConstants.SEND_FILE_REQUEST:
                transfertRequestToSendFile();
                break;
            case ChatConstants.SEND_FILE_REQUEST_APPROVAL:
            case ChatConstants.SEND_FILE_REQUEST_DISAPPROVAL:
                transfertAnswerFromReceiver(action);
                break;
            case ChatConstants.CHANGE_USERNAME:
                changeUsername();
                break;
            case ChatConstants.PUBLIC_CONV:
                Hashtable arr = serveur.getHashtableOfUsers();
                int rgb = receiveInt();
                String text = receiveText();
                sendToAll(rgb,text, arr);
                break;
            case ChatConstants.SEND_DP:
                transfertDp();
                break;
            case ChatConstants.GET_USER_INFO:
                getUserInfo();
                break;
            case ChatConstants.GET_INFO_OF_USER:
                sendInfoOfUser();
                break;
            case ChatConstants.DOWNLOAD_DP_FROM_USER:
                receiveDP();
                break;
            case ChatConstants.REPORT:
                addReport();
                break;
            case ChatConstants.GET_FILE_INFO:
                getFileInfo();
                break;
            case ChatConstants.SHARE_SCREEN_REQUEST:
                transfertShareScreenRequest();
                break;
            case ChatConstants.SHARE_SCREEN_DISAPROVAL:
                transfertDisaproveToShareScreen();
                break;
            case ChatConstants.SHARE_SCREEN_APPROVAL:
                transfertApprovalToShareScreen();
                break;
            case ChatConstants.VOICE_REQUEST:
                transfertVoiceRequest();
                break;
            case ChatConstants.CALL_REFUSE:
                transfertCallRefuse();
                break;
            case ChatConstants.CALL_ACCEPTANCE:
                transfertCallAccept();
                break;
            case ChatConstants.VOICE_ID:
                transfertVoiceId();
                break;
            case ChatConstants.SCREEN_CONTROL_REQUEST:
                transfertScreenControlRequest();
                break;
            case ChatConstants.SCREEN_CONTROL_ACCEPT:
            case ChatConstants.SCREEN_CONTROL_REFUSE:
                transfertScreenControlAnswer(action);
                break;
            case ChatConstants.SCREEN_CONTROL_END:
                transfertScreenControlEnd();
                break;
            case ChatConstants.SOUND_CLIP:
                transfertSoundClip();
                break;
            case ChatConstants.PUBLIC_KEY_REQUEST:
                transfertPublicKeyRequest();
                break;
            case ChatConstants.PUBLIC_KEY:
                transfertPublicKey();
                break;
            case ChatConstants.SECURE_CONV:
                transfertSecureConv();
                break;


        }
    }

    /**
     * lit un String sur le flux d'entree
     * @return la valeur lue
     */
    public String receiveText() {
        String text = null;
        try {
            text = entree.readUTF();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while receiving data",ex);
        }
        return text;
    }

    /**
     * envoi un String sur le flux de sortie concatene avec le nom d'utilisateur de l'envoyeur ainsi que "said:"
     * @param msg message que l'on veut envoyer
     * @param user utilisateur qui a envoye le message
     */
    public void sendText(String msg, String user) {
        try {
            sortie.writeUTF(user);
            sortie.writeUTF(msg);
        } catch (IOException e) {
            ErrorLogger.getLogger().logError("Error while writing on stream",e);
        }
    }

    /**
     * ecrit un entier sur le flux de sortie
     * @param i entier a ecrire@
     */
    public void sendInt(int i) {
        try {
            sortie.writeInt(i);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing integer on stream",ex);
        }
    }

    /**
     * ecrit un string sur le flux de sortie
     * @param txt texte a ecrire
     */
    public void sendText(String txt) {
        try {
            sortie.writeUTF(txt);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing String on stream",ex);
        }
    }

    /**
     * envoi le text a tous les utilisateurs connectes sur le serveur
     * @param text text que l'on veut envoyer
     * @param userTable Hashtable des utilisateurs connectes
     */
    public void sendToAll(int RGB,String text, Hashtable userTable) {
        Enumeration en = userTable.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendInt(ChatConstants.PUBLIC_CONV);
            tr.sendInt(RGB);
            tr.sendText(text, nickname);
        }
    }

    private void addReport() {
            String type = receiveText();
            String report = receiveText();
            ReportList.getInstance().addReport(type, report);
            //ReportList.getInstance().writeToFileByType();
    }

    /**
     * change le nom d'utilisateur si le nouveau nom d'utilisateur n'existe pas et averti l'utilisateur dans les 2 cas
     *
     */
    private void changeUsername() {
        String newNickname = receiveText();
        Hashtable userTable = serveur.getHashtableOfUsers();
        TraitementClient res = (TraitementClient) userTable.get(newNickname);
        if (res == null) {//ok
            sendInt(ChatConstants.CHANGE_USERNAME);
            sendText(newNickname);
            String oldNickname = nickname;
            nickname = newNickname;
            serveur.changeUsername(username, oldNickname, newNickname, this);
            sendChangeUsernameToAll(oldNickname);
            updateContactList();
        } else {
            sendInt(ChatConstants.UNCHANGED_USERNAME);
        }
    }

    /**
     * vide le flux de sortie
     */
    private void flush() {
        try {
            sortie.flush();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while emptying the stream",ex);
        }
    }

    private void forwardFileInfo(String name,String type, long size, ImageIcon i) {
        sendInt(ChatConstants.GET_FILE_INFO);
        sendText(name);
        sendText(type);
        sendLong(size);
        sendObject(i);
    }

    private static Image iconToImage(Icon icon) {
          if (icon instanceof ImageIcon) {
              return ((ImageIcon)icon).getImage();
          } else {
              int w = icon.getIconWidth();
              int h = icon.getIconHeight();
              GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
              GraphicsDevice gd = ge.getDefaultScreenDevice();
              GraphicsConfiguration gc = gd.getDefaultConfiguration();
              BufferedImage image = gc.createCompatibleImage(w, h);
              Graphics2D g = image.createGraphics();
              icon.paintIcon(null, g, 0, 0);
              g.dispose();
              return image;
          }
      }

    private void getFileInfo() {
            JFileChooser chooser = new JFileChooser();
            String path = receiveText();
            File f = new File("../shared" + path);
            long size = f.length();
            String name = f.getName();
            Icon i = chooser.getIcon(f);
            String description = chooser.getTypeDescription(f);
            ImageIcon icon = new ImageIcon(iconToImage(i));
            forwardFileInfo(name, description, size, icon);
    }

    private void getUserInfo() {
        User newUser = (User)receiveObject();
        serveur.updateUserInfo(newUser, username);
    }

    private void receiveDP() {
        ImageIcon icon = (ImageIcon)receiveObject();
        serveur.setDP(username, icon);
    }

    private long receiveLong() {
        long l = 0;
        try {
            l = entree.readLong();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while reading long",ex);
        }
        return l;
    }

    /**
     * ecrit un byte sur le flux de sortie
     * @param size byte a ecrire
     */
    private void sendByte(byte size) {
        try {
            sortie.writeByte(size);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing byte on stream",ex);
        }
    }

    /**
     * envoi a l'utilisateur un message d'erreur
     * @param string texte decrivant l'erreur
     */
    public void sendError(String string) {
        sendInt(ChatConstants.ERROR);
        sendText(string);
    }

    /**
     * recoit un int sur le flux d'entree
     * @return le int lu
     * @throws java.io.IOException declenche si la lecture sur le flux est impossible
     */
    private int receiveInt() throws IOException {
        int action = 100;
        action = entree.readInt();
        return action;
    }

    /**
     * envoi l'action a tous les utilisateurs connectes sur le serveur
     * @param action entier qui definit l'action
     * @param table Hashtable des utilisateurs connectes sur le serveur
     * @throws java.io.IOException si l'ecriture est impossible sur le flux de sortie
     */
    private void sendActionToAll(int action, Hashtable table) throws IOException {
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendInt(action);
        }
    }

    /**
     * envoi la notification a tous les utilisateurs connectes comme quoi l'utilisateur a change de nom d'utilisateur
     * @param oldUsername ancien nom d'utilisateur
     */
    private void sendChangeUsernameToAll(String oldUsername) {
        Hashtable table = serveur.getHashtableOfUsers();
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {//on parcour tous les utilisateurs connectes
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendInt(ChatConstants.USER_CHANGED_USERNAME);
            tr.sendText(oldUsername);
            tr.sendText(this.nickname);
        }
    }

    /**
     * envoi la notification a tous les utilisateurs comme quoi l'utilisateur s'est connecte
     */
    private void sendConnectedStatusToAll() {
        Hashtable table = serveur.getHashtableOfUsers();
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendInt(ChatConstants.CONNECTED_USER);
            tr.sendText(this.nickname);
        }
    }

    /**
     * envoi la liste de contact a tous les utilisateurs connectes sur le serveur
     * @param table Hashtable des utilisateurs connectes sur le serveur
     * @throws java.io.IOException si l'ecriture est impossible sur le flux de sortie
     */
    private void sendContactToAll(Hashtable table) throws IOException {
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            Enumeration en2 = table.elements();
            while (en2.hasMoreElements()) {
                TraitementClient ct = (TraitementClient) en2.nextElement();
                tr.sendText(ct.nickname);
            }
        }
    }

    /**
     * envoi la notification aux utilisateurs connectes comme quoi l'utilisateur s'est deconnecte
     */
    private void sendDisconnectStatusToAll() {
        Hashtable table = serveur.getHashtableOfUsers();
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendInt(ChatConstants.DISCONNECTED_USER);
            tr.sendText(this.nickname);
        }
    }

    private void sendInfoOfUser() {
        String userNick = receiveText();
        TraitementClient tr =(TraitementClient) serveur.getHashtableOfUsers().get(userNick);
        User userRequested = serveur.getUser(tr.getUsername());
        sendInt(ChatConstants.SEND_REQUESTED_USER_INFO);
        sendText(userNick);
        sendObject(userRequested);

    }

    /**
     * envoi un long
     * @param l long  a envoyer
     */
    private void sendLong(long l) {
        try {
            sortie.writeLong(l);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while sending long",ex);
        }
    }

    private void sendObject(Object obj) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(sortie);
            oos.writeObject(obj);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while sending object",ex);
        } 
       
    }

    /**
     * envoi un message prive a un utilisateur donné
     * @param tr client a qui l'on veut envoyer le message
     * @param privateMessage message que l'on veut envoyer
     * @param user nom d'utilisateur qui a envoye le message
     */
    private void sendPrivateMessage(int RGB,TraitementClient tr, String privateMessage, String user,String convName) {
        tr.sendInt(ChatConstants.PRIVATE_CONV);
        tr.sendInt(RGB);
        tr.sendText(user);
        tr.sendText(convName);
        tr.sendText(privateMessage);
    }

    private void sendTab(byte[] b) {
        try {
            sortie.write(b);
            sortie.flush();
        } catch (IOException ex) {
            Logger.getLogger(TraitementClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendUserInfo() {
        sendInt(ChatConstants.SEND_USER_INFO);
        sendObject(user);
    }

    private void transfertCallAccept() {
        String caller = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(caller);
        tr.sendInt(ChatConstants.CALL_ACCEPTANCE);
        tr.sendText(nickname);
    }

    private void transfertCallRefuse() {
        String caller = receiveText();
        TraitementClient tr =(TraitementClient) serveur.getHashtableOfUsers().get(caller);
        tr.sendInt(ChatConstants.CALL_REFUSE);
        tr.sendText(nickname);
    }

    private void transfertDisaproveToShareScreen() {
        String destNickname = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(destNickname);
        tr.sendInt(ChatConstants.SHARE_SCREEN_DISAPROVAL);
        tr.sendText(this.nickname);
    }

    private void transfertDp() {
        String destUsername = receiveText();
        Object obj = receiveObject();
        serveur.setDP(username, (ImageIcon)obj);
        TraitementClient res = (TraitementClient) serveur.getHashtableOfUsers().get(destUsername);
        res.sendInt(ChatConstants.SEND_DP);
        res.sendText(this.nickname);
        res.sendObject(obj);
    }

    private Object receiveObject(){
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(entree);
            obj = ois.readObject(); 
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("Class not found",ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO",ex);
        }
        return obj;
    }

    /**
     * recupere le nom d'utilisateur du destinataire, le recupere das la Hashtable des utilisateurs et appelle la methode
     * sendPrivateMessage avec les arguements demandes 1 fois pour l'envoyeur et une fois pour le destinataire
     * de facon a ce que le message soit envoye aux deux
     */
    private void transfertPrivateConv() {
        try {
            int RGB  = receiveInt();
            String privateUsername = receiveText();
            TraitementClient sender, receiver;
            String privateMessage = receiveText();
            Hashtable table = serveur.getHashtableOfUsers();
            sender = (TraitementClient) table.get(nickname);
            table = null;
            table = serveur.getHashtableOfUsers();
            receiver = (TraitementClient) table.get(privateUsername);
            sendPrivateMessage(RGB,sender, privateMessage, nickname,privateUsername);
            sendPrivateMessage(RGB,receiver, privateMessage, nickname,nickname);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Client disconnected",e);
        }
    }

    /**
     * envoi une demande d'envoi de fichier avec le nom du fichier la taille le chemin ainsi
     * que le nom de l'utilisateur qui veut l'envoyer
     * @param user nom de l'utilisateur a qui la demande est destinee
     * @param size taille du fichier
     * @param fileName nom du fichier
     * @param path chemin absolu du fichier
     */
    private void sendRequestTransertFile(String user, long size, String fileName, String path) {
        Hashtable table = serveur.getHashtableOfUsers();
        TraitementClient c = (TraitementClient) table.get(user);
        c.sendInt(ChatConstants.SEND_FILE_REQUEST);
        c.sendText(this.nickname);
        c.sendLong(size);
        c.sendText(fileName);
        c.sendText(path);
    }

    private void transfertPublicKey() {
        String destusername = receiveText();
        Object obj = receiveObject();
        TraitementClient tr = (TraitementClient)serveur.getHashtableOfUsers().get(destusername);
        tr.sendInt(ChatConstants.PUBLIC_KEY);
        tr.sendText(this.nickname);
        tr.sendObject(obj);
    }

    private void transfertPublicKeyRequest() {
        String destusername = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(destusername);
        tr.sendInt(ChatConstants.PUBLIC_KEY_REQUEST);
        tr.sendText(this.nickname);
    }

    /**
     * recoit la demande d'envoi de fichier et la transfert vers l'utilisateur adequat
     */
    private void transfertRequestToSendFile() {
        String user = receiveText();
        long size = receiveLong();
        String fileName = receiveText();
        String path = receiveText();
        sendRequestTransertFile(user, size, fileName, path);
    }

    /**
     * envoi la taille de la liste de contact a tous les utilisateurs connectes sue le serveur
     * @param size taille de la liste de contact
     * @param table Hashtable des utilisateurs
     * @throws java.io.IOException si l'ecriture est impossible sur le flux de sortie
     */
    private void sendSizeOfContactListToAll(byte size, Hashtable table) throws IOException {
        Enumeration en = table.elements();
        while (en.hasMoreElements()) {
            TraitementClient tr = (TraitementClient) en.nextElement();
            tr.sendByte(size);
            tr.flush();
        }
    }

    private void transfertApprovalToShareScreen(){
        try {
            int id = receiveInt();
            String user = receiveText();
            Hashtable table = serveur.getHashtableOfUsers();
            TraitementClient tr = (TraitementClient) table.get(user);
            tr.sendInt(ChatConstants.SHARE_SCREEN_APPROVAL);
            tr.sendInt(id);
            tr.sendText(user);
        } catch (IOException ex) {
            Logger.getLogger(TraitementClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * transfert la reponse de demande d'envoi de fichier
     * @param action reponse du client:
     * 4:acceptation d'envoi
     * 5:refus d'envoi
     */
    private void transfertAnswerFromReceiver(int action) {
        if (action == ChatConstants.SEND_FILE_REQUEST_APPROVAL) {
            try {
                int id = receiveInt();
                String user = receiveText();
                String fileName = receiveText();
                String path = receiveText();
                Hashtable table = serveur.getHashtableOfUsers();
                TraitementClient t = (TraitementClient) table.get(user);
                t.sendInt(action);
                t.sendInt(id);
                t.sendText(this.nickname);
                t.sendText(fileName);
                t.sendText(path);
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("Transfert error",ex);
            }
        } else if (action == ChatConstants.SEND_FILE_REQUEST_DISAPPROVAL) {
            String user = receiveText();
            String fileName = receiveText();
            Hashtable table = serveur.getHashtableOfUsers();
            TraitementClient t = (TraitementClient) table.get(user);
            t.sendInt(action);
            t.sendText(this.nickname);
            t.sendText(fileName);
        }
    }

    private void transfertScreenControlAnswer(int action) {
        String nick = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(nick);
        tr.sendInt(action);
        tr.sendText(this.nickname);
    }

    private void transfertScreenControlEnd() {
        String nick = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(nick);
        tr.sendInt(ChatConstants.SCREEN_CONTROL_END);
        tr.sendText(this.nickname);
    }

    private void transfertScreenControlRequest() {
        String nick = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(nick);
        tr.sendInt(ChatConstants.SCREEN_CONTROL_REQUEST);
        tr.sendText(this.nickname);
    }

    private void transfertSecureConv() {
        String destUsername  = receiveText();
        String crypted = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(destUsername);
        tr.sendInt(ChatConstants.SECURE_CONV);
        tr.sendText(this.nickname);
        tr.sendText(crypted);
    }

    private void transfertShareScreenRequest() {
        String userDest = receiveText();
        TraitementClient tr =(TraitementClient) serveur.getHashtableOfUsers().get(userDest);
        tr.sendInt(ChatConstants.SHARE_SCREEN_REQUEST);
        tr.sendText(nickname);
    }

    private void transfertSoundClip() {

        try {
            String dest = receiveText();
            int size = receiveInt();
            byte[] b = new byte[size];
            entree.read(b);
            TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(dest);
            tr.sendInt(ChatConstants.SOUND_CLIP);
            tr.sendText(nickname);
            tr.sendInt(size);
            tr.sendTab(b);
        } catch (IOException ex) {
            Logger.getLogger(TraitementClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void transfertVoiceId() {
        try {
            String destusername = receiveText();
            int id = receiveInt();
            TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(destusername);
            tr.sendInt(ChatConstants.VOICE_ID);
            tr.sendInt(id);
        } catch (IOException ex) {
            Logger.getLogger(TraitementClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void transfertVoiceRequest() {
        String destUser = receiveText();
        TraitementClient tr = (TraitementClient) serveur.getHashtableOfUsers().get(destUser);
        tr.sendInt(ChatConstants.VOICE_REQUEST);
        tr.sendText(nickname);
    }

    /**
     * met a jour la Hashtable des utilisateurs connectes chaque fois qu'un utilisateur se connecte ou se deconnecte
     */
    private void updateContactList() {
        try {
            Hashtable table = serveur.getHashtableOfUsers();
            byte size = (byte) table.size();
            sendActionToAll(1, table);
            sendSizeOfContactListToAll(size, table);
            sendContactToAll(table);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while sending contactList",ex);
        }
    }

    /**
     * Envoi tous le contenu du repertoire shared a l'utilisateur courant
     */
    public void sendAllDirectories() {
        ArrayList all = serveur.makeArrayOfDirectory();
        sendInt(ChatConstants.LIST_DIRECTORY);
        sendInt(all.size());
        for (int i = 0; i < all.size(); i++) {
            String[] str = (String[]) all.get(i);
            sendInt(str.length);
            for (int j = 0; j < str.length; j++) {
                sendText(str[j]);
            }
        }
    }

    public String getUsername() {
        return this.nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    
    
}