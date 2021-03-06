/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.userdata;

import java.util.Hashtable;
import javax.swing.ImageIcon;

/**
 * interface for future database implementation...
 * Strategy pattern 
 * @author benoitdaccache
 */
public interface UserDataInterface {

    public Hashtable<String, User> getList();

    public void addUser(String name, String surname ,String username, String password, String nickname,String email, char sex, String day, String month, String year);

    public User getUser(String username);

    public void removeUser(String username) throws NullPointerException;

    public void setConnected(String username, boolean b);

    public void setDP(String username, ImageIcon icon);

    public void setNickname(String username, String nickname);

    public void updateUserInfo(User newUser, String username);

    public void updateUsername(String username, String newUsername);

}
