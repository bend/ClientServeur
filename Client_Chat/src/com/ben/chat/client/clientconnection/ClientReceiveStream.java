/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.clientconnection;

import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import com.ben.chat.userdata.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author bendaccache
 */

public class ClientReceiveStream extends Thread implements Runnable{
    private ClientConnection clC;
    private ClientChatView cv;
    private boolean connected=true;

    /**
     * cree un flux de lecture sur le flux d'entree
     * @param con ClientConnection
     * @param cv Client_ChatView
     */
    public ClientReceiveStream(ClientConnection con){
   		super("ReceiveStream");
        this.clC=con;
        this.cv=ClientChatView.getInstance();
   }

    @Override
    public void run() throws NullPointerException{
        while(connected){
            try{
                int action = clC.receiveInt();//on lit le type d'action a faire
                String username=null;
                String msg=null;
                switch(action){
                    case ChatConstants.CONTACT_LIST://on recoit une contactList
                        byte sizeofList=clC.receiveByte();//on lit la taille de la liste a recevoir
                        String[] contactList = new String[sizeofList];
                        int i;
                        for(i=0;i<sizeofList;i++){//lecture de la liste de contact que l'on met dans un tableau
                           msg=clC.receiveText();
                           contactList[i]=msg;
                        }
                       cv.updateContactList(contactList);//on met a jour la liste de contact
                       break;
                    case ChatConstants.PRIVATE_CONV://privateConv
                        int RGB = clC.receiveInt();
                        String sender=clC.receiveText();//on lit le nom d'utilisateur de l'envoyeur du message
                        String convName = clC.receiveText();
                        msg=clC.receiveText();//on lit le message
                        cv.startPrivateConversation(RGB,sender,convName,msg); //on appelle la methode qui va afficher le message dans une conversation privee
                        break;
                    case ChatConstants.SEND_FILE_REQUEST://request to send
                        String senderUser = clC.receiveText();
                        long fileSize = clC.receiveLong();
                        String fileName = clC.receiveText();
                        String filePath = clC.receiveText();
                        cv.showRequestToReceive(senderUser,fileSize,fileName,filePath);
                        break;
                    case ChatConstants.SEND_FILE_REQUEST_APPROVAL://clear to send
                        int connectionId=clC.receiveInt();
                        username= clC.receiveText();
                        fileName = clC.receiveText();
                        filePath = clC.receiveText();
                        cv.sendFile(username,filePath,connectionId);
                        cv.prepareTextOnSalonField("***"+username+" accepted the request to send "+fileName+"***");
                        break;
                    case ChatConstants.SEND_FILE_REQUEST_DISAPPROVAL://non clear to send
                        username= clC.receiveText();
                        fileName = clC.receiveText();
                        JOptionPane.showMessageDialog(null, username+" declined the request to receive "+fileName);
                        cv.prepareTextOnSalonField("***"+username+" declined the request to send "+fileName+"***");
                        break;
                    case ChatConstants.LIST_DIRECTORY: //directory list
                        ArrayList all = new ArrayList();
                        int listSize = clC.receiveInt();
                        for (i = 0; i <listSize; i++) {
                            int size2=clC.receiveInt();
                            Object[] obj= new String[size2];
                            for (int j = 0; j < size2; j++) {
                                obj[j]=clC.receiveText();
                            }
                            all.add(obj);
                        }
                        cv.updateTree(all);
                        break;
                    case ChatConstants.USERNAME_UPDATE_OK://modification ok
                        username=clC.receiveText();
                        cv.updatedUsername(true,username);
                        break;
                    case ChatConstants.USERNAME_UPDATE_FAIL://modification refusee
                        cv.updatedUsername(false,null);
                        break;
                    case ChatConstants.PUBLIC_CONV://salonConv
                        RGB = clC.receiveInt();
                        sender=clC.receiveText();
                        msg=clC.receiveText();
                        cv.writePublicConv(RGB,sender,msg);
                        break;
                    case ChatConstants.ERROR_MESSAGE://reception erreur
                        msg=clC.receiveText();
                        JOptionPane.showMessageDialog(null, msg);
                    case ChatConstants.ADMINISTRATOR_MESSAGE:
                        msg=clC.receiveText();
                        JOptionPane.showMessageDialog(null, "Message from administrator:\n"+msg);
                        break;
                    case ChatConstants.DISCONNECTED_USER:
                        String disconnectedUser = clC.receiveText();
                        cv.showDisconnectedUser(disconnectedUser);
                        break;
                    case ChatConstants.CONNECTED_USER:
                        String connectedUser = clC.receiveText();
                        cv.showConnectedUser(connectedUser);
                        break;
                    case ChatConstants.USER_CHANGED_USERNAME:
                        String oldUsername = clC.receiveText();
                        String newUsername = clC.receiveText();
                        cv.showUpdatedUsername(oldUsername,newUsername);
                        break;
                    case ChatConstants.VOICE_REQUEST:
                        username = clC.receiveText();
                        cv.showCallRequest(username);
                        break;
                    case ChatConstants.CALL_REFUSE:
                        username = clC.receiveText();
                        cv.showCallRefuse(username);
                        break;
                    case ChatConstants.CALL_ACCEPTANCE:
                        username = clC.receiveText();
                        cv.showCallAccept(username);
                        break;
                    case ChatConstants.VOICE_ID:
                        int id = clC.receiveInt();
                        cv.setVoiceId(id);
                        break;
                    case ChatConstants.SEND_DP:
                        username = clC.receiveText();
                        Object obj = clC.receiveObject();
                        cv.updateDp(username, obj);
                        break;
                    case ChatConstants.GET_USER_INFO:
                        User user = (User)clC.receiveObject();
                        cv.setUserInfo(user);
                        break;
                    case ChatConstants.RECEIVE_REQUESTED_USER_INFO:
                        String requestNickname = clC.receiveText();
                        User userWant = (User) clC.receiveObject();
                        cv.showGetInfoBox(requestNickname,userWant);
                        break;
                    case ChatConstants.GET_FILE_INFO:
                        String filename  = clC.receiveText();
                        String type = clC.receiveText();
                        long size = clC.receiveLong();
                        ImageIcon icon =(ImageIcon) clC.receiveObject();
                        cv.showFileInfo(filename,type, size, icon);
                        break;
                    case ChatConstants.SHARE_SCREEN_REQUEST:
                        String nickname = clC.receiveText();
                        cv.showShareScreenRequest(nickname);
                        break;
                    case ChatConstants.SHARE_SCREEN_DISAPROVAL:
                        nickname  = clC.receiveText();
                        cv.showShareScreenDisaproval(nickname);
                        break;
                    case ChatConstants.SHARE_SCREEN_APPROVAL:
                        id = clC.receiveInt();
                        nickname = clC.receiveText();
                        cv.beginScreenSharing(id, nickname);
                        break;
                    case ChatConstants.SCREEN_CONTROL_REQUEST:
                        nickname = clC.receiveText();
                        cv.showScreenControlRequest(nickname);
                        break;
                    case ChatConstants.SCREEN_CONTROL_ACCEPT:
                        nickname = clC.receiveText();
                        cv.showScreenControlAccept(nickname);
                        break;
                    case ChatConstants.SCREEN_CONTROL_REFUSE:
                        nickname = clC.receiveText();
                        cv.showScreenControlRefuse(nickname);
                        break;
                    case ChatConstants.SCREEN_CONTROL_END:
                        nickname = clC.receiveText();
                        cv.showScreenControlEnd(nickname);
                        break;
                    case ChatConstants.SOUND_CLIP:
                        nickname = clC.receiveText();
                        int s = clC.receiveInt();
                        byte[] b =clC.receiveTabByte(s);
                        cv.showSoundClip(nickname, b);
                        break;
                    case ChatConstants.PUBLIC_KEY_REQUEST:
                        String fromUsername = clC.receiveText();
                        cv.startSecureChat(fromUsername);
                        break;
                    case ChatConstants.PUBLIC_KEY:
                        fromUsername = clC.receiveText();
                        obj = clC.receiveObject();
                        cv.setPublicKey(fromUsername, obj);
                        break;
                    case ChatConstants.SECURE_CONV:
                        fromUsername = clC.receiveText();
                        String crypted = clC.receiveText();
                        cv.showCryptedConv(fromUsername, crypted);
                        break;
                    }
               }catch (IOException e){
                    connected =false;
                    JOptionPane.showMessageDialog(null, "Connection closed..\nPlease try to reconnect", "Connection closed", 1);
                    cv.initConnectionWindow();
               }
        }
    }

}
