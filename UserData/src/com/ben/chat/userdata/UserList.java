package com.ben.chat.userdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bendaccache
 */
public class UserList implements UserDataInterface, Serializable{

    public Hashtable<String, User> listUserByUsername;
    public Hashtable<String, String> listUserByEmail;//Hashtable<email, usernamre>

    public UserList() {
        RefreshList();
    }

    private Hashtable<String, User> RefreshList() {
        listUserByUsername = new Hashtable<String, User>();
        FileInputStream fisuser;
        ObjectInputStream oisuser;
        try {
            fisuser = new FileInputStream("../conf/listUser.dat");
            oisuser = new ObjectInputStream(fisuser);
            listUserByUsername = (Hashtable<String, User>) oisuser.readObject();
            oisuser.close();
            fisuser.close();
            fisuser = new FileInputStream("../conf/listEmail.dat");
            oisuser = new ObjectInputStream(fisuser);
            listUserByEmail = (Hashtable<String, String>) oisuser.readObject();
            oisuser.close();
            fisuser.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
           listUserByUsername = new Hashtable<String, User>();
           listUserByEmail = new Hashtable<String,String>();
           SerializeList();
        }
        return listUserByUsername;
    }

    private void SerializeList() {
        FileOutputStream fos;
        ObjectOutputStream oos;
        Object user = null;
        try {
            File f = new File("../conf/listUser.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(listUserByUsername);
            oos.close();
            fos.close();
            f = new File("../conf/listEmail.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(listUserByEmail);
            oos.close();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public Hashtable<String, User> getList() {
        return listUserByUsername;
    }

    public void addUser(String name, String surname ,String username, String password, String nickname,String email, char sex, String day, String month, String year) {
        User nu = new User(name, surname ,username, password, nickname,email,  sex, day, month, year);
        listUserByUsername = RefreshList();
        if(listUserByUsername.get(username)==null){
            if(listUserByEmail.get(email)==null){
                listUserByUsername.put(username, nu);
                listUserByEmail.put(email, name);
                SerializeList();
                }else System.out.println("Email already used");
        }else System.out.println("Username already exist");
    }

    public User getUser(String username) {
        return listUserByUsername.get(username);
    }

    public void removeUser(String username) throws NullPointerException{
        String email = listUserByUsername.get(username).getEmail();
        listUserByUsername.remove(username);
        listUserByEmail.remove(email);
        SerializeList();
    }

    public void setConnected(String username, boolean b) {
        listUserByUsername.get(username).setConnected(b);
        SerializeList();
    }

    public void setDP(String username, ImageIcon icon) {
        listUserByUsername.get(username).setDp(icon);
        SerializeList();
    }

    public void setNickname(String username, String nickname) {
        listUserByUsername.get(username).setNickname(nickname);
        SerializeList();
    }

    public void updateUserInfo(User newUser, String username) {
       ImageIcon icon = listUserByUsername.get(username).getDp();
       listUserByUsername.put(username, newUser);
       listUserByUsername.get(username).setConnected(true);
       listUserByUsername.get(username).setDp(icon);
       SerializeList();
    }

    public void updateUsername(String username, String newUsername) {
        listUserByUsername.get(username).setNickname(newUsername);
        SerializeList();
    }
}
