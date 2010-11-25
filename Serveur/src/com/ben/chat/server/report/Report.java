/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.server.report;

import java.io.Serializable;

/**
 *
 * @author bendaccache
 */
public class Report implements Serializable{
    private String type;
    private String report;

    public Report(String type, String report){
        this.type=type;
        this.report=report;
    }

    public String getType(){
        return type;
    }

    public String getReport(){
        return report;
    }

}
