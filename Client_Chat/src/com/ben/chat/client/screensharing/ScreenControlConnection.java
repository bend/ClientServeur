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
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**

/**
 *
 * @author bendaccache
 */
public class ScreenControlConnection {

    private String host;
    private int numPort;
    private ClientChatView cv;
    private Socket socket;
    private DataOutputStream sortie;
    private DataInputStream entree;

    public ScreenControlConnection(String host, int port) throws UnknownHostException, IOException {
        InputStream in = null;
        this.host = host;
        this.numPort = port;
        this.cv = ClientChatView.getInstance();
        socket = new Socket(host, this.numPort);
        OutputStream out = socket.getOutputStream();
        sortie = new DataOutputStream(out);
        in = socket.getInputStream();
        entree = new DataInputStream(in);
        sendInt(ChatConstants.SCREEN_CONTROL_CONNECTION); //envoi le type de connection au serveur
        sendText(cv.getNickname());
    }

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
            ErrorLogger.getLogger().logError("", ex);
        }
    }

    public int receiveInt() throws IOException, java.net.SocketException {
        int size = 0;
        size = entree.readInt();
        return size;
    }

    public String receiveText() throws IOException {
        String text = null;
        text = entree.readUTF();
        return text;
    }

    public void sendDouble(double d) throws IOException {
        sortie.writeDouble(d);
    }

    public double receiveDouble() throws IOException {
        return entree.readDouble();
    }
}