/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.voice;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.ClientChatView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bendaccache
 */
public class VoiceConnection {
    private String host;
    private int numPort;
    private ClientChatView cv;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private int Id;
    private int pearId;
    private Socket socket;

    public VoiceConnection(String host, int port){
        try {
            this.host = host;
            this.numPort = port;
            this.cv = ClientChatView.getInstance();
            socket = new Socket(host, this.numPort); //on ouvre la connexion
            OutputStream out = socket.getOutputStream(); //on cree les flux d'entree et de sortie
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            sendInt(ChatConstants.VOICE_CONNECTION);
            this.Id = receiveInt();
        } catch (UnknownHostException ex) {
            ErrorLogger.getLogger().logError("", ex);
        } catch (IOException ex) {
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            entree.close();
            sortie.close();
            socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("", ex);
        }
    }

    public int getId(){
        return Id;
    }

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
    public byte[] receiveTabByte(int size) throws IOException {
        byte[] b = new byte[size];
            entree.readFully(b);
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

      public void sendText(String sendText) throws IOException {
            sortie.writeUTF(sendText);
            sortie.flush();
    }

    /**
     * envoi un int
     * @param i le int a envoyer
     * @throws java.io.IOException
     */
    public void sendInt(int i) throws IOException {
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

    public void setPearId(int id) {
        pearId = id;
    }

    public int getPearId(){
        return pearId;
    }



}
