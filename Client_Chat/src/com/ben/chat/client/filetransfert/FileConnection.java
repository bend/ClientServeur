/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.filetransfert;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author daccachb
 */
public class FileConnection {

    private String host;
    private int numPort;
    private ClientChatView cv;
    private DataInputStream entree;
    private DataOutputStream sortie;
    private Socket socket;
    private int connectionId;

    /**
     * Ouvre une connection pour le transfert de fichier
     * @param host hostname su serveur
     * @param port port d'ecoute du serveur
     * @param cv Client_ChatView
     * @throws java.net.UnknownHostException si la connection au serveur est impossible
     * @throws java.io.IOException si un probleme d'entree/sortie survient
     */
    public FileConnection(String host, int port) throws UnknownHostException, IOException {
        InputStream in = null;
        this.host = host;
        this.numPort = port;
        this.cv = ClientChatView.getInstance();
        socket = new Socket(host, this.numPort);
        OutputStream out = socket.getOutputStream();
        sortie = new DataOutputStream(out);
        in = socket.getInputStream();
        entree = new DataInputStream(in);
        sendInt(ChatConstants.FILE_CONNECTION); //envoi le type de connection au serveur
        connectionId = receiveInt(); //recoit l'id de connection du serveur
    }

    /**
     * retourne l'id de connection
     * @return id de connection
     */
    public int getConnectionId() {
        return connectionId;
    }

    /**
     * envoie un int sur le flux de sortie
     * @param i int a envoyer
     * @throws java.io.IOException si un probleme d'ecriture survient
     */
    public void sendInt(int i) throws IOException {
        sortie.writeInt(i);
        sortie.flush();
    }

    /**
     * envoi un UTF sur le flux de sortie
     * @param sendText texte a envoyer
     */
    public void sendText(String sendText) {
        try {
            sortie.writeUTF(sendText);
            sortie.flush();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    /**
     * envoi un tableau d'octets sur le flux de sortie
     * @param tabFile tableau a envoyer
     * @throws java.io.IOException si un probleme d'ecriture survient
     */
    public void sendTabByte(byte[] tabFile) throws IOException {
        sortie.write(tabFile);
        sortie.flush();
    }

    public int receiveInt() throws IOException {
        int size = 0;
        size = entree.readInt();
        return size;
    }

    /**
     * recoit un tableau d'octets sur le flux d'entree
     * @param size taille du tableau a recevoir
     * @return le tableau lu
     * @throws java.io.IOException si un probleme de lecture survient
     */
    public byte[] receiveTabByte(int size) throws IOException {
        byte[] b = new byte[size];
        entree.readFully(b);
        return b;
    }

    /**
     * recoit un UTF sur le flux d'entree
     * @return l'UTF lu
     * @throws java.io.IOException si un probleme de lecture survient
     */
    public String receiveText() throws IOException {
        String text = null;
        text = entree.readUTF();
        return text;
    }

    /**
     * ferme la connection
     */
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    /**
     * recoit un long sur le flux d'entree
     * @return le long lu
     */
    public long receiveLong() {
        long l = 0;
        try {
            l = entree.readLong();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
        return l;
    }

    /**
     * envoi un long sur le flux de sortie
     * @param i long a ecrire
     */
    public void sendLong(long i) {
        try {
            sortie.writeLong(i);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }
}
