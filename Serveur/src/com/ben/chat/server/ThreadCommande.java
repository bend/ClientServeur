package com.ben.chat.server;
import com.ben.chat.server.report.ReportList;
import com.ben.chat.server.report.ErrorLogger;
import com.ben.crypt.md5.MD5Password;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * classe qui permet la saisie au clavier des commandes
 * @author bendaccache
 */
public class ThreadCommande extends Thread implements Runnable {
    Serveur serveur;
    public ThreadCommande(Serveur serveur){
        this.serveur=serveur;
    }

    @Override
    /**
     * lance le thread qui gerera les entrees au clavier du serveur et executera
     * l'action adequate en fonction du texte entre
     */
    public void run(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String commande;
            System.out.print(">>");
            while ((commande = in.readLine()) != null){
                if (commande.equalsIgnoreCase("exit")) {
                    ErrorLogger.getLogger().log("Server shutdown by root");
                    System.exit(0);
                }else if(commande.equalsIgnoreCase("nbconnected")){
                    System.out.println("number of users connected: "+serveur.getHashtableOfUsers().size());
                }else if(commande.equalsIgnoreCase("nbRegistred")){
                    System.out.println("number of users registred: "+serveur.getNbRegistrated());
                }else if(commande.equalsIgnoreCase("userinfo")){
                    serveur.showConnectedUserInfo();
                    }else if(commande.equalsIgnoreCase("removeUser")){
                        System.out.println("Username");
                        String username = in.readLine();
                        serveur.kickUser(serveur.getNickname(username),"removed");
                        serveur.removeUser(username);
                }else if(commande.length()>=11 && commande.substring(0, 11).equalsIgnoreCase("sendMessage")){
                    try{
                        int indexM = 0,indexP;
                        String msg = null,user;
                        if(commande.contains("-m ")){
                            indexM=commande.lastIndexOf("-m ");
                            msg =commande.substring(indexM+3);
                            if(commande.contains("-u ")){
                                indexP=commande.lastIndexOf("-u ");
                                user=commande.substring(indexP+3, indexM-1);
                                serveur.sendMessageToUser(user,msg);
                            }else serveur.sendMessageToAllUsers(msg);
                        }else System.out.println("Usage: sendMessage -u [username] -m message");
                    }catch(IndexOutOfBoundsException e){System.out.println("Usage: sendMessage -u [username] -m message");}
                }else if(commande.length()>=8 && commande.substring(0, 8).equalsIgnoreCase("kickUser")){
                    try{
                        int indexM = 0,indexP;
                        String cause = null,user = null;
                        if(commande.contains("-c ")){
                            indexM=commande.lastIndexOf("-c ");
                            cause =commande.substring(indexM+3);
                            if(commande.contains("-u ")){
                                indexP=commande.lastIndexOf("-u ");
                                user=commande.substring(indexP+3, indexM-1);
                                serveur.kickUser(user,cause);
                            }else System.out.println("Usage: kickUser -u username [-c cause]");
                        }else System.out.println("Usage: kickUser -u username [-c cause]");
                    }catch(IndexOutOfBoundsException e){ErrorLogger.getLogger().logError("Index out of bound",e);}
                }else if(commande.equalsIgnoreCase("clear")){
                    System.out.print( "\033[H\033[2J" );
                }else if(commande.equalsIgnoreCase("addUser")){
                    System.out.println("Name :");
                    String name = in.readLine();
                    System.out.println("Surname :");
                    String surname = in.readLine();
                    System.out.println("Username: ");
                    String username  = in.readLine();
                    System.out.println("password :");
                    String pass = in.readLine();
                    System.out.println("email :");
                    String mail = in.readLine();
                    System.out.println("Brith Date :");
                    System.out.println("Day ");
                    String day  =in.readLine();
                    System.out.println("Month :");
                    String month = in.readLine();
                    System.out.println("year :");
                    String year = in.readLine();
                    System.out.println("Sex :");
                    String sex = in.readLine();
                    char sex1  = sex.toCharArray()[0];
                    serveur.addUser(name, surname, username, MD5Password.getEncodedPassword(pass), username, mail, sex1, day, month, year);
                }else if(commande.equalsIgnoreCase("buildreports")){
                    ReportList.getInstance().writeToFileByType();
                }else if(commande.equalsIgnoreCase("Help")){
                    System.out.println("Commands:\nnbConnected: shows number of users connected\n" +
                    "nbRegistred: shows number of users registrated\nuserInfo: shows the list of all connected users" +
                    "\nremoveUser\nsendMessage\nremoveUser\nkickUser\naddUser\n");
                }else if(commande.equalsIgnoreCase("listusers")){
                    serveur.listUsers();
                }else if(commande.equalsIgnoreCase("getUserInfo")){
                    System.out.println("username : ");
                    serveur.showDescription(in.readLine());
                }else if(commande.equalsIgnoreCase("")){
                }else{
                    System.out.println("Unknown command");
                }
                System.out.print(">>");
            }
        } catch (IOException ex) {ErrorLogger.getLogger().logError("Error command prompt",ex);
}
    }
}
