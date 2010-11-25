package com.ben.chat.server;

import com.ben.chat.server.constants.ChatConstants;
import com.ben.chat.server.report.ReportList;
import com.ben.chat.server.report.ErrorLogger;
import com.ben.chat.server.traitement.TraitementFichier;
import com.ben.chat.server.traitement.TraitementClient;
import com.ben.chat.server.traitement.TraitementVoice;
import com.ben.chat.server.traitement.TraitementShareScreen;
import com.ben.chat.server.traitement.TraitementScreenControl;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import com.ben.chat.userdata.*;

/**
 * Classe principale qui ecoute sur un port donne et attend les connexions de clients
 */
public class Serveur {

    private Hashtable userTable = new Hashtable();
    private Hashtable fileTransfertTable = new Hashtable();
    private Hashtable voiceTable = new Hashtable();
    private Hashtable shareScreenTable = new Hashtable();
    private Hashtable<String, TraitementScreenControl> sharecontrol = new Hashtable<String, TraitementScreenControl>();
    private int numPort;
    private boolean listening = true;
    private int fileConnectionId = 1;
    private int shareConnectionid= 1;
    private int voiceConnectionid= 1;
    private com.ben.chat.userdata.UserDataInterface userData;

    /**
     * le constructeur du Serveur crée un Socket sur le port donné en argument
     * et attend qu'un client se connecte.
     * @param numPort Numero de port sur lequel on veut lancer le Serveur.
     */
    public Serveur(int numPort) {
        new ErrorLogger().log("Server launched and running");
        new ReportList();
        this.numPort = numPort;
        initFolders();
        initUsersList();
        try {
            ServerSocket socketEcoute = new ServerSocket(this.numPort);
            afficheWelcome(numPort);
            new ThreadCommande(this).start();
            while (listening) {
                Socket socket = socketEcoute.accept();
                InputStream in = socket.getInputStream();
                DataInputStream entree = new DataInputStream(in);
                int typeConnection = entree.readInt();
                switch (typeConnection) {
                    case ChatConstants.CHAT_CONNECTION:
                        TraitementClient traite = new TraitementClient(socket, this);
                        userTable.put(traite, "");
                        traite.start();
                        break;
                    case ChatConstants.FILE_CONNECTION:
                        TraitementFichier tf = new TraitementFichier(socket, this, fileConnectionId);
                        tf.start();
                        fileTransfertTable.put(fileConnectionId, tf);
                        fileConnectionId++;
                        break;
                    case ChatConstants.VOICE_CONNECTION:
                        TraitementVoice tv = new TraitementVoice(socket, this,voiceConnectionid);
                        tv.start();
                        voiceTable.put(voiceConnectionid, tv);
                        voiceConnectionid++;
                        break;
                    case ChatConstants.SCREEN_SHARING_CONNECTION:
                        TraitementShareScreen tss = new TraitementShareScreen(socket, this, shareConnectionid);
                        tss.start();
                        shareScreenTable.put(shareConnectionid, tss);
                        shareConnectionid++;
                        break;
                    case ChatConstants.SCREEN_CONTROL_CONNECTION:
                        String nickname = entree.readUTF();
                        TraitementScreenControl tsc = new TraitementScreenControl(socket, this);
                        tsc.start();
                        sharecontrol.put(nickname, tsc);
                        break;
                }
            }
        } catch (IOException e) {
            ErrorLogger.getLogger().logError("Error while creating socket", e);
        }
    }

    /**
     * la fonction main appelle le constructeur du Serveur
     * Elle verifie aussi que le nombre d'arguments est le bon
     * @param argv port sur lequel on lance le serveur.
     */
    public static void main(String[] argv) {
        if (argv.length == 1)
            if (Integer.parseInt(argv[0]) > 65536 || Integer.parseInt(argv[0]) < 1024)
                System.out.println("Invalid port");
            else
                new Serveur(Integer.parseInt(argv[0]));
        else
            System.out.println("Port missing");
    }

    /**
     * retourne le hashtable des utilisateurs connectés à la classe traitementClient.
     * @return HashTable des utilisateurs connectés
     */
    public Hashtable getHashtableOfUsers() {
        return userTable;
    }

