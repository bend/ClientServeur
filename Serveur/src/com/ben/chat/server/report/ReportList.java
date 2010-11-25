package com.ben.chat.server.report;


import com.ben.chat.server.constants.ChatConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bendaccache
 */
public class ReportList {

    private Hashtable<String, ArrayList<Report>> list;
    public static ReportList instance;

    public ReportList() {
        refreshList();
        instance = this;
        Serialize();
    }

    public static ReportList getInstance() {
        return instance;
    }

    private Hashtable<String, ArrayList<Report>> refreshList() {
        list = new Hashtable<String, ArrayList<Report>>();
        FileInputStream fisuser;
        ObjectInputStream oisuser;
        try {
            fisuser = new FileInputStream(ChatConstants.PathToReport);
            oisuser = new ObjectInputStream(fisuser);
            list = (Hashtable<String, ArrayList<Report>>) oisuser.readObject();
            oisuser.close();
            fisuser.close();
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("", ex);
        } catch (IOException ex) {
            list = new Hashtable<String, ArrayList<Report>>();
        }
        return list;
    }

    public void addReport(String type, String report) {
        try {
            if (list.get(type) == null)
                list.put(type, new ArrayList<Report>());
            list.get(type).add(new Report(type, report));
            Serialize();

        } catch (NullPointerException e) {
            list.put(type, new ArrayList<Report>()).add(new Report(type, report));
            Serialize();
        }
    }

    private void Serialize() {
        FileOutputStream fos;
        ObjectOutputStream oos;
        Object user = null;
        try {
            File f = new File(ChatConstants.PathToReport);
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("", ex);
        }
    }

    public void writeToFileByType() throws IOException {
        Enumeration e = list.elements();
        while (e.hasMoreElements()) {
            ArrayList<Report> array = (ArrayList<Report>) e.nextElement();
            File f = new File(ChatConstants.PathToReportDirectory + ((Report) array.get(0)).getType() + ".txt");
            FileWriter fw = new FileWriter(f);
            for (int i = 0; i < array.size(); i++) {
                fw.write(array.get(i).getReport() + "\n");
            }
            fw.close();
        }
    }
}
