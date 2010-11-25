package com.ben.chat.server.traitement;

import com.ben.chat.server.constants.ChatConstants;
import com.ben.chat.server.report.ErrorLogger;
import com.ben.chat.server.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;

/**
 * classe qui gere le transfert de fichier du cote du serveur
 * @author daccachb
 */
public class TraitementFichier extends Thread implements Runnable {

    private Socket socket;
    private Serveur serveur;
    private boolean connected = true;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private int connectionId;

    /**
     * Cree un thread qui contiendra la connexion fichier
     * @param socket socket sur lequel le client est connecte
     * @param serveur serveur principal
     * @param id id de connexion de la connexion
     */
    public TraitementFichier(Socket socket, Serveur serveur, int id) {
        super("Traitement Fichier");
        this.socket = socket;
        this.serveur = serveur;
        this.connectionId = id;
    }

    @Override
    public void run() {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();//on cree le flux d'entree et sortie
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            sortie.writeInt(connectionId);
            while (connected) {//on lit les actions
                int action = receiveInt();
                ForwardAction(action);
            }
        } catch (IOException ex) {//fernetureconnection
            ErrorLogger.getLogger().logError("IO", ex);
        }

    }

    private void ForwardAction(int action) throws IOException {
        switch (action) {
            case ChatConstants.UPLOAD_FILE_TO_CLIENT:
                uploadFile();
                break;
            case ChatConstants.UPLOAD_FILE_TO_SERVER:
                downloadFile();
                break;
            case ChatConstants.FILE_TRANSFERT:
                transfertFile();
                break;
        }
    }

    public void sendInt(int i) {
        try {
            sortie.writeInt(i);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing integer on stream", ex);
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
            ErrorLogger.getLogger().logError("Error while writing UTF on stream", ex);
        }
    }

    public void sendTabByte(byte[] tabFile) throws IOException {
        sortie.write(tabFile);
        sortie.flush();
    }

    public String receiveText() {
        String text = null;
        try {
            text = entree.readUTF();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while receiving UTF on stream", ex);
        }
        return text;
    }

    /**
     * ferme la connexion en cours
     */
    private void closeConnection() {
        try {
            this.sortie.close();
            this.entree.close();
            this.socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Connection closed", ex);

        }
    }

    private long receiveLong() {
        long l = 0;
        try {
            l = entree.readLong();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing long on stream", ex);
        }
        return l;
    }

    private byte[] receiveTabByte(int size) throws IOException {
        byte[] tabFile = new byte[size];
        entree.readFully(tabFile);
        return tabFile;
    }

    private int receiveInt() throws IOException {
        int action = entree.readInt();
        return action;

    }

    private void sendLong(long length) {
        try {
            sortie.writeLong(length);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error writing int", ex);
        }
    }

    /**
     * envoi le fichier que l'utilisateur a selectionne a ce dernier
     */
    private void uploadFile() {
        try {
            FileInputStream inF = null;
            String path = receiveText();
            File f = new File("../shared" + path);
            inF = new FileInputStream(f);
            float nbMorceaux = f.length() / ChatConstants.PARSE_SIZE;
            int nbPassage = (int) nbMorceaux;//nombre de morceaux de fichier
            if (f.length() % ChatConstants.PARSE_SIZE > 0)
                nbPassage++;
            int nbPasse = 0;
            byte[] tabFile;
            long tailleRestante = f.length();
            sendInt(ChatConstants.UPLOAD_FILE_TO_CLIENT);
            sendText(f.getName());//on envoi le nom du fichier
            sendLong(f.length());//la taille
            sendInt(nbPassage);//le nombre de passages
            while (nbPasse < nbPassage) {
                if (tailleRestante >= ChatConstants.PARSE_SIZE) {//si il reste plus de PARSE_SIZE octets a envoyer on envoi PARSE_SIZE octets
                    tabFile = new byte[ChatConstants.PARSE_SIZE];
                    inF.read(tabFile);
                    tailleRestante -= ChatConstants.PARSE_SIZE;
                    nbPasse++;
                } else {//sinon on envoi les octets restants
                    tabFile = new byte[(int) tailleRestante];
                    inF.read(tabFile);
                    nbPasse++;
                }
                sendInt(tabFile.length);
                sendTabByte(tabFile);
            }
            socket.close();
        } catch (FileNotFoundException ex) {
            ErrorLogger.getLogger().logError("File not found", ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO error", ex);
        }
    }

    /**
     * recoit un fichier qu'un utlisateur veut charge sur le serveur
     */
    private void downloadFile() {
        File temp = null;
        try {
            String FileName = receiveText();
            int nbPassage = receiveInt();//recoi le nombre de passages
            int nbPasse = 0;
            temp = new File("temp" + connectionId);//cree un fichier temporaire
            FileOutputStream outF = new FileOutputStream(temp);
            byte[] tabFile = null;
            while (nbPasse < nbPassage) {//lit les tableaux d'octets
                int size = receiveInt();
                tabFile = new byte[size];
                try {
                    tabFile = receiveTabByte(size);
                    outF.write(tabFile);//les ecrit dans le fichier temp
                    nbPasse++;
                } catch (IOException e) {
                    ErrorLogger.getLogger().logError("Error while downloading file", e);

                    temp.delete();
                }
            }
            outF.close();
            serveur.saveFile(FileName, temp);//il appele la methode qui enregistrera le fichier au bon endroit
            serveur.sendAllDirectories();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while receiving file", ex);
            temp.delete();
        }
        serveur.removeFileConnectionFromHashtable(connectionId);

        try {
            socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while closing socket", ex);
        }
    }

    /**
     * transfert le fichier entre les deux clients
     */
    private void transfertFile() {
        int conId;
        int cont = 0;
        String destUser = null;
        String username;
        String fileName;
        long fileSize;
        int size = 0;
        byte[] tabFile = null;
        int nbPassage = 0;
        int nbPasse = 0;
        TraitementFichier tf = null;
        try {
            conId = receiveInt();//il recoit l'ID de connexion
            destUser = receiveText();//lit le nom d'utlisateur du receveur
            username = receiveText(); //de l'envoyeur
            fileName = receiveText(); //le nom du fichier
            fileSize = receiveLong();// la taille du fichier
            size = 0;
            nbPassage = receiveInt();
            nbPasse = 0;
            Hashtable tableFileCon = serveur.getHashtableOfFileConnection();//il recherche la connexion grace a l'ID
            tf = (TraitementFichier) tableFileCon.get(conId);//et envoi sur cette connexion
            tf.sendInt(ChatConstants.FILE_TRANSFERT);
            tf.sendText(username);
            tf.sendInt(size);
            tf.sendText(fileName);
            tf.sendLong(fileSize);
            tf.sendInt(nbPassage);
            cont = receiveInt();
        } catch (IOException e) {
            ErrorLogger.getLogger().logError("IO", e);
        }

        while (nbPasse < nbPassage && cont == ChatConstants.CONTINUE) {//et envoi le fichier
            try {//envoyeur deconnecte

                size = receiveInt();
                tabFile = new byte[size];
                tabFile = receiveTabByte(size);
            } catch (IOException e) {
                Hashtable table = serveur.getHashtableOfUsers();
                TraitementClient t = (TraitementClient) table.get(destUser);
                t.sendError("Error while transfering file; user disconnected");
                ErrorLogger.getLogger().logError("Error while transfering file, user disconnected", e);

                tf.closeConnection();
                this.closeConnection();
                break;
            }
            try {
                tf.sendInt(cont);
                tf.sendInt(size);
                tf.sendTabByte(tabFile);
                nbPasse++;
                cont = receiveInt();
                if (cont == ChatConstants.STOP)
                    tf.sendInt(ChatConstants.STOP);

            } catch (IOException e) {
                ErrorLogger.getLogger().logError("Receiver disconnected", e);

                tf.closeConnection();
                this.closeConnection();
                break;
            }
        }//ferme la connexion courante

        serveur.removeFileConnectionFromHashtable(connectionId);
        try {
            socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while closing socket", ex);
        }
    }
}
    
