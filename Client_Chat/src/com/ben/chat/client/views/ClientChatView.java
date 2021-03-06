/*
 * Client_ChatView.java
 */
package com.ben.chat.client.views;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.views.LoggerView;
import com.ben.chat.client.screensharing.ScreenControlThread;
import com.ben.chat.client.screensharing.ControlPearScreenThread;
import com.ben.chat.client.screensharing.ShareMyScreenThread;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.clientconnection.UpdateContactListThread;
import com.ben.chat.client.clientconnection.ClientReceiveStream;
import com.ben.chat.client.clientconnection.ClientConnection;
import com.ben.chat.client.sound.SoundFile;
import com.ben.chat.client.sound.SoundModule;
import com.ben.chat.client.constant.preferences.PreferencesWrapper;
import com.ben.chat.client.filetransfert.FileConnection;
import com.ben.chat.client.filetransfert.FileReceiveStream;
import com.ben.chat.client.filetransfert.TransfertThread;
import com.ben.chat.client.filetransfert.UploadToServerThread;
import com.ben.chat.client.screensharing.ScreenControlConnection;
import com.ben.chat.client.screensharing.ShareScreenReceiveStream;
import com.ben.chat.client.screensharing.ShareScreenConnection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import com.ben.chat.userdata.*;

/**
 * The application's main frame.
 */
public class ClientChatView extends FrameView {

    private ClientConnection cl;
    public boolean updated = false;
    private String nickname;
    private ArrayList pane;
    private String[] contactList;
    private DefaultListModel listModel;
    private ConnectionView con;
    private boolean firstUpdate = true;
    private ServerBrowserView browser;
    private FileConnection fileCon;
    private String host;
    private int port;
    private StyledDocument styledDocument;
    private static ClientChatView instance;
    public ImageIcon iconDisplayPic;
    private String language;
    private User user;
    private String username;
    private CallView callView;
    private CallAcceptView callAcceptView;
    private boolean isCallInstance = false;
    private ShareMyScreenThread sharemyscreen;
    private boolean isShareScreenInstance = false;
    private ShareScreenConnection ssc;
    private ShareScreenReceiveStream shareScreenReceive;
    private Branding branding;
    private int textColor ;

    /**
     * classe qui creee la fenetre principale et execute dse actions d'envoi de messages et de fichier
     * @param app
     */
    @SuppressWarnings("static-access")
    public ClientChatView(SingleFrameApplication app) {
        super(app);
        new ErrorLogger();
        this.instance = this;
        showBranding();
        initConnectionWindow();
        listModel = new DefaultListModel();//on initialize la liste qui comportera les contacts
        initFolders();
        initComponents();
        this.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//pour fermer la fenetre quand on clique sur la croix
        UpdateContactListThread ThU = new UpdateContactListThread(this);//pour mettre a jour la liste des contacts
        ThU.start();
        pane = new ArrayList();//arraylist qui contiendra les elements dans un tab
        updateLanguageOfInterface();
        writeTextOnSalonField(java.util.ResourceBundle.getBundle(getLanguage()).getString("*******You_are_now_connected*******"), true, false, 14, ChatConstants.vert);
        uploadDpToserver();
        new PreferencesWrapper();
        textColor = PreferencesWrapper.getPreferences().getColorPref();
        if(PreferencesWrapper.getPreferences().isSound())
            new SoundModule(SoundFile.WELCOME).start();
   }