    /**
     * retourne le hashtable des utilisateurs ayant ouvert une connexion pour transfert de fichier
     * @return HashTable des utilisateurs connectés
     */
    public Hashtable getHashtableOfFileConnection() {
        return fileTransfertTable;
    }

    public Hashtable getHashtableOfScreenSharing() {
        return shareScreenTable;
    }

    /**
     * met a jour la table des utilisateurs connectes, cette methode est appellée par la classe TraitementClient
     * lorsque celle-ci recoit le nom d'utilisateur du client connecté.
     * @param tr Le Thread du client
     * @param username le nom d'utilisateur du client
     * @return le code de succes ou d'echec
     */
    public synchronized byte updateHashtable(TraitementClient tr, String nickname) {
        userTable.remove(tr);//on efface le traitementClient qui etait en cle
        if (userTable.get(nickname) == null) {//on regarde si le nom d'utilisateur existe deja
            userTable.put(nickname, tr);//si il existe pas on place dans la hashtable le nom d'utlisateur en cle et le Traitementclient en objet
            return 1;//on revoi 1 si l'operation est un succes
        } else
            return -1;//-1 sinon
    }

    /**
     * Change le nom d'utilisateur dans la hashtable des utilisateurs
     * @param username ancien nom d'utilisateur
     * @param newUsername nouveau nom d'utilisateur
     * @param tr TraitementClient du client que l'on veut modifier
     */
    public synchronized void changeUsername(String username, String Oldnickname, String newNickname, TraitementClient tr) {
        userTable.remove(Oldnickname);
        userTable.put(newNickname, tr);
        userData.updateUsername(username, newNickname);
    }

    /**
     * enleve un client de la hashTable losque ce dernier des deconnecte.
     * @param username nom d'utilisateur du client qui se deconnecte.
     */
    public void removeClient(String username, String nickname) {
        try {
            userData.setConnected(username, false);
            userTable.remove(nickname);
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("NullPointer", e);
        }
    }

    public void removeClient(TraitementClient tr) {
        userTable.remove(tr);
    }

    /**
     * affiche sur le serveur la deconnexion du client avec son @ IP et le port.
     *
     * @param socketTransfert socket du client qui s'est deconnecte
     */
    public void afficherDisconnected(Socket socketTransfert) {
        InetAddress ipClient = socketTransfert.getInetAddress();
        int portClient = socketTransfert.getPort();
        System.out.println("\r\rClient disconnected...");
        System.out.print("IP : " + ipClient + " PORT : " + portClient + "\n>>");
        ErrorLogger.getLogger().log("Client disconnected...\n" + "IP : " + ipClient + " PORT : " + portClient);
    }

    /**
     * affiche la connexion d'un nouveau client sur le serveur
     * @param socketTransfert socket du client qui s'est connecte
     */
    public void afficherConnected(Socket socketTransfert) {
        InetAddress ipClient = socketTransfert.getInetAddress();
        int portClient = socketTransfert.getPort();
        System.out.println("\r\rClient connected...");
        System.out.print("IP : " + ipClient + " PORT : " + portClient + "\n>>");
        ErrorLogger.getLogger().log("Client Connected\nIp " + ipClient + " Port " + portClient);
    }

    /**
     * Lit un fichier et le met dans un tableau d'octets
     * @param f nom du fichier a ouvrir
     * @return le tableau d'octets
     */
    public byte[] loadFile(File f) {
        byte[] tabFile = null;
        try {
            FileInputStream inF = new FileInputStream(f);
            int size = (int) f.length();
            tabFile = new byte[size];
            inF.read(tabFile);
            inF.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while reading file", ex);
        }
        return tabFile;
    }

    public void sendAllDirectories() {
        Enumeration e = userTable.elements();
        while (e.hasMoreElements()) {
            TraitementClient t = (TraitementClient) e.nextElement();
            t.sendAllDirectories();
        }
    }

    public void removeFileConnectionFromHashtable(int connectionId) {
        fileTransfertTable.remove(connectionId);
    }

    public Hashtable getHashtableOfVoice() {
        return this.voiceTable;
    }

