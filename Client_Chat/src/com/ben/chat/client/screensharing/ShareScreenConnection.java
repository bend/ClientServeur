/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.screensharing;

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
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**

/**
 *
 * @author bendaccache
 */
public class ShareScreenConnection {

    private String host;
    private int numPort;
    private ClientChatView cv;
    private Socket socket;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private int connectionId;

    public ShareScreenConnection(String host, int port) throws UnknownHostException, IOException{
        InputStream in = null;
        this.host = host;
        this.numPort = port;
        this.cv = ClientChatView.getInstance();
        socket = new Socket(host, this.numPort);
        OutputStream out = socket.getOutputStream();
        sortie = new DataOutputStream(out);
        in = socket.getInputStream();
        entree = new DataInputStream(in);
        sendInt(ChatConstants.SCREEN_SHARING_CONNECTION); //envoi le type de connection au serveur
        //recoit l'id de connection du serveur
        connectionId = receiveInt();
    }

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
        } catch (IOException ex) {ErrorLogger.getLogger().logError("", ex);
}
    }

    public int receiveInt() throws IOException, java.net.SocketException {
        int size = 0;
        size = entree.readInt();
        return size;
    }

    public String receiveText() throws IOException{
         String text =null;
            text = entree.readUTF();
        return text;
    }

    public  Object receiveObject() throws IOException, ClassNotFoundException{
        Object obj = null;
        ObjectInputStream ois = new ObjectInputStream(entree);
        obj = ois.readObject();
        return obj;
    }
    /**
     * ferme la connection
     */
    public void closeConnection() {
        try {
            socket.close();
            cv.setShareScreenInstance(false);
        } catch (IOException ex) {ErrorLogger.getLogger().logError("", ex);
}
    }

    public void sendObject(Object iconDisplayPic) throws SocketException, IOException {
            ObjectOutputStream oos = new ObjectOutputStream(sortie);
            oos.writeObject(iconDisplayPic);
            oos.flush();
    }

}
