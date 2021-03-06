/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ServeurBrowser.java
 * Created on Mar 14, 2009, 12:56:55 PM
 */

package com.ben.chat.client.views;

import com.ben.chat.client.constant.ChatConstants;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author bendaccache
 */
public class ServerBrowserView extends javax.swing.JFrame {
    private ClientChatView cv;
    private ArrayList all;

    /** Creates new form ServeurBrowser */
    public ServerBrowserView(java.awt.Frame parent, boolean modal) {
        super("Serveur contents");
        this.cv=ClientChatView.getInstance();
        initComponents();
        setLanguage();
    }
    /**
     * met a jour jour l'arbre des fichier
     * @param all
     */
    public void updateTree(ArrayList all) {
        this.all=all;
        Object[] hierarchy = new Object[all.size()+1];
        hierarchy[0]="Server";
        for(int i=1;i<all.size()+1;i++){
            hierarchy[i] = all.get(i-1);
        }
        initTree(hierarchy);
    }

    void showFileInfo(String name,String type, long size, ImageIcon icon) {
        FileInfoView fiv = new FileInfoView(null, false);
        fiv.setData(name,type, size, icon);
        fiv.setVisible(true);
    }
    /**
     * parcour le vecteur et construit l'arbre de fichier
     */
     private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
        DefaultMutableTreeNode node =
        new DefaultMutableTreeNode(hierarchy[0]);
        DefaultMutableTreeNode child;
        for(int i=1; i<hierarchy.length; i++) {
            Object nodeSpecifier = hierarchy[i];
                if (nodeSpecifier instanceof Object[]) 
                    child = processHierarchy((Object[])nodeSpecifier);
                else
                    child = new DefaultMutableTreeNode(nodeSpecifier);
            node.add(child);
        }
        return(node);
    }
     /**
      * initialise l'arbre des fichier
      * @param hierarchy
      */
     private void initTree(Object[] hierarchy){
        DefaultMutableTreeNode root = processHierarchy(hierarchy);
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
     }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        getInfoMenuItem = new javax.swing.JMenuItem();
        downloadMenuItem = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        downloadButton = new javax.swing.JButton();
        uploadButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        getInfoItem = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N

        getInfoMenuItem.setName("get Info"); // NOI18N
        getInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(getInfoMenuItem);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(ServerBrowserView.class);
        downloadMenuItem.setText(resourceMap.getString("downloadMenuItem.text")); // NOI18N
        downloadMenuItem.setName("downloadMenuItem"); // NOI18N
        downloadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(downloadMenuItem);

        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tree.setBackground(resourceMap.getColor("tree.background")); // NOI18N
        tree.setModel(null);
        tree.setDragEnabled(true);
        tree.setName("tree"); // NOI18N
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        downloadButton.setIcon(resourceMap.getIcon("downloadButton.icon")); // NOI18N
        downloadButton.setText(resourceMap.getString("downloadButton.text")); // NOI18N
        downloadButton.setName("downloadButton"); // NOI18N
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        uploadButton.setIcon(resourceMap.getIcon("uploadButton.icon")); // NOI18N
        uploadButton.setText(resourceMap.getString("uploadButton.text")); // NOI18N
        uploadButton.setName("uploadButton"); // NOI18N
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        searchField.setText(resourceMap.getString("searchField.text")); // NOI18N
        searchField.setName("searchField"); // NOI18N
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchFieldKeyTyped(evt);
            }
        });

        searchButton.setIcon(resourceMap.getIcon("searchButton.icon")); // NOI18N
        searchButton.setText(resourceMap.getString("searchButton.text")); // NOI18N
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        getInfoItem.setIcon(resourceMap.getIcon("getInfoItem.icon")); // NOI18N
        getInfoItem.setText(resourceMap.getString("getInfoItem.text")); // NOI18N
        getInfoItem.setName("getInfoItem"); // NOI18N
        getInfoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoItemActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, uploadButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, downloadButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(getInfoItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(searchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(downloadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(uploadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(getInfoItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * methode apellee quand on clique sur le bouton download
     * @param evt
     */
    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        try{
            Object[] path =tree.getSelectionPath().getPath();
            if(path.length<3)
                JOptionPane.showMessageDialog(null, "Sorry, you cannot download a complete folder!");
            else{
                cv.confirmDownloadFileFromServer(path);
            }
        }catch (NullPointerException e){JOptionPane.showMessageDialog(null, "You must select a item!");}
    }//GEN-LAST:event_downloadButtonActionPerformed
    /**
     * methode apellee quand on clique sur le bouton upload
     * @param evt
     */
    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showDialog(null, "Send");
        if(returnVal==JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            cv.confirmUploadFileToServer(f);
            }
}//GEN-LAST:event_uploadButtonActionPerformed
    /**
     * methode apellee quand on clique sur le bouton Search et recherche le fichier que l'on recherche et le
     * selectionne dans l'aroberecense
     * @param evt
     */
    @SuppressWarnings("empty-statement")
    /**
     * fonction qui cherche une valeur dans l'arbre
     * on pourrait ajouter ici les strings metrics pour plus defficacite et un module qui cherhce toutes les valeurs
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String searched = searchField.getText();
        if(!searched.equals("")){
            int pos = 0;
            int hierarchy = 0;
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                String[] s = (String[]) all.get(i);
                for (int j = 0; j < s.length; j++) {
                    if (s[j].equalsIgnoreCase(searched)) {/*on regarde si le nom existe en pleine lettre*/
                        pos = i;
                        hierarchy = j;
                        found = true;
                        break;
                    }
                    if (found) {
                        break;
                    }
                }
            }
            if (!found) {
                for (int i = 0; i < all.size(); i++) {
                    String[] s = (String[]) all.get(i);
                    for (int j = 0; j < s.length; j++) {
                        if (s[j].toLowerCase().contains(searched.toLowerCase())) {//sinon on regarde si une partie de ce mot existe dans un nom de fichier
                            pos = i;
                            hierarchy = j;
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (found) {
                searchField.setBackground(Color.blue);
                TreeModel m = tree.getModel();
                tree.setModel(null);
                tree.setModel(m);
                tree.setSelectionRow(pos + 1);
                tree.expandPath(tree.getSelectionPath());
                tree.setSelectionRow(pos + hierarchy + 1);
            }
            if (!found) {
                searchField.setBackground(Color.RED);
            }
        }
}//GEN-LAST:event_searchButtonActionPerformed

    private void searchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyTyped
        searchField.setBackground(Color.white);
        if(evt.getKeyChar()==KeyEvent.VK_ENTER)
            searchButtonActionPerformed(null);
    }//GEN-LAST:event_searchFieldKeyTyped

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        if(evt.getButton()==MouseEvent.BUTTON3){
            tree.setSelectionPath(tree.getClosestPathForLocation(evt.getX(),evt.getY()));
            popupMenu.show(tree, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_treeMouseClicked

    private void downloadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadMenuItemActionPerformed
        downloadButtonActionPerformed(evt);
    }//GEN-LAST:event_downloadMenuItemActionPerformed

    private void getInfoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getInfoMenuItemActionPerformed
         try{
            Object[] path =tree.getSelectionPath().getPath();
            if(path.length<3)
                JOptionPane.showMessageDialog(null, "Sorry, you cannot get the info of a complete folder!");
            else{
                String currentPath = "";
                for (int i = 1; i < path.length; i++) {
                    currentPath += "/" + path[i];
                }
                 cv.sendInt(ChatConstants.GET_FILE_INFO);
                 cv.sendText(currentPath);
            }
        }catch (NullPointerException e){JOptionPane.showMessageDialog(null, "You must select a item!");}
    }//GEN-LAST:event_getInfoMenuItemActionPerformed

    private void getInfoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getInfoItemActionPerformed
        getInfoMenuItemActionPerformed(evt);
    }//GEN-LAST:event_getInfoItemActionPerformed
    public void setLanguage(){
        downloadButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Download_Button"));
        uploadButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Upload_Button"));
        searchButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Search_Button"));
        downloadMenuItem.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Download_Button"));
        getInfoMenuItem.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("get_info"));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downloadButton;
    private javax.swing.JMenuItem downloadMenuItem;
    private javax.swing.JButton getInfoItem;
    private javax.swing.JMenuItem getInfoMenuItem;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JTree tree;
    private javax.swing.JButton uploadButton;
    // End of variables declaration//GEN-END:variables

}