    public byte getUsers(String username, String password) {
        try {
            User hashdata = userData.getList().get(username);
            if (hashdata.getPassword().equals(password)) {
                if (!hashdata.isConnected()) {
                    hashdata.setConnected(true);
                    return 0;
                }
                return -2;
            } else
                return -1;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public void kickUser(String user, String cause) {
        try {
            TraitementClient t = (TraitementClient) userTable.get(user);
            t.sendAdministratorMessage("you'll be disconnected by admin in 10s :\n" + cause);
            t.disconnect();
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Cannot send message, user is not connected...", e);
        }
    }

    public String getNickname(String username) {
        try{
            return userData.getList().get(username).getNickname();
        }catch(NullPointerException e){System.out.println("unknown user");}
        return null;
    }

    public void addUser(String name, String surname, String username, String pass, String nickname, String mail, char sex, String day, String month, String year) {
        userData.addUser(name, surname, username, pass, nickname, mail, sex, day, month, year);
    }

    public Hashtable<String,TraitementScreenControl> getHashtableOfScreenControl() {
        return sharecontrol;

    }

    public User getUser(String username) {
        return userData.getUser(username);
    }

    public void removeUser(String username) {
        try{
            userData.removeUser(username);
        }catch(NullPointerException e){System.out.println("unknown user");}
    }

    public void removeShareConnectionFromHashtable(int connectionId) {
        shareScreenTable.remove(connectionId);
    }

    public void setDP(String username, ImageIcon icon) {
        userData.setDP(username, icon);
    }

    public void setNickname(String username, String nickname) {
        userData.setNickname(username, nickname);
    }

    public void updateUserInfo(User newUser, String username) {
        userData.updateUserInfo(newUser, username);
    }

    public void listUsers() {
        Enumeration en = userData.getList().elements();
        while (en.hasMoreElements()){
            User u = (User) en.nextElement();
            System.out.println("username : "+u.getUsername()+"\n");
        }
    }

    public void showDescription(String username){
        try{
            User u = userData.getList().get(username);
            System.out.println(u.toString());
        }catch(NullPointerException e){System.out.println("Unknown user");}
    }

    /**
     * affiche l'ecran de lancement du serveur qui indique sur quel port a été lancé
     * le serveur
     * @param port numero de port sur lequel le serveur a été lancé
     */
    private void afficheWelcome(Integer port) {
        System.out.println("+--------------------------+");
        System.out.println("| Server launched          |");
        System.out.println("+--------------------------+");
        System.out.println("| listening on port : " + port.toString());
        System.out.println("+--------------------------+");
        System.out.println("| Quit : type \"exit\"       |");
        System.out.println("+--------------------------+");

    }

    /**
     * affiche tous les repertoires present dans le repertoire shared
     */
    private File[] getDirectoryList() {
        File f = new File("../shared");
        File[] list = new File[f.list().length];
        list = f.listFiles();
        int j;
        j = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory())
                j++;
        }
        File[] directories = new File[j];
        j = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                directories[j] = list[i];
                j++;
            }
        }
        return directories;
    }

    /**
     * lit tous les fichier et dossier present dans le repertoire dir
     * @param dir repertoire dont on veut afficher le contenu
     * @return un vecteur de String contenant les elements lus
     */
    private String[] getFilesIntoDirectory(String dir) {
        File f = new File("../shared/" + dir);
        String[] listProvisoire = new String[f.list().length];
        listProvisoire = f.list();
        String[] list = new String[f.list().length + 1];
        list[0] = f.getName();
        for (int i = 0; i < f.list().length; i++) {
            list[i + 1] = listProvisoire[i];
        }
        return list;
    }

    private void initFolders() {
        File f = new File("../shared");
        f.mkdir();
        f = new File("../shared/Videos");
        f.mkdir();
        f = new File("../shared/Documents");
        f.mkdir();
        f = new File("../shared/Music");
        f.mkdir();
        f = new File("../shared/Archives");
        f.mkdir();
        f = new File("../shared/Other");
        f.mkdir();
        f = new File("../shared/Pictures");
        f.mkdir();
        f = new File("../conf");
        f.mkdir();
        f = new File("../reports");
        f.mkdir();


    }

    private void initUsersList() {
        userData = new UserList();
    }

    /**
     * lit les repertoires et fichier du repertoire shared et les mets dans un ArrayList
     * @return le arraylist 
     */
    public ArrayList makeArrayOfDirectory() {
        File[] directories = getDirectoryList();
        ArrayList all = new ArrayList();
        for (int i = 0; i < directories.length; i++) {
            String name = directories[i].getName();
            all.add(getFilesIntoDirectory(name));
        }
        return all;
    }

    /**
     * renomme deplace le fichier dans le repertoire adequat en fonction de son extension
     * @param name nom du fichier sous lequel on veut enregistrer
     * @param temp fichier que l'on veut deplacer dans le repertoire adequat
     */
    public void saveFile(String name, File temp) {
        File dir = null;
        try {
            String ext = name.substring(name.lastIndexOf("."));//on extrait l'extension du nom du fichier
            //en fonction de l'extension on cree le fichier dans lequel on deplacera la fichier temp
            if (ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".gif") ||
                    ext.equalsIgnoreCase(".bmp") || ext.equalsIgnoreCase(".png")) {
                dir = new File("../shared/Pictures/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Pictures/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            } else if (ext.equalsIgnoreCase(".mp3") || ext.equals(".wav") || ext.equalsIgnoreCase(".wma")) {
                dir = new File("../shared/Music/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Music/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            } else if (ext.equalsIgnoreCase(".txt") || ext.equalsIgnoreCase(".doc") ||
                    ext.equalsIgnoreCase(".docx") || ext.equalsIgnoreCase(".odt") ||
                    ext.equalsIgnoreCase(".pdf") || ext.equalsIgnoreCase(".ppt") ||
                    ext.equalsIgnoreCase(".pps") || ext.equalsIgnoreCase(".odp") ||
                    ext.equalsIgnoreCase(".xls") || ext.equalsIgnoreCase(".ods") ||
                    ext.equalsIgnoreCase(".pages") || ext.equalsIgnoreCase(".html") ||
                    ext.equalsIgnoreCase(".htm")) {
                dir = new File("../shared/Documents/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Documents/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            } else if (ext.equalsIgnoreCase(".avi") || ext.equals(".mpeg") || ext.equalsIgnoreCase(".mp4") ||
                    ext.equalsIgnoreCase(".flv") || ext.equals(".ogm") || ext.equals(".mpg")) {
                dir = new File("../shared/Videos/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Videos/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            } else if (ext.equalsIgnoreCase(".zip") || ext.equals(".tar") || ext.equalsIgnoreCase(".gz") ||
                    ext.equalsIgnoreCase(".bz2") || ext.equals(".rar") || ext.equals(".7z")) {
                dir = new File("../shared/Archives/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Archives/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            } else {
                dir = new File("../shared/Other/" + name);
                int i = 1;
                while (dir.exists()) {
                    dir = new File("../shared/Other/" + name.replaceAll(ext, "_" + i) + ext);
                    i++;
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            dir = new File("../shared/Other/" + name);
            int i = 1;
            while (dir.exists()) {
                dir = new File("../shared/Other/" + name + "_" + i);
                i++;
            }
        }
        boolean success = temp.renameTo(dir);//on deplace et renomme le fichier
        if (!success)
            ErrorLogger.getLogger().log("Error while saving file");

    }

    /**
     * affiche a l'ecran tous les utilisateurs connectes sur le serveur
     */
    public void showConnectedUserInfo() {
        Enumeration e = userTable.elements();
        while (e.hasMoreElements()) {
            TraitementClient c = (TraitementClient) e.nextElement();
            String usrn = c.getUsername();
            Socket s = c.getSocket();
            System.out.println("Connected: " + usrn + " on port " + s.getPort());
        }
    }

    /**
     * envoi un message de l'administrateur a un utilisateur
     * @param user utilisateur a qui on envoi le message
     * @param msg message a envoyer
     */
    public void sendMessageToUser(String user, String msg) {
        try {
            TraitementClient c = (TraitementClient) userTable.get(user);
            c.sendAdministratorMessage(msg);
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Cannot send admininstrator message to " + user + ", user is disconnected", e);
        }
    }

    /**
     * envoi un message de l'administrateur a tous les utlisateurs
     * @param msg message a envoyer
     */
    public void sendMessageToAllUsers(String msg) {
        TraitementClient c;
        Enumeration e = userTable.elements();
        while (e.hasMoreElements()) {
            c = (TraitementClient) e.nextElement();
            c.sendAdministratorMessage(msg);
        }
        if (userTable.size() == 0)
            ErrorLogger.getLogger().log("Cannot send administrator message, no users connected");
    }

    public int getNbRegistrated() {
        return userData.getList().size();
    }
}
