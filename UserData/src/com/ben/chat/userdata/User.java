/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.userdata;

import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 *
 * @author bendaccache
 */
public class User implements Serializable{

    private String name;
    private String surname;
    private String username;
    private String password;
    private String nickname;
    private String country;
    private String city;
    private String about;
    private String interests;
    private char sex;
    private UserDate birthDate;
    transient private boolean isConnected = false;
    private String email ;
    private String webSiteURI;
    private ImageIcon dp;
    private String phoneNumber;

    public User(String name, String surname ,String login, String pass,  String nickname,String email, char sex,String day, String month, String year){
        this.name=name;
        this.surname=surname;
        this.username = login;
        this.password= pass;
        this.nickname=nickname;
        this.sex=sex;
        birthDate = new UserDate(day, month, year);
        this.email = email;
        about = "";
        interests="";
        city="";
        country="";
        webSiteURI="http://";
    }

    public User(String name, String surname ,String login, String pass,  String nickname,
        String email, char sex,String day, String month, String year,String city,
        String country,String interests, String about, String phoneNumber, String webSiteURi){
        this.name=name;
        this.surname=surname;
        this.username = login;
        this.password= pass;
        this.nickname=nickname;
        this.sex=sex;
        birthDate = new UserDate(day, month, year);
        this.email = email;
        this.about = about;
        this.interests=interests;
        this.city=city;
        this.country=country;
        this.phoneNumber=phoneNumber;
        this.webSiteURI=webSiteURi;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getWebSiteURI(){
        return webSiteURI;
    }

    public void setPhoneNumber(String number){
        this.phoneNumber=number;
    }

    public String getLogin(){
        return username;
    }

     public String getName(){
            return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getEmail(){
        return email;
    }


    public String getPassword(){
        return password;
    }

    public String getNickname(){
        return nickname;
    }

    public char getSex(){
        return sex;
    }

    public String getUsername(){
        return username;
    }

    public UserDate getBirthDate(){
        return birthDate;
    }

    public void setNickname(String nickname){
        this.nickname=nickname;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean b) {
        isConnected = b;
    }

    public String getCountry(){
        return country;
    }

    public String getCity(){
        return city;
    }

    public String getInterest(){
        return interests;
    }

   public String getAbout(){
        return about;
    }

   public void setDp(ImageIcon icon){
       this.dp = icon;
   }

   public ImageIcon getDp(){
       return dp;
   }

   @Override

   public String toString(){
       return "name: "+name +"\nsurname: "+surname+"\nsex: "+sex+"\nbirthdate: "+birthDate+"\nemail: "+email;
   }

}
