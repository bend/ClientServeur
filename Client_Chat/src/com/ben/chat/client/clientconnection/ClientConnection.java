/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.clientconnection;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author daccachb
 */
public class ClientConnection {

    private String host;
    private int numPort;
    private String username;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private ClientChatView cv;

    /**
     * ouvre une connection de type chat avec le serveur
     * @param nomServeur hostname du serveur
     * @param numPort numero de port sur lequel le serveur attend les connexions
     * @param username nom d'utilisateur du client
     * @param cv Client_ChatView
     * @throws java.io.IOException si la connection est impossible
     * @throws java.lang.ClassNotFoundException
     */
    public ClientConnection(ConnectionView con, String nomServeur, int numPort, String username, String password) throws IOException, ClassNotFoundException {
        this.host = nomServeur;
        this.numPort = numPort;
        this.username = username;
        this.cv = ClientChatView.getInstance();
        Socket socket = new Socket(host, this.numPort);//on ouvre la connexion
        OutputStream out = socket.getOutputStream();//on cree les flux d'entree et de sortie
        sortie = new DataOutputStream(out);
        InputStream in = socket.getInputStream();
        entree = new DataInputStream(in);
        sendInt(ChatConstants.CHAT_CONNECTION);//on envoi le type de connexion
        sendText(username);//on envoi le nm d'utilisateur
        sendText(password);
        byte res = receiveByte();
        if (res != 0) {// pas ok
            if (res == -1) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Bad_Username_Pass"));
            }
            if (res == -2) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Double_Signin"));
            }
            System.exit(0);

        } else if (res == 0) {//on veut voir si le nivkname est bon
            byte res2 = -1;
            do {
                res2 = entree.readByte();
                if (res2 == -1) {//on redemande de saisir le nom d'utilisateur
                    String nickname = null;
                    while (nickname == null || nickname.equals("") || nickname.contains(" ")) {
                        nickname = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Username_already_taken"));
                    }
                    cv.setNickname(nickname);
                    try {
                        sendText(nickname);
                    } catch (NullPointerException e) {
                        ErrorLogger.getLogger().logError("Null pointer", e);
                    }

                } else {
                    cv.setNickname(entree.readUTF());
                }

            } while (res2 == -1);
        }
    }

    /**
     * recoi un int
     * @return le int recu
     * @throws java.io.IOException
     */
    public int receiveInt() throws IOException {
        int size = 0;
        size = entree.readInt();
        return size;
    }

    /**
     * recoit un tableau d'octets
     * @param size taille du tableau a recevoir
     * @return le tableau d'octets recu
     */
    public byte[] receiveTabByte(int size) {
        byte[] b = new byte[size];
        try {
            entree.readFully(b);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
        return b;
    }

    /**
     * recoit un String
     * @return le String recu
     * @throws java.io.IOException si la connexion est interrompue
     */
    public String receiveText() throws IOException {
        String text = null;
        text = entree.readUTF();
        return text;
    }

    /**
     * recoit un byte
     * @return le byte recu
     */
    public byte receiveByte() {
        byte size = 0;
        try {
            size = entree.readByte();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
        return size;
    }

    /**
     * envoi un String
     * @param sendText le String a envoyer
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
     * envoi un int
     * @param i le int a envoyer
     * @throws java.io.IOException
     */
    public void sendInt(int i) throws IOException, SocketException {
        sortie.writeInt(i);
        sortie.flush();
    }

    /**
     * envoi un tableau d'octets
     * @param tabFile tableau d'octets a envoyer
     * @throws java.io.IOException si la connexion est interrompue
     */
    public void sendTabByte(byte[] tabFile) throws IOException {
        sortie.write(tabFile);
        sortie.flush();
    }

    /**
     * envoi un long
     * @param l le long a envoyer
     */
    public void sendLong(long l) {
        try {
            sortie.writeLong(l);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    /**
     * recoi un long
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

    public void sendObject(Object iconDisplayPic) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(sortie);
            oos.writeObject(iconDisplayPic);
            oos.flush();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    public Object receiveObject() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(entree);
            Object obj = ois.readObject();
            return obj;
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("Class not found", ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
        return null;
    }
}