    /**
     * Partage l'ecran de l'utilisateur courant
     * @param id l'id de connexion de lautre utilisateur
     * @param nickname le nom d'utilisateur 
     */
    public void beginScreenSharing(int id, String dest) {
        try {
            ssc = new ShareScreenConnection(host, port);
            sharemyscreen = new ShareMyScreenThread(ssc, id, dest, this.nickname);
            sharemyscreen.start();
            isShareScreenInstance = true;
        } catch (UnknownHostException ex) {
            ErrorLogger.getLogger().logError("Error", ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error", ex);
        }

    }

    /**
     * ouvre la fenetre de connection qui demandera a l'utilisateur
     * de rentrer un port, un host et un nom d'utilisateur pour se connecter
     */
    public void initConnectionWindow() {
        con = new ConnectionView(null, true);
        JFrame mainFrame = ClientChatApp.getApplication().getMainFrame();
        con.setLocationRelativeTo(mainFrame);
        con.setAlwaysOnTop(false);
        branding.dispose();
        con.setVisible(true);
    }

    public void setPublicKey(String fromUsername, Object obj) {

        boolean exist = false;
        int i;
        int nbTab = salonConvTab.getTabCount();
        String tabTitle;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            tabTitle = salonConvTab.getTitleAt(i);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" "+ fromUsername)) {
                exist = true;
                break;
            }
        }
         WhispPane whisp = (WhispPane) pane.get(i - 1);
         whisp.setPublicKey(obj);

    }

    public void setVoiceId(int id) {
        try {
            callView.setVoiceId(id);
        } catch (NullPointerException e) {
            callAcceptView.setVoiceId(id);
        }
    }

    /**
     * ouvre la fenetre de about
     */
    public void showAboutBox() {
        JFrame mainFrame = ClientChatApp.getApplication().getMainFrame();
        aboutBox = new ClientChatAboutBox(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        aboutBox.setVisible(true);
    }

    /**
     * met a jour le socket, le host, et le port pour pouvoir ouvrir une connection pour l'envoi de fichier
     * @param cl ClientConnection : contient les socket de transfert
     * @param host hostname du serveur
     * @param port port d'ecoute du serveur
     */
    public void setSocket(ClientConnection cl, String host, String port) {
        this.cl = cl;
        this.host = host;
        this.port = Integer.parseInt(port);
        ClientReceiveStream receiveStr = new ClientReceiveStream(this.cl);
        receiveStr.start();
    }

    public void showCallAccept(String username) {
        callView.startCall();
    }

    public void showCallRefuse(String username) {
        callView.setDisaproval();
        isCallInstance = false;
        callView = null;
    }

    public void showCallRequest(String username) {
        callAcceptView = new CallAcceptView(ClientChatApp.getApplication().getMainFrame(), false, username);
        callAcceptView.setLocationRelativeTo(ClientChatApp.getApplication().getMainFrame());
        isCallInstance = true;
    }

    public void setCallInstance(boolean b) {
        isCallInstance = b;
    }

    public void showCryptedConv(String fromUsername, String crypted) {
        boolean exist = false;
        int i;
        int nbTab = salonConvTab.getTabCount();
        String tabTitle;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            tabTitle = salonConvTab.getTitleAt(i);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" "+ fromUsername)) {
                exist = true;
                break;
            }
        }
        try{
            WhispPane whisp = (WhispPane) pane.get(i - 1);
            whisp.showCryptedConv(crypted);
        }catch(NullPointerException e){JOptionPane.showMessageDialog(null, fromUsername +" sent a crypted message that cannot be decrypted because of window disposing");}
        catch(IndexOutOfBoundsException e){JOptionPane.showMessageDialog(null, fromUsername +" sent a crypted message that cannot be decrypted because of window disposing");}
    }

    public void showScreenControlAccept(String dest) {
        try {
            JOptionPane.showMessageDialog(null, dest + "accepted screen control");
            shareScreenReceive.setScreenControl(true);
            new ControlPearScreenThread(new ScreenControlConnection(host, port), shareScreenReceive, dest).start();
        } catch (UnknownHostException ex) {
            ErrorLogger.getLogger().logError("Unknown host", ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    public void showScreenControlEnd(String nickname) {
        sharemyscreen.setScreenControl(false);
        shareScreenReceive.setScreenControl(false);
    }

    public void showScreenControlRefuse(String dest) {
        JOptionPane.showMessageDialog(null, dest + " refused screen control");
        shareScreenReceive.setScreenControl(false);
    }

    public void showScreenControlRequest(String nickname) {
        if(PreferencesWrapper.getPreferences().isSound())
            new SoundModule(SoundFile.NOTIFICATION).start();
        JOptionPane confPane = new JOptionPane(nickname + java.util.ResourceBundle.getBundle(getLanguage()).getString("screen_control"));
        Object[] option = new Object[]{java.util.ResourceBundle.getBundle(getLanguage()).getString("Accept"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Decline")};
        confPane.setOptions(option);
        JDialog dialog = confPane.createDialog(new JFrame(), java.util.ResourceBundle.getBundle(getLanguage()).getString("screen_cont"));
        dialog.setVisible(true);
        Object obj = confPane.getValue();
        int result = -1;
        for (int k = 0; k < option.length; k++) {
            if (option[k].equals(obj)) {
                result = k;//recuperation du resultat
            }
        }
        if (result == 0) {//accepte
            try {
                sharemyscreen.setScreenControl(true);
                new ScreenControlThread(new ScreenControlConnection(host, port), sharemyscreen).start();
                sendInt(ChatConstants.SCREEN_CONTROL_ACCEPT);
                sendText(nickname);
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            }
        } else {
            sendInt(ChatConstants.SCREEN_CONTROL_REFUSE);
            sendText(nickname);
        }
    }

    public void showSoundClip(String nickname, byte[] b) {
        new SoundClipView(null, false, nickname, b);
    }

    public void startSecureChat(String fromUsername) {
        //get the right pane
        boolean exist = false;
        int i;
        int nbTab = salonConvTab.getTabCount();
        String tabTitle;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            tabTitle = salonConvTab.getTitleAt(i);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" "+ fromUsername)) {
                exist = true;
                break;
            }
        }

        if (!exist) {//si elle existe pas on la cree
            createTabPane(fromUsername);
        }
         WhispPane whisp = (WhispPane) pane.get(i - 1);
         whisp.startSecureChat();

    }

    /**
     * met a jour la liste des contacts quand le serveur en envoi une nouvelle
     * @param list
     */
    public void updateContactList(String[] list) {
        contactList = list;
        updated = true;
    }

    /**
     * rafraichit la liste des contacts quand elle a ete mise a jour
     */
    public void refreshContactList() {
        listModel.clear();
        for (int i = 0; i < contactList.length; i++) {
            listModel.addElement(contactList[i]);
        }
        contactJList.setSelectedIndex(0);
    }

    /**
     * ouvre une conversation privee si inexistante ou bien ecrit le texte recu dedans si existate
     * @param sender nom d'utilisateur de l'envoyeur du message
     * @param msg message qu'il a envoye
     */
    public void startPrivateConversation(int RGB,String sender, String convName, String msg) {
        int nbTab = salonConvTab.getTabCount();
        String tabTitle;
        int i;
        boolean exist = false;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            tabTitle = salonConvTab.getTitleAt(i);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" " + convName)) {
                exist = true;
                break;
            }
        }
        if (!exist) {//si elle existe pas on la cree
            createTabPane(sender);
        }
        if(i-1==salonConvTab.getSelectedIndex()){
            salonConvTab.setBackgroundAt(i, Color.red);
        }
        WhispPane whisp = (WhispPane) pane.get(i - 1);//on la recupere
        whisp.prepareConversationText(RGB,msg, sender);//on ecrit dedans
        if(PreferencesWrapper.getPreferences().isSound() && !sender.equals(this.nickname)){
            new SoundModule(SoundFile.SEND_TEXT).start();
        }
    }

    /**
     * affiche un message de confirmation a l'utilisateur pour lui demander si il est sur de vouloir uploader le ficheir sur le serveur
     * @param f fichier a uploader
     */
    public void confirmUploadFileToServer(File f) {
        JOptionPane confPane = new JOptionPane(java.util.ResourceBundle.getBundle(getLanguage()).getString("Are_you_sure_you_want_to_upload_") + f.getName() + " ?");//on cree la fenetre de confirmation
        Object[] option = new Object[]{java.util.ResourceBundle.getBundle(getLanguage()).getString("Upload"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Cancel")};
        confPane.setOptions(option);
        JDialog dialog = confPane.createDialog(new JFrame(), java.util.ResourceBundle.getBundle(getLanguage()).getString("Upload_File_to_server"));
        dialog.setVisible(true);
        Object obj = confPane.getValue();
        int result = -1;
        for (int k = 0; k < option.length; k++) {
            if (option[k].equals(obj)) {
                result = k;
            }
        }
        if (result == 0) {//si oui
            try {
                fileCon = new FileConnection(host, port);//on ouvre une nouvelle connexion avec le serveur
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error while opening connection");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error while transfering file");
            }
            UploadToServerThread st = new UploadToServerThread(f, fileCon);//on cree le thread d'envoi de fichier
            st.start();//on demarre le thread d'envoi de fichier
        }
    }

    /**
     * envoi un fichier a un autre utilisateur
     * @param destUsername utlisateur a qui on envoi le fichier
     * @param path chemin absolu du fichier
     * @param connectionId id de connexion de l'utilisateur a qui l'on veut envoyer le fichier
     */
    public void sendFile(String destUsername, String path, int connectionId) {
        File f = new File(path);
        try {
            FileInputStream inF = new FileInputStream(f);
            FileConnection fc = new FileConnection(host, port);
            TransfertThread tt = new TransfertThread(f, fc, connectionId, destUsername, nickname);//on cree le thread de transfert
            tt.start();
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error while opening connection");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error while transfering file");
        }
    }

    /**
     * ecrit sur le champ du salon les messages recu par un utilisateur
     * @param username nom d'utilisateur de l'envoyeur
     * @param msg message envoye par l'utilisateur
     */
    public void writePublicConv(int RGB,String username, String msg) {
        this.writeTextOnSalonField(username + " " + java.util.ResourceBundle.getBundle(getLanguage()).getString("said") + " " + ":", true, true, 13, new Color(RGB));
        this.writeTextOnSalonField(msg, false, true, 12, new Color(RGB));
    }

    /**
     * format le texte pour etre affiche sur le champ du salon
     * @param string texte a ecrire
     */
    public void prepareTextOnSalonField(String string) {
        this.writeTextOnSalonField(string, false, true, 14, ChatConstants.bleuGris);
    }

    /**
     * affiche si un utilisateur est deconnecte et recherche les conversation privee avec cet utilisateur et les desactive en attendant qu'il se reconnecte
     * @param disconnectedUser nom d'utilisateur de l'utilisateur deconnecte
     */
    public void showDisconnectedUser(String disconnectedUser) {
        if (!firstUpdate) {
            writeTextOnSalonField("***" + disconnectedUser + java.util.ResourceBundle.getBundle(getLanguage()).getString("_is_now_disconnected***"), false, true, 14, ChatConstants.rouge);
            for (int j = 0; j < salonConvTab.getTabCount(); j++) {
                String tabTitle = salonConvTab.getTitleAt(j);
                if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_") +" " + disconnectedUser)) {//on recherche les converastion privee
                    WhispPane p = (WhispPane) pane.get(j - 1);
                    p.writeTextOnWhispField("***" + disconnectedUser + java.util.ResourceBundle.getBundle(getLanguage()).getString("_is_disconnected***"), false, true, 14, ChatConstants.rouge);
                    p.setMessageFieldActive(false);//on la desactive
                }
            }
        }
        firstUpdate = false;
    }

    /**
     * affiche si un utilisateur est connecte et recherche si une conversation privee est en cour avec lui et la reactive
     * @param connectedUser nom d'utilisateur de l'utilisateur qui vient de se connecter
     */
    public void showConnectedUser(String connectedUser) {
        if (!firstUpdate) {
            writeTextOnSalonField("***" + connectedUser + java.util.ResourceBundle.getBundle(getLanguage()).getString("_is_now_connected***"), false, true, 14, ChatConstants.bleu);
            for (int j = 0; j < salonConvTab.getTabCount(); j++) {//on cherche la conversation privee, si elle existe
                String tabTitle = salonConvTab.getTitleAt(j);
                if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_") +" " +connectedUser)) {
                    WhispPane p = (WhispPane) pane.get(j - 1);
                    p.writeTextOnWhispField("***" + connectedUser + java.util.ResourceBundle.getBundle(getLanguage()).getString("_is_connected***"), false, true, 14, ChatConstants.rouge);
                    p.setMessageFieldActive(true);//on l'active
                }
            }
        }
        firstUpdate = false;
    }

    /**
     * affiche si un utlisateur a change de nom d'utilisateur et modifie le titre des conversations privee
     * @param oldUsername ancien nom d'utilisateur
     * @param newUsername nouveau nom d'utilisateur
     */
    public void showUpdatedUsername(String oldUsername, String newUsername) {
        writeTextOnSalonField("***" + oldUsername + java.util.ResourceBundle.getBundle(getLanguage()).getString("_changed_his_username_to_") + newUsername + " ***", false, true, 14, ChatConstants.bleu);
        for (int j = 0; j < salonConvTab.getTabCount(); j++) {
            String tabTitle = salonConvTab.getTitleAt(j);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_") + " " +oldUsername)) {
                salonConvTab.setTitleAt(j, java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_") +" " + newUsername);//on change le titre de la conversation
                WhispPane p = (WhispPane) pane.get(j - 1);
                p.setUsername(newUsername);
                p.writeTextOnWhispField("***" + oldUsername + java.util.ResourceBundle.getBundle(getLanguage()).getString("_changed_his_username_to_") + newUsername + " ***", false, true, 14, ChatConstants.bleu);
                p.setMessageFieldActive(true);
            }
        }
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setDisplayPic(ImageIcon iconDisplayPic) {
        this.iconDisplayPic = iconDisplayPic;
    }

    public void sendDp() {
        cl.sendObject(iconDisplayPic);
    }

    public void updateDp(String username, Object obj) {
        int nbTab = salonConvTab.getTabCount();
        String tabTitle;
        int i;
        boolean exist = false;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            tabTitle = salonConvTab.getTitleAt(i);
            if (tabTitle.equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_") +" "+ username)) {
                exist = true;
                WhispPane whisp = (WhispPane) pane.get(i - 1);//on la recupere
                whisp.setDp(obj);
            }
        }

    }

    public void setLanguage(String language) {
        if (language.equalsIgnoreCase("English")) {
            language = "com/ben/chat/client/resources/English";
        } else if (language.equalsIgnoreCase("Francais")) {
            language = "com/ben/chat/client/resources/Francais";
        }
        this.language = language;

    }

    public String getLanguage() {
        return language;
    }

    public void setUserInfo(User user) {
        this.user = user;
    }

    public void showFileInfo(String name, String type, long size, ImageIcon icon) {
        browser.showFileInfo(name, type, size, icon);
    }

    public void showGetInfoBox(String username, User userWant) {
        JFrame mainFrame = ClientChatApp.getApplication().getMainFrame();
        UserInfoView useriv = new UserInfoView(mainFrame, false, userWant);
        useriv.setLocationRelativeTo(mainFrame);
        useriv.setVisible(true);

    }

    public void setShareScreenInstance(boolean b) {
        isShareScreenInstance = b;
    }

    public void showShareScreenRequest(String nickname) {
        if(PreferencesWrapper.getPreferences().isSound())
            new SoundModule(SoundFile.NOTIFICATION).start();
        JOptionPane confPane = new JOptionPane(nickname + java.util.ResourceBundle.getBundle(getLanguage()).getString("want_share"));
        Object[] option = new Object[]{java.util.ResourceBundle.getBundle(getLanguage()).getString("Accept"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Decline")};
        confPane.setOptions(option);
        JDialog dialog = confPane.createDialog(new JFrame(), java.util.ResourceBundle.getBundle(getLanguage()).getString("screen_share"));
        dialog.setVisible(true);
        Object obj = confPane.getValue();
        int result = -1;
        for (int k = 0; k < option.length; k++) {
            if (option[k].equals(obj)) {
                result = k;//recuperation du resultat
            }
        }
        if (result == 0) {//accepte
            try {
                ssc = new ShareScreenConnection(host, port);
                shareScreenReceive = new ShareScreenReceiveStream(ssc);
                shareScreenReceive.start();
                isShareScreenInstance = true;
                sendInt(ChatConstants.SHARE_SCREEN_APPROVAL);
                sendInt(ssc.getConnectionId());//envoi l'ID de connection de la connexion ouverte pour le transfert de fichier
                sendText(nickname);
            } catch (UnknownHostException ex) {
                ErrorLogger.getLogger().logError("Unknown host", ex);
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            }

        } else {//refuse
            sendInt(ChatConstants.SHARE_SCREEN_DISAPROVAL);
            sendText(nickname);
        }
    }

    public void updateDpForAll() {
        int nbTab = salonConvTab.getTabCount();
        int i;
        boolean exist = false;
        for (i = 0; i < nbTab; i++) {//on regarde si la conversateion existe deja
            WhispPane whisp = (WhispPane) pane.get(i - 1);//on la recupere
            whisp.sendDpToOther();

        }
    }

    public User getUserInfo() {
        return user;
    }

    public void showShareScreenDisaproval(String nickname) {
        JOptionPane.showMessageDialog(null, nickname + java.util.ResourceBundle.getBundle(getLanguage()).getString("share_disaprove"));
    }

    public void uploadUserInfo() {
        try {
            cl.sendInt(ChatConstants.SEND_USER_INFO);
            cl.sendObject(user);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO in upload user info", ex);
        }
    }

    public void sendAudioClip(byte[] b, String destNickname) {
        try {
            sendInt(ChatConstants.SOUND_CLIP);
            sendText(destNickname);
            sendInt(b.length);
            cl.sendTabByte(b);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO sending sound clip", ex);
        }
    }

    public void sendObject(PublicKey aPublic) {
        cl.sendObject(aPublic);
    }

    void setUsername(String username) {
        this.username = username;
    }

    private void initFolders() {
        File dir = new File("/downloaded");
        dir.mkdir();
        dir = new File("/Conversations");
        dir.mkdir();
    }

    private void openCallView() {
        callView = new CallView(ClientChatApp.getApplication().getMainFrame(), false, contactJList.getSelectedValue().toString());
        callView.setLocationRelativeTo(ClientChatApp.getApplication().getMainFrame());
        callView.setVisible(true);
        isCallInstance = true;
    }

    /**
     * envoi une demande d'envoi de fichier a un utilisateur
     * @param size taille du fichier
     * @param fileName nom du fichier
     * @param path chemin absolu du fichier
     * @param receiverUsername nom d'utilisateur du destinataire
     */
    private void sendRequestToSendFile(long size, String fileName, String path, String receiverUsername) {
        writeTextOnSalonField(java.util.ResourceBundle.getBundle(getLanguage()).getString("Request_sent_to_") + receiverUsername + java.util.ResourceBundle.getBundle(getLanguage()).getString("waiting_for_acceptation**"), true, false, 14, ChatConstants.bleuGris);
        sendInt(ChatConstants.SEND_FILE_REQUEST);
        sendText(receiverUsername);
        sendLong(size);
        sendText(fileName);
        sendText(path);
    }

    /**envoi un UTF sur le flux de sortie
     *
     * @param text texte a envoyer
     */
    public void sendText(String text) {
        cl.sendText(text);
    }

    public void sendInt(int i) {
        try {
            cl.sendInt(i);
        } catch (IOException ex) {
           ErrorLogger.getLogger().logError("IO while sending int", ex);
        }
    }

    /**
     * envoi un long sur le flux de sortie en appelant la methode de la classe ClientConnection
     * @param size int a envoyer
     */
    private void sendLong(long size) {
        cl.sendLong(size);
    }

    /**
     * Affiche une boite de dialogue a l'utilisateur lorsqu'il recoit une demande de recpetion de fichier
     * et renvoie l'identifiant d'acceptation ou de refus
     * @param destUser nom d'utilisateur de l'envoyeur de fichier
     * @param size taille du fichier
     * @param fileName nom du fichier
     * @param path chemin absolu du fichier
     */
    public void showRequestToReceive(String destUser, long size, String fileName, String path) {
        //affichage de la demande de reception
        if(PreferencesWrapper.getPreferences().isSound())
            new SoundModule(SoundFile.NOTIFICATION).start();        JOptionPane confPane = new JOptionPane(destUser + java.util.ResourceBundle.getBundle(getLanguage()).getString("_wants_to_send_you_") + fileName + "\nSize: " + size / 1024 + "KB");
        Object[] option = new Object[]{java.util.ResourceBundle.getBundle(getLanguage()).getString("Accept"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Decline")};
        confPane.setOptions(option);
        JDialog dialog = confPane.createDialog(new JFrame(), java.util.ResourceBundle.getBundle(getLanguage()).getString("Receive_file"));
        dialog.setVisible(true);
        Object obj = confPane.getValue();
        int result = -1;
        for (int k = 0; k < option.length; k++) {
            if (option[k].equals(obj)) {
                result = k;//recuperation du resultat
            }
        }
        try {
            if (result == 0) {//accepte
                FileConnection fc = new FileConnection(host, port);//ouverture d'une deuxieme connection avec le serveur pour l'envoi du fichier
                new FileReceiveStream(fc).start(); //lancement du thread de reception de fichier
                sendInt(ChatConstants.ACCEPT_FILE);
                sendInt(fc.getConnectionId());//envoi l'ID de connection de la connexion ouverte pour le transfert de fichier
                sendText(destUser);
                sendText(fileName);
                sendText(path);
            } else {//refuse
                sendInt(ChatConstants.DECLINE_FILE);
                sendText(destUser);
                sendText(fileName);
            }
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Error_while_connecting_to_server"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Error_while_connecting_to_server"));
        }
    }

    /**
     * met a jour le nom d'utilisateur
     * @param usernameUP nouveau nom d'utilisateur
     */
    public void setNickname(String usernameUP) {
        this.nickname = usernameUP;
    }

    private static String getNameStyle(boolean bold, boolean italic, int size, Color color) {
        StringBuffer sb = new StringBuffer();
        if (bold) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        if (italic) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append(size);
        sb.append(color.getRGB());

        return sb.toString();
    }

    private Style getStyle(boolean bold, boolean italic, int size, Color color) {
        String styleName = getNameStyle(bold, italic, size, color);

        Style style = styledDocument.getStyle(styleName);

        if (style != null) {
            return style;
        } else {
            Style styleDefaut = styledDocument.getStyle(StyleContext.DEFAULT_STYLE);
            style = styledDocument.addStyle(styleName, styleDefaut);

            StyleConstants.setBold(style, bold);
            StyleConstants.setItalic(style, italic);
            StyleConstants.setFontSize(style, size);
            StyleConstants.setForeground(style, color);
            return style;
        }
    }

    public void updateLanguageOfInterface() {
        sendButton.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Send_Button"));
        EditMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Edit_Menu"));
        ViewMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("View_Menu"));
        browseMenuItem.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Browse_Menu_Item"));
        browseServeurMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Browse_Server_Menu"));
        changeColorMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Change_Color_Menu"));
        changeUsernameMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Change_Username_Menu"));
        clearField.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Clear_Field"));
        clearFieldMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Clear_Field_Menu"));
        close.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Close"));
        closeTab.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Close"));
        disconnectMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Disconnect"));
        fullScreenMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Full_Screen"));
        menuAbout.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("AboutMenuItem"));
        menuPrivateConversation.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Menu_Private_Conv"));
        saveConv.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Save_Conv"));
        saveConvMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Save_Conv"));
        sendFileMenu.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Send_File"));
        PrefMenuItem.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("Preference"));
        reportMenuItem.setText(java.util.ResourceBundle.getBundle(getLanguage()).getString("report"));

        //update language in whisp.
        for (int i = 0; i < pane.size(); i++) {
            WhispPane whisp = (WhispPane) pane.get(i);
            whisp.ChangeLanguage();
        }


    }

    private void showBranding() {
        branding = new Branding();
    }

    private void uploadDpToserver() {
        try {
            cl.sendInt(ChatConstants.UPLOAD_DP_TO_SERVER);
            sendDp();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO while uploading dp to server", ex);

        }
    }

    /**
     * ecrit le texte avec un style sur le champ du salon
     * @param texte a ecrire
     * @param bold si on veut que le texte soit gras
     * @param italic si on veut que le texte soit italic
     * @param size taille du texte
     * @param color couleur du texte
     */
    private void writeTextOnSalonField(String texte, boolean bold, boolean italic, int size, Color color) {
        try {
            styledDocument = salonField.getStyledDocument();
            styledDocument.insertString(styledDocument.getLength(), texte + "\n", getStyle(bold, italic, size, color));
            salonField.setCaretPosition(styledDocument.getLength());
        } catch (Exception e) {
                        ErrorLogger.getLogger().logError("Error writing on salon", e);
        }
    }

    /**
     * met a jour l'arbre contenant les fichier du repertoire shared sur le serveur
     * @param all
     */
    public void updateTree(ArrayList all) {
        if (browser == null) {//si la fenetre n'existe pas on la cree
            JFrame mainFrame = ClientChatApp.getApplication().getMainFrame();
            browser = new ServerBrowserView(mainFrame, false);
            browser.setLocationRelativeTo(mainFrame);
            browser.setVisible(false);
        }
        browser.updateTree(all);//et on met a jour l'arbre
    }

    /**
     * affiche une fenetre de confirmation de telechargement du fichier a partir du serveur
     * @param path chemin du fichier dans la hierarchy
     */
    public void confirmDownloadFileFromServer(Object[] path) {
        if(PreferencesWrapper.getPreferences().isSound())
            new SoundModule(SoundFile.NOTIFICATION).start();
        JOptionPane confPane = new JOptionPane(java.util.ResourceBundle.getBundle(getLanguage()).getString("Are_you_sure_you_want_to_download_") + path[path.length - 1] + " ?");
        Object[] option = new Object[]{java.util.ResourceBundle.getBundle(getLanguage()).getString("Download"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Cancel")};
        confPane.setOptions(option);
        JDialog dialog = confPane.createDialog(new JFrame(), java.util.ResourceBundle.getBundle(getLanguage()).getString("Download_File_from_server"));
        dialog.setVisible(true);
        Object obj = confPane.getValue();
        int result = -1;
        for (int k = 0; k < option.length; k++) {
            if (option[k].equals(obj)) {
                result = k;
            }
        }
        if (result == 0) {
            String currentPath = "";
            for (int i = 1; i < path.length; i++) {
                currentPath += "/" + path[i];
            }
            downloadFileFromServer(currentPath);
        }
    }

    /**
     * met a jour le nom d'utilisateur
     * @param updated si le nom d'utilisateur a ete mis a jour dans le serveur
     * @param newUsername nouveau nom d'utilisateur
     */
    public void updatedUsername(boolean updated, String newUsername) {
        if (updated) {//si il a change on informe l'utilisateur
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Username_updated"));
            this.nickname = newUsername;
        } else {//sinon on demande de resaisir le nom d'utilisateur
            try {
                newUsername = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle(getLanguage()).getString("Username_already_taken"));
                if (!newUsername.equals(null)) {
                    sendInt(ChatConstants.CHANGE_USERNAME);
                    sendText(newUsername);
                }
            } catch (NullPointerException e) {
                ErrorLogger.getLogger().logError("Null pointer exception", e);
            }
        }
    }

    /**
     * telecharge un fichier depuis le serveur
     * @param path chemin dans la hierarchy du fichier
     */
    private void downloadFileFromServer(String path) {
        writeTextOnSalonField(java.util.ResourceBundle.getBundle(getLanguage()).getString("***Transfer_of_the_file_in_progress"), false, true, 14, ChatConstants.bleuGris);
        try {
            fileCon = new FileConnection(host, port);//on ouvre une nouvelle connexion avec le serveur
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Host_unreachable"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Error_while_trying_to_connect_to_server_for_file_transfert"));
        }
        try {
            fileCon.sendInt(ChatConstants.DOWNLOAD_FILE_FROM_SERVER);//on envoi le type de connexion
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainPanel, java.util.ResourceBundle.getBundle(getLanguage()).getString("Error_while_transfering_data"));
        }
        fileCon.sendText(path);//on envoi le chemin du fichier a telecharger
        new FileReceiveStream(fileCon).start();//on demarre le thread de reception du fichier
    }

    /**
     * cree un nouvel onglet
     * @param userClicked utilisateur avec qui la conversation est ouverte
     */
    private void createTabPane(String userClicked) {
        int i;
        boolean exist = false;
        for (i = 0; i < salonConvTab.getTabCount(); i++) {//on regarde si il existe deja
            if (salonConvTab.getTitleAt(i).equals(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" " + userClicked)) {
                exist = true;
                break;
            }
        }
        if (!exist && !userClicked.equals(nickname)) {//si elle existe pas on en cree un
            WhispPane wp = new WhispPane(userClicked);
            boolean res = pane.add(wp);
            if (res) {
                salonConvTab.addTab(java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_with_")+" " + userClicked, wp);
            } else {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Unable_to_start_private_conversation..."));
            }
        } else {//sinom on ouvre l'onglet
            salonConvTab.setSelectedIndex(i);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        salonConvTab = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        contactJList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        salonField = new javax.swing.JTextPane();
        sendButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        messageField = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        privateConvToolbarItem = new javax.swing.JButton();
        callToolbarItem = new javax.swing.JButton();
        shareToolbarItem = new javax.swing.JButton();
        sendFileToolbarItem = new javax.swing.JButton();
        getInfoToolbarItem = new javax.swing.JButton();
        browseToolbarItem = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        saveConversationToolbarItem = new javax.swing.JButton();
        ClearFieldToolbarItem = new javax.swing.JButton();
        colorChooserButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu FileMenu = new javax.swing.JMenu();
        PrefMenuItem = new javax.swing.JMenuItem();
        saveConvMenu = new javax.swing.JMenuItem();
        browseServeurMenu = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        disconnectMenu = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        clearFieldMenu = new javax.swing.JMenuItem();
        changeUsernameMenu = new javax.swing.JMenuItem();
        closeTab = new javax.swing.JMenuItem();
        ViewMenu = new javax.swing.JMenu();
        fullScreenMenu = new javax.swing.JCheckBoxMenuItem();
        changeColorMenu = new javax.swing.JMenuItem();
        logMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu HelpMenu = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        reportMenuItem = new javax.swing.JMenuItem();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        popupContactList = new javax.swing.JPopupMenu();
        menuPrivateConversation = new javax.swing.JMenuItem();
        callContact = new javax.swing.JMenuItem();
        getInfoMenuItem = new javax.swing.JMenuItem();
        sendFileMenu = new javax.swing.JMenuItem();
        browseMenuItem = new javax.swing.JMenuItem();
        shareScreenMenuItem = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        closePopup = new javax.swing.JPopupMenu();
        close = new javax.swing.JMenuItem();
        saveConv = new javax.swing.JMenuItem();
        clearField = new javax.swing.JMenuItem();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(ClientChatView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setMinimumSize(new java.awt.Dimension(100, 150));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(400, 600));

        salonConvTab.setName("salonConvTab"); // NOI18N
        salonConvTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                salonConvTabMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                salonConvTabMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salonConvTabMouseClicked(evt);
            }
        });
        salonConvTab.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                salonConvTabStateChanged(evt);
            }
        });

        jPanel5.setName("jPanel5"); // NOI18N

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setDividerSize(5);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        contactJList.setModel(listModel);
        contactJList.setInheritsPopupMenu(true);
        contactJList.setName("contactJList"); // NOI18N
        contactJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                contactJListMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                contactJListMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactJListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(contactJList);

        jSplitPane2.setRightComponent(jScrollPane2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        salonField.setBackground(resourceMap.getColor("salonField.background")); // NOI18N
        salonField.setEditable(false);
        salonField.setText(resourceMap.getString("salonField.text")); // NOI18N
        salonField.setDisabledTextColor(resourceMap.getColor("salonField.disabledTextColor")); // NOI18N
        salonField.setDoubleBuffered(true);
        salonField.setName("salonField"); // NOI18N
        salonField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salonFieldMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(salonField);

        jSplitPane2.setLeftComponent(jScrollPane1);

        sendButton.setIcon(resourceMap.getIcon("sendButton.icon")); // NOI18N
        sendButton.setText(resourceMap.getString("sendButton.text")); // NOI18N
        sendButton.setName("sendButton"); // NOI18N
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        messageField.setColumns(20);
        messageField.setLineWrap(true);
        messageField.setRows(5);
        messageField.setName("messageField"); // NOI18N
        messageField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                messageFieldMouseClicked(evt);
            }
        });
        messageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                messageFieldKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(messageField);

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        privateConvToolbarItem.setIcon(resourceMap.getIcon("privateConvToolbarItem.icon")); // NOI18N
        privateConvToolbarItem.setText(resourceMap.getString("privateConvToolbarItem.text")); // NOI18N
        privateConvToolbarItem.setToolTipText(resourceMap.getString("privateConvToolbarItem.toolTipText")); // NOI18N
        privateConvToolbarItem.setFocusable(false);
        privateConvToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        privateConvToolbarItem.setName("privateConvToolbarItem"); // NOI18N
        privateConvToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        privateConvToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateConvToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(privateConvToolbarItem);

        callToolbarItem.setIcon(resourceMap.getIcon("callToolbarItem.icon")); // NOI18N
        callToolbarItem.setText(resourceMap.getString("callToolbarItem.text")); // NOI18N
        callToolbarItem.setToolTipText(resourceMap.getString("callToolbarItem.toolTipText")); // NOI18N
        callToolbarItem.setFocusable(false);
        callToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        callToolbarItem.setName("callToolbarItem"); // NOI18N
        callToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        callToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(callToolbarItem);

        shareToolbarItem.setIcon(resourceMap.getIcon("shareToolbarItem.icon")); // NOI18N
        shareToolbarItem.setText(resourceMap.getString("shareToolbarItem.text")); // NOI18N
        shareToolbarItem.setToolTipText(resourceMap.getString("shareToolbarItem.toolTipText")); // NOI18N
        shareToolbarItem.setFocusable(false);
        shareToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shareToolbarItem.setName("shareToolbarItem"); // NOI18N
        shareToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shareToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(shareToolbarItem);

        sendFileToolbarItem.setIcon(resourceMap.getIcon("sendFileToolbarItem.icon")); // NOI18N
        sendFileToolbarItem.setText(resourceMap.getString("sendFileToolbarItem.text")); // NOI18N
        sendFileToolbarItem.setToolTipText(resourceMap.getString("sendFileToolbarItem.toolTipText")); // NOI18N
        sendFileToolbarItem.setFocusable(false);
        sendFileToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sendFileToolbarItem.setName("sendFileToolbarItem"); // NOI18N
        sendFileToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sendFileToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(sendFileToolbarItem);

        getInfoToolbarItem.setIcon(resourceMap.getIcon("getInfoToolbarItem.icon")); // NOI18N
        getInfoToolbarItem.setText(resourceMap.getString("getInfoToolbarItem.text")); // NOI18N
        getInfoToolbarItem.setToolTipText(resourceMap.getString("getInfoToolbarItem.toolTipText")); // NOI18N
        getInfoToolbarItem.setFocusable(false);
        getInfoToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getInfoToolbarItem.setName("getInfoToolbarItem"); // NOI18N
        getInfoToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        getInfoToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(getInfoToolbarItem);

        browseToolbarItem.setIcon(resourceMap.getIcon("browseToolbarItem.icon")); // NOI18N
        browseToolbarItem.setText(resourceMap.getString("browseToolbarItem.text")); // NOI18N
        browseToolbarItem.setToolTipText(resourceMap.getString("browseToolbarItem.toolTipText")); // NOI18N
        browseToolbarItem.setFocusable(false);
        browseToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        browseToolbarItem.setName("browseToolbarItem"); // NOI18N
        browseToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        browseToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseToolbarItemActionPerformed(evt);
            }
        });
        jToolBar1.add(browseToolbarItem);

        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        saveConversationToolbarItem.setIcon(resourceMap.getIcon("saveConversationToolbarItem.icon")); // NOI18N
        saveConversationToolbarItem.setText(resourceMap.getString("saveConversationToolbarItem.text")); // NOI18N
        saveConversationToolbarItem.setFocusable(false);
        saveConversationToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveConversationToolbarItem.setName("saveConversationToolbarItem"); // NOI18N
        saveConversationToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveConversationToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConversationToolbarItemActionPerformed(evt);
            }
        });
        jToolBar2.add(saveConversationToolbarItem);

        ClearFieldToolbarItem.setIcon(resourceMap.getIcon("ClearFieldToolbarItem.icon")); // NOI18N
        ClearFieldToolbarItem.setText(resourceMap.getString("ClearFieldToolbarItem.text")); // NOI18N
        ClearFieldToolbarItem.setFocusable(false);
        ClearFieldToolbarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ClearFieldToolbarItem.setName("ClearFieldToolbarItem"); // NOI18N
        ClearFieldToolbarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ClearFieldToolbarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearFieldToolbarItemActionPerformed(evt);
            }
        });
        jToolBar2.add(ClearFieldToolbarItem);

        colorChooserButton.setIcon(resourceMap.getIcon("colorChooserButton.icon")); // NOI18N
        colorChooserButton.setText(resourceMap.getString("colorChooserButton.text")); // NOI18N
        colorChooserButton.setBorderPainted(false);
        colorChooserButton.setName("colorChooserButton"); // NOI18N
        colorChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorChooserButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                .add(6, 6, 6)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sendButton)
                    .add(colorChooserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 298, Short.MAX_VALUE)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 211, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                        .add(colorChooserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(sendButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        salonConvTab.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(salonConvTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(salonConvTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );

        menuBar.setBackground(resourceMap.getColor("menuBar.background")); // NOI18N
        menuBar.setAutoscrolls(true);
        menuBar.setName("menuBar"); // NOI18N

        FileMenu.setText(resourceMap.getString("FileMenu.text")); // NOI18N
        FileMenu.setName("FileMenu"); // NOI18N

        PrefMenuItem.setIcon(resourceMap.getIcon("PrefMenuItem.icon")); // NOI18N
        PrefMenuItem.setText(resourceMap.getString("PrefMenuItem.text")); // NOI18N
        PrefMenuItem.setName("PrefMenuItem"); // NOI18N
        PrefMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrefMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(PrefMenuItem);

        saveConvMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveConvMenu.setIcon(resourceMap.getIcon("saveConvMenu.icon")); // NOI18N
        saveConvMenu.setText(resourceMap.getString("saveConvMenu.text")); // NOI18N
        saveConvMenu.setName("saveConvMenu"); // NOI18N
        saveConvMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConvMenuActionPerformed(evt);
            }
        });
        FileMenu.add(saveConvMenu);

        browseServeurMenu.setIcon(resourceMap.getIcon("browseServeurMenu.icon")); // NOI18N
        browseServeurMenu.setText(resourceMap.getString("browseServeurMenu.text")); // NOI18N
        browseServeurMenu.setName("browseServeurMenu"); // NOI18N
        browseServeurMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseServeurMenuMenuActionPerformed(evt);
            }
        });
        FileMenu.add(browseServeurMenu);

        jSeparator2.setName("jSeparator2"); // NOI18N
        FileMenu.add(jSeparator2);

        disconnectMenu.setIcon(resourceMap.getIcon("disconnectMenu.icon")); // NOI18N
        disconnectMenu.setText(resourceMap.getString("disconnectMenu.text")); // NOI18N
        disconnectMenu.setName("disconnectMenu"); // NOI18N
        disconnectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMenuActionPerformed(evt);
            }
        });
        FileMenu.add(disconnectMenu);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getActionMap(ClientChatView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setIcon(resourceMap.getIcon("exitMenuItem.icon")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        FileMenu.add(exitMenuItem);

        menuBar.add(FileMenu);

        EditMenu.setText(resourceMap.getString("EditMenu.text")); // NOI18N
        EditMenu.setName("EditMenu"); // NOI18N

        clearFieldMenu.setIcon(resourceMap.getIcon("clearFieldMenu.icon")); // NOI18N
        clearFieldMenu.setText(resourceMap.getString("clearFieldMenu.text")); // NOI18N
        clearFieldMenu.setName("clearFieldMenu"); // NOI18N
        clearFieldMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldMenuActionPerformed(evt);
            }
        });
        EditMenu.add(clearFieldMenu);

        changeUsernameMenu.setIcon(resourceMap.getIcon("changeUsernameMenu.icon")); // NOI18N
        changeUsernameMenu.setText(resourceMap.getString("changeUsernameMenu.text")); // NOI18N
        changeUsernameMenu.setName("changeUsernameMenu"); // NOI18N
        changeUsernameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeUsernameMenuActionPerformed(evt);
            }
        });
        EditMenu.add(changeUsernameMenu);

        closeTab.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        closeTab.setIcon(resourceMap.getIcon("closeTab.icon")); // NOI18N
        closeTab.setText(resourceMap.getString("closeTab.text")); // NOI18N
        closeTab.setName("closeTab"); // NOI18N
        closeTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTabMenuActionPerformed(evt);
            }
        });
        EditMenu.add(closeTab);

        menuBar.add(EditMenu);

        ViewMenu.setText(resourceMap.getString("ViewMenu.text")); // NOI18N
        ViewMenu.setName("ViewMenu"); // NOI18N

        fullScreenMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        fullScreenMenu.setText(resourceMap.getString("fullScreenMenu.text")); // NOI18N
        fullScreenMenu.setIcon(resourceMap.getIcon("fullScreenMenu.icon")); // NOI18N
        fullScreenMenu.setName("fullScreenMenu"); // NOI18N
        fullScreenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullScreenMenuActionPerformed(evt);
            }
        });
        ViewMenu.add(fullScreenMenu);

        changeColorMenu.setIcon(resourceMap.getIcon("changeColorMenu.icon")); // NOI18N
        changeColorMenu.setText(resourceMap.getString("changeColorMenu.text")); // NOI18N
        changeColorMenu.setFocusable(true);
        changeColorMenu.setName("changeColorMenu"); // NOI18N
        changeColorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeColorMenuActionPerformed(evt);
            }
        });
        ViewMenu.add(changeColorMenu);

        logMenuItem.setIcon(resourceMap.getIcon("logMenuItem.icon")); // NOI18N
        logMenuItem.setText(resourceMap.getString("logMenuItem.text")); // NOI18N
        logMenuItem.setName("logMenuItem"); // NOI18N
        logMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(logMenuItem);

        menuBar.add(ViewMenu);

        HelpMenu.setAction(actionMap.get("showAboutBox")); // NOI18N
        HelpMenu.setText(resourceMap.getString("HelpMenu.text")); // NOI18N
        HelpMenu.setName("HelpMenu"); // NOI18N

        menuAbout.setAction(actionMap.get("showAboutBox")); // NOI18N
        menuAbout.setIcon(resourceMap.getIcon("menuAbout.icon")); // NOI18N
        menuAbout.setText(resourceMap.getString("menuAbout.text")); // NOI18N
        menuAbout.setName("menuAbout"); // NOI18N
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        HelpMenu.add(menuAbout);

        jSeparator1.setName("jSeparator1"); // NOI18N
        HelpMenu.add(jSeparator1);

        reportMenuItem.setIcon(resourceMap.getIcon("reportMenuItem.icon")); // NOI18N
        reportMenuItem.setText(resourceMap.getString("reportMenuItem.text")); // NOI18N
        reportMenuItem.setName("reportMenuItem"); // NOI18N
        reportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(reportMenuItem);

        menuBar.add(HelpMenu);

        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        popupContactList.setAutoscrolls(true);
        popupContactList.setName("popupContactList"); // NOI18N

        menuPrivateConversation.setIcon(resourceMap.getIcon("menuPrivateConversation.icon")); // NOI18N
        menuPrivateConversation.setText(resourceMap.getString("menuPrivateConversation.text")); // NOI18N
        menuPrivateConversation.setComponentPopupMenu(popupContactList);
        menuPrivateConversation.setFocusPainted(true);
        menuPrivateConversation.setName("menuPrivateConversation"); // NOI18N
        menuPrivateConversation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPrivateConversationActionPerformed(evt);
            }
        });
        popupContactList.add(menuPrivateConversation);

        callContact.setIcon(resourceMap.getIcon("callContact.icon")); // NOI18N
        callContact.setText(resourceMap.getString("callContact.text")); // NOI18N
        callContact.setToolTipText(resourceMap.getString("callContact.toolTipText")); // NOI18N
        callContact.setName("callContact"); // NOI18N
        callContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callContactActionPerformed(evt);
            }
        });
        popupContactList.add(callContact);

        getInfoMenuItem.setIcon(resourceMap.getIcon("getInfoMenuItem.icon")); // NOI18N
        getInfoMenuItem.setText(resourceMap.getString("getInfoMenuItem.text")); // NOI18N
        getInfoMenuItem.setName("getInfoMenuItem"); // NOI18N
        getInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoMenuItemActionPerformed(evt);
            }
        });
        popupContactList.add(getInfoMenuItem);

        sendFileMenu.setIcon(resourceMap.getIcon("sendFileMenu.icon")); // NOI18N
        sendFileMenu.setText(resourceMap.getString("sendFileMenu.text")); // NOI18N
        sendFileMenu.setActionCommand(resourceMap.getString("sendFileMenu.actionCommand")); // NOI18N
        sendFileMenu.setFocusCycleRoot(true);
        sendFileMenu.setFocusPainted(true);
        sendFileMenu.setFocusTraversalPolicyProvider(true);
        sendFileMenu.setFocusable(true);
        sendFileMenu.setName("sendFileMenu"); // NOI18N
        sendFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileMenuActionPerformed(evt);
            }
        });
        popupContactList.add(sendFileMenu);

        browseMenuItem.setIcon(resourceMap.getIcon("browseMenuItem.icon")); // NOI18N
        browseMenuItem.setText(resourceMap.getString("browseMenuItem.text")); // NOI18N
        browseMenuItem.setFocusPainted(true);
        browseMenuItem.setName("browseMenuItem"); // NOI18N
        browseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMenuItemActionPerformed(evt);
            }
        });
        popupContactList.add(browseMenuItem);

        shareScreenMenuItem.setIcon(resourceMap.getIcon("shareScreenMenuItem.icon")); // NOI18N
        shareScreenMenuItem.setText(resourceMap.getString("shareScreenMenuItem.text")); // NOI18N
        shareScreenMenuItem.setName("shareScreenMenuItem"); // NOI18N
        shareScreenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareScreenMenuItemActionPerformed(evt);
            }
        });
        popupContactList.add(shareScreenMenuItem);

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        closePopup.setName("closePopup"); // NOI18N

        close.setIcon(resourceMap.getIcon("close.icon")); // NOI18N
        close.setText(resourceMap.getString("close.text")); // NOI18N
        close.setName("close"); // NOI18N
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTabMenuActionPerformed(evt);
            }
        });
        closePopup.add(close);

        saveConv.setIcon(resourceMap.getIcon("saveConv.icon")); // NOI18N
        saveConv.setText(resourceMap.getString("saveConv.text")); // NOI18N
        saveConv.setName("saveConv"); // NOI18N
        saveConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConvMenuActionPerformed(evt);
            }
        });
        closePopup.add(saveConv);

        clearField.setIcon(resourceMap.getIcon("clearField.icon")); // NOI18N
        clearField.setText(resourceMap.getString("clearField.text")); // NOI18N
        clearField.setName("clearField"); // NOI18N
        clearField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldActionPerformed(evt);
            }
        });
        closePopup.add(clearField);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void disconnectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectMenuActionPerformed
        int res = JOptionPane.showConfirmDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Are_you_sure_you_want_to_disconnect?"));
        if (res == 0) {
            sendInt(ChatConstants.DISCONNECTION);
        }
}//GEN-LAST:event_disconnectMenuActionPerformed
    /**
     * envoi le message tape quand on appuie sur entrer
     * @param evt
     */
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        if (popupContactList.isVisible()) {
            popupContactList.setVisible(false);
        }
        if (!messageField.getText().equals("")) {
            sendInt(ChatConstants.PUBLIC_CONV);
            sendInt(textColor);
            sendText(messageField.getText());
            messageField.setText("");
        }
}//GEN-LAST:event_sendButtonActionPerformed

    /**
     * ouvre un popup quand on fait un clic droit sur la liste de contact ou ouvre une conversation avec l'utilisateur quand on double clic dessus
     * @param evt
     */
    private void contactJListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactJListMouseClicked
        int index;
        if (evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1) {
            index = contactJList.getSelectedIndex();
            String userClicked = listModel.getElementAt(index).toString();
            if (!userClicked.equals(nickname)) {
                createTabPane(userClicked);
                salonConvTab.setSelectedIndex(salonConvTab.getTabCount() - 1);
            } else {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("You_cannot_chat_with_yourself!"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Error"), 1);
            }
        }
}//GEN-LAST:event_contactJListMouseClicked

    private void closeTabMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeTabMenuActionPerformed
        int index = salonConvTab.getSelectedIndex();
        int res;
        if (index != 0) {
            WhispPane whisp = (WhispPane)pane.get(index-1);
            res =JOptionPane.showConfirmDialog(null, "Are you sure you want to close this conversation?\n This will end crypted conversation with the user");
            if(res==JOptionPane.OK_OPTION){
                whisp.disposeSecure();
                salonConvTab.remove(index);
                pane.remove(index - 1);
            }
        } else {
            res = JOptionPane.showConfirmDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Are_you_sure_you_want_to_exit?"));
            if (res == 0) {
                salonConvTab.remove(index);
                sendInt(ChatConstants.DISCONNECTION);
                System.exit(0);
            }
        }
}//GEN-LAST:event_closeTabMenuActionPerformed

    private void salonConvTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salonConvTabMouseClicked
        if (popupContactList.isVisible()) {
            popupContactList.setVisible(false);
        } else if (evt.getButton() == MouseEvent.BUTTON2) {
            closeTabMenuActionPerformed(null);
        }
    }//GEN-LAST:event_salonConvTabMouseClicked

    private void messageFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_messageFieldMouseClicked
        if (popupContactList.isVisible()) {
            popupContactList.setVisible(false);
        }
    }//GEN-LAST:event_messageFieldMouseClicked

    /**
     * Methode permettant le transfert de fichiers entre deux clients
     * @param evt event du click sur l'item send file du popup menu
     */
    private void sendFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileMenuActionPerformed
        popupContactList.setVisible(false);
        int index = contactJList.getSelectedIndex();
        String userClicked = listModel.getElementAt(index).toString();
        if (!userClicked.equals(nickname)) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Send"));
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                sendRequestToSendFile(f.length(), f.getName(), f.getAbsolutePath(), userClicked);
            }
        } else {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("You_cannot_send_a_file_to_yourself"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Error"), 1);
        }

    }//GEN-LAST:event_sendFileMenuActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        showAboutBox();
}//GEN-LAST:event_menuAboutActionPerformed

    private void menuPrivateConversationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPrivateConversationActionPerformed
        popupContactList.setVisible(false);
        int index = contactJList.getSelectedIndex();
        String userClicked = listModel.getElementAt(index).toString();
        if (!userClicked.equals(nickname)) {
            createTabPane(userClicked);
            salonConvTab.setSelectedIndex(salonConvTab.getTabCount() - 1);
        } else {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("You_cannot_chat_with_yourself!"), java.util.ResourceBundle.getBundle(getLanguage()).getString("Error"), 1);
        }
    }//GEN-LAST:event_menuPrivateConversationActionPerformed

    private void salonFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salonFieldMouseClicked
        if (popupContactList.isVisible()) {
            popupContactList.setVisible(false);
        }
}//GEN-LAST:event_salonFieldMouseClicked

    private void browseServeurMenuMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseServeurMenuMenuActionPerformed
        browser.setVisible(true);
}//GEN-LAST:event_browseServeurMenuMenuActionPerformed

    private void browseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMenuItemActionPerformed
        popupContactList.setVisible(false);
        browser.setVisible(true);
    }//GEN-LAST:event_browseMenuItemActionPerformed

    @SuppressWarnings("empty-statement")
    private void changeUsernameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeUsernameMenuActionPerformed
        try {
            String newUsername = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle(getLanguage()).getString("Enter_your_new_username"));
            if (!newUsername.equals(null) || !newUsername.equals("") || !newUsername.equals(" ")) {
                sendInt(ChatConstants.CHANGE_USERNAME);
                sendText(newUsername);
            }
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Nullpointer", e);
        }

    }//GEN-LAST:event_changeUsernameMenuActionPerformed

    private void saveConvMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConvMenuActionPerformed
        int index = salonConvTab.getSelectedIndex();
        String contents = null;
        WhispPane p;
        if (index == 0) {
            try {
                contents = styledDocument.getText(0, styledDocument.getLength());
            } catch (BadLocationException ex) {
                System.err.print(ex.getMessage());
            }
            saveConversationInFile(java.util.ResourceBundle.getBundle(getLanguage()).getString("Room_conversation"), contents);
            JOptionPane.showMessageDialog(saveConvMenu, java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_saved..."));
        } else {
            p = (WhispPane) pane.get(index - 1);
            contents = p.getWhispFieldContents();
            saveConversationInFile(salonConvTab.getTitleAt(index), contents);
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Conversation_saved..."));
        }
    }//GEN-LAST:event_saveConvMenuActionPerformed

    private void changeColorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeColorMenuActionPerformed
        Color returnColor = JColorChooser.showDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Choose_the_color"), ChatConstants.noir);
        try {
            if (!returnColor.equals(null)) {
                salonConvTab.setBackground(returnColor);
                mainPanel.setBackground(returnColor);
            }
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Null pointer", e);
        }

}//GEN-LAST:event_changeColorMenuActionPerformed

    /**
     * affiche la fenetre en plein ecran
     * @param evt
     */
    private void fullScreenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullScreenMenuActionPerformed
        if (fullScreenMenu.isSelected()) {//si on veut se mettre en plein ecran
            this.getFrame().dispose();//on detruit la fenetre
            this.getFrame().setUndecorated(true);//on enleve la barre d'etat
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
            }else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
                this.getFrame().setLocation(0, 0);
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();//on affiche la fenetre a la position 0,0
                this.getFrame().setSize(dim);//on lui met la taille de la taille de l'ecran
            } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1 ||
                    System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
                this.getFrame().setLocationByPlatform(true);//on affiche la fenetre en fonction de l'os
                this.getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
            }
            this.getFrame().repaint();//on la recree
            this.getFrame().setVisible(true);//et on la rend visible
        } else {
            this.getFrame().dispose();//on la detruit
            this.getFrame().setUndecorated(false);//on remet la barre d'etat
            this.getFrame().repaint();//on la redessine
            this.getFrame().setVisible(true);//on la rend visible
            this.getFrame().setSize(this.getFrame().getPreferredSize());// et on remet la taille par defaut
        }
}//GEN-LAST:event_fullScreenMenuActionPerformed

    /**
     * efface le contenu du salon field
     * @param evt
     */
    private void clearFieldMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldMenuActionPerformed
        try {
            int index = salonConvTab.getSelectedIndex();
            if (index == 0) {
                styledDocument.remove(0, styledDocument.getLength());
            } else {
                WhispPane p = (WhispPane) pane.get(index - 1);
                p.clearWhispField();
            }
        } catch (BadLocationException ex) {
            ErrorLogger.getLogger().logError("Bas location exception", ex);
        }
    }//GEN-LAST:event_clearFieldMenuActionPerformed

    private void salonConvTabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salonConvTabMousePressed
        showPopup(evt);
}//GEN-LAST:event_salonConvTabMousePressed

    private void salonConvTabMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salonConvTabMouseReleased
        showPopup(evt);
    }//GEN-LAST:event_salonConvTabMouseReleased

    private void clearFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldActionPerformed
        clearFieldMenuActionPerformed(evt);
}//GEN-LAST:event_clearFieldActionPerformed

    private void messageFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageFieldKeyTyped
        if (evt.getKeyChar() == 10) {
            messageField.setText(messageField.getText().replace("\n", ""));
            sendButtonActionPerformed(null);
        } else if (messageField.getText().length() > ChatConstants.FIELD_TEXT_SIZE) {
            messageField.setText(messageField.getText().substring(0, ChatConstants.FIELD_TEXT_SIZE));
        }
    }//GEN-LAST:event_messageFieldKeyTyped

    private void callContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callContactActionPerformed
        int index = contactJList.getSelectedIndex();
        String userClicked = listModel.getElementAt(index).toString();
        if (!userClicked.equals(nickname)) {
            popupContactList.setVisible(false);
            openCallView();
        }
    }//GEN-LAST:event_callContactActionPerformed

    private void contactJListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactJListMousePressed
        if (evt.isPopupTrigger()) {
            Point cursorLocation = evt.getPoint();
            contactJList.setSelectedIndex(contactJList.locationToIndex(evt.getPoint()));
            popupContactList.show(contactJList, cursorLocation.x, cursorLocation.y);
        }
    }//GEN-LAST:event_contactJListMousePressed

    private void contactJListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactJListMouseReleased

        if (evt.isPopupTrigger()) {
            Point cursorLocation = evt.getPoint();
            contactJList.setSelectedIndex(contactJList.locationToIndex(evt.getPoint()));
            popupContactList.show(contactJList, cursorLocation.x, cursorLocation.y);
        }
    }//GEN-LAST:event_contactJListMouseReleased

    private void PrefMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrefMenuItemActionPerformed
        PreferenceDialog prefs = new PreferenceDialog(null, true);
        prefs.setVisible(true);
}//GEN-LAST:event_PrefMenuItemActionPerformed

    private void getInfoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getInfoMenuItemActionPerformed
        try {
            int index = contactJList.getSelectedIndex();
            String userClicked = listModel.getElementAt(index).toString();
            cl.sendInt(ChatConstants.GET_INFO_OF_USER);
            cl.sendText(userClicked);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO while get info", ex);
        }

    }//GEN-LAST:event_getInfoMenuItemActionPerformed

    private void reportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportMenuItemActionPerformed
        JFrame mainFrame = ClientChatApp.getApplication().getMainFrame();
        ReportBugView r = new ReportBugView(mainFrame, true);
        r.setVisible(true);
        r.setLocationRelativeTo(mainFrame);

    }//GEN-LAST:event_reportMenuItemActionPerformed

    private void shareScreenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareScreenMenuItemActionPerformed
        int index = contactJList.getSelectedIndex();
        String userClicked = listModel.getElementAt(index).toString();
        if (!userClicked.equals(nickname)) {
            try {
                /*envoi la demande de partage d'ecran*/
                cl.sendInt(ChatConstants.SHARE_SCREEN_REQUEST);
                cl.sendText(userClicked);
            } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO while share screen", ex);

            }
        }



    }//GEN-LAST:event_shareScreenMenuItemActionPerformed

    private void privateConvToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateConvToolbarItemActionPerformed
        menuPrivateConversationActionPerformed(evt);
}//GEN-LAST:event_privateConvToolbarItemActionPerformed

    private void callToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callToolbarItemActionPerformed
        callContactActionPerformed(evt);
}//GEN-LAST:event_callToolbarItemActionPerformed

    private void shareToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareToolbarItemActionPerformed
        shareScreenMenuItemActionPerformed(evt);
}//GEN-LAST:event_shareToolbarItemActionPerformed

    private void sendFileToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileToolbarItemActionPerformed
        sendFileMenuActionPerformed(evt);
}//GEN-LAST:event_sendFileToolbarItemActionPerformed

    private void getInfoToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getInfoToolbarItemActionPerformed
        getInfoMenuItemActionPerformed(evt);
}//GEN-LAST:event_getInfoToolbarItemActionPerformed

    private void browseToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseToolbarItemActionPerformed
        browseServeurMenuMenuActionPerformed(evt);
}//GEN-LAST:event_browseToolbarItemActionPerformed

    private void saveConversationToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConversationToolbarItemActionPerformed
        saveConvMenuActionPerformed(evt);
    }//GEN-LAST:event_saveConversationToolbarItemActionPerformed

    private void ClearFieldToolbarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearFieldToolbarItemActionPerformed
        clearFieldActionPerformed(evt);
    }//GEN-LAST:event_ClearFieldToolbarItemActionPerformed

    private void salonConvTabStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_salonConvTabStateChanged
        salonConvTab.setBackgroundAt(salonConvTab.getSelectedIndex(),Color.WHITE);
    }//GEN-LAST:event_salonConvTabStateChanged

    private void colorChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorChooserButtonActionPerformed
       Color returnColor = JColorChooser.showDialog(null, java.util.ResourceBundle.getBundle(getLanguage()).getString("Choose_the_color"), ChatConstants.noir);
        try {
            if (!returnColor.equals(null) && !returnColor.equals(Color.WHITE)) {
                this.textColor = returnColor.getRGB();
                PreferencesWrapper.getPreferences().setTextColorPref(textColor);
            }
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("Null pointer while changing color", e);
        }
}//GEN-LAST:event_colorChooserButtonActionPerformed

    private void logMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logMenuItemActionPerformed
        new LoggerView();
}//GEN-LAST:event_logMenuItemActionPerformed

    private void showPopup(java.awt.event.MouseEvent evt) {
        Point loc;
        closePopup.setInvoker(salonConvTab);
        if (evt.isPopupTrigger()) {
            loc = evt.getPoint();
            closePopup.show(this.getFrame(), loc.x, loc.y + 20);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    /**
     * enregistre la conversation dans un fichier
     * @param convName nom de la conversation
     * @param contents contenu de la conversation
     */
    private void saveConversationInFile(String convName, String contents) {
        BufferedWriter out = null;
        try {
            Calendar date = Calendar.getInstance();
            out = new BufferedWriter(new FileWriter(ChatConstants.SAVE_CONVERSATION_FOLDER + convName + ".txt", true));
            out.write(date.getTime().toString());
            out.write("\n");
            out.write(contents);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO while save to file convs", ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
            }
        }
    }

    public static ClientChatView getInstance() {
        return instance;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ClearFieldToolbarItem;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JMenuItem PrefMenuItem;
    private javax.swing.JMenu ViewMenu;
    private javax.swing.JMenuItem browseMenuItem;
    private javax.swing.JMenuItem browseServeurMenu;
    private javax.swing.JButton browseToolbarItem;
    private javax.swing.JMenuItem callContact;
    private javax.swing.JButton callToolbarItem;
    private javax.swing.JMenuItem changeColorMenu;
    private javax.swing.JMenuItem changeUsernameMenu;
    private javax.swing.JMenuItem clearField;
    private javax.swing.JMenuItem clearFieldMenu;
    private javax.swing.JMenuItem close;
    private javax.swing.JPopupMenu closePopup;
    private javax.swing.JMenuItem closeTab;
    private javax.swing.JButton colorChooserButton;
    private javax.swing.JList contactJList;
    private javax.swing.JMenuItem disconnectMenu;
    private javax.swing.JCheckBoxMenuItem fullScreenMenu;
    private javax.swing.JMenuItem getInfoMenuItem;
    private javax.swing.JButton getInfoToolbarItem;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem logMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuPrivateConversation;
    private javax.swing.JTextArea messageField;
    private javax.swing.JPopupMenu popupContactList;
    private javax.swing.JButton privateConvToolbarItem;
    private javax.swing.JMenuItem reportMenuItem;
    private javax.swing.JTabbedPane salonConvTab;
    private javax.swing.JTextPane salonField;
    private javax.swing.JMenuItem saveConv;
    private javax.swing.JMenuItem saveConvMenu;
    private javax.swing.JButton saveConversationToolbarItem;
    private javax.swing.JButton sendButton;
    private javax.swing.JMenuItem sendFileMenu;
    private javax.swing.JButton sendFileToolbarItem;
    private javax.swing.JMenuItem shareScreenMenuItem;
    private javax.swing.JButton shareToolbarItem;
    // End of variables declaration//GEN-END:variables
    private ClientChatAboutBox aboutBox;
}
