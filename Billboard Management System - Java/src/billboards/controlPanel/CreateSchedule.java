package billboards.controlPanel;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import billboards.shared.Schedule;
import billboards.shared.TCPMessage;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 *
 * @author JN
 */
public class CreateSchedule extends javax.swing.JFrame {

    String[][] billboardData;
    HashMap<String, String> hmapBillboard = new HashMap<>();
    HashMap<String, Integer> hmapDuration = new HashMap<>();
    String token;

    /**
     * Creates new form CreateScheduleForm
     */
    public CreateSchedule(String[][] billboardData, String sessionToken) {
        for(String[] s : billboardData){
            hmapBillboard.put(s[1],s[4]);
        }
        hmapDuration.put("1 minute", 1);
        hmapDuration.put("5 minutes", 5);
        hmapDuration.put("10 minutes", 10);
        hmapDuration.put("30 minutes", 30);
        hmapDuration.put("1 hour",60);
        hmapDuration.put("2 hours",120);
        this.token = sessionToken;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        cboBillboards = new javax.swing.JComboBox<>();
        radNow = new javax.swing.JRadioButton();
        radSpecific = new javax.swing.JRadioButton();
        radReoccur = new javax.swing.JRadioButton();
        cboReoccur = new javax.swing.JComboBox<>();
        lblDisplay = new javax.swing.JLabel();
        lblScheduleAt = new javax.swing.JLabel();
        lblBillboard = new javax.swing.JLabel();
        cboDisplayFor = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        spinDateTime = new javax.swing.JSpinner( new SpinnerDateModel());
        //spinDateTime.setEditor(new JSpinner.DateEditor(spinDateTime,"yyyy-MM-dd HH:mm:ss"));
dateEditor = new JSpinner.DateEditor(spinDateTime, "yyyy-MM-dd HH:mm:ss");
        dateEditor.getTextField().setEditable( true );
        spinDateTime.setEditor(dateEditor);

        spinDateTime.setEnabled(false);
        radSpecific.setSelected(false);
        radReoccur.setSelected(false);
        cboReoccur.setEnabled(false);


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        cboBillboards.setModel(new javax.swing.DefaultComboBoxModel(hmapBillboard.keySet().toArray()));

        radNow.setSelected(true);
        radNow.setText("Now");
        radNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNowActionPerformed(evt);
            }
        });

        radSpecific.setText("Specific time");
        radSpecific.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radSpecificActionPerformed(evt);
            }
        });

        radReoccur.setText("Re-occurring");
        radReoccur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radReoccurActionPerformed(evt);
            }
        });

        cboReoccur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Every Minute", "Every 5 Minutes", "Every 30 Minutes", "Hourly", "Daily" }));

        lblDisplay.setText("Display for:");

        lblScheduleAt.setText("Schedule at:");

        lblBillboard.setText("Billboard:");

        cboDisplayFor.setModel(new javax.swing.DefaultComboBoxModel(hmapDuration.keySet().toArray()));

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblScheduleAt)
                                        .addComponent(cboDisplayFor, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cboBillboards, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblBillboard)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(2, 2, 2)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(radNow)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(radSpecific))
                                                                                .addComponent(lblDisplay))
                                                                        .addGap(18, 18, 18)
                                                                        .addComponent(radReoccur))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(spinDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(cboReoccur, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(lblBillboard)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboBillboards, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(lblScheduleAt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(radNow)
                                        .addComponent(radSpecific)
                                        .addComponent(radReoccur))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cboReoccur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spinDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDisplay)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboDisplayFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                                        .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void radNowActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        spinDateTime.setEnabled(false);
        radSpecific.setSelected(false);
        radReoccur.setSelected(false);
        cboReoccur.setEnabled(false);
    }

    private void radSpecificActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        radNow.setSelected(false);
        radReoccur.setSelected(false);
        spinDateTime.setEnabled(true);
        cboReoccur.setEnabled(false);
    }

    private void radReoccurActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        radNow.setSelected(false);
        radSpecific.setSelected(false);
        spinDateTime.setEnabled(true);
        cboReoccur.setEnabled(true);
    }

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if(radNow.isSelected()){
            String spinDate = dateEditor.getTextField().getText();
            String value = cboBillboards.getSelectedItem().toString();
            int duration = hmapDuration.get(cboDisplayFor.getSelectedItem().toString());
            String schedId = UUID.randomUUID().toString();
            String message = TCPMessage.build(TCPMessage.type.REQUEST_SCHEDULE,schedId,LocalDateTime.now().toString(),duration+"",hmapBillboard.get(value),token);
            System.out.println(message);
            String response = ServerCommunication.getResponse(message);
            String[] fields = TCPMessage.receive(response);
            if(Integer.parseInt(fields[0]) == 1000){
                StartControlPanel.bcp.UpdateTable(2);
                this.dispose();
            }
            else{
                MessageBox.infoBox(fields[1],"Error");
            }
        }else if(radSpecific.isSelected()){
            String spinDate = dateEditor.getTextField().getText();
            String value = cboBillboards.getSelectedItem().toString();
            int duration = hmapDuration.get(cboDisplayFor.getSelectedItem().toString());
            String schedId = UUID.randomUUID().toString();
            String message = TCPMessage.build(TCPMessage.type.REQUEST_SCHEDULE,schedId,spinDate,duration+"",hmapBillboard.get(value),token);
            System.out.println(message);
            String response = ServerCommunication.getResponse(message);
            String[] fields = TCPMessage.receive(response);
            if(Integer.parseInt(fields[0]) == 1000){
                StartControlPanel.bcp.UpdateTable(2);
                this.dispose();
            }
            else{
                MessageBox.infoBox(fields[1],"Error");
            }
        }else if(radReoccur.isSelected()){
            String value = cboBillboards.getSelectedItem().toString();
            String spinDate = dateEditor.getTextField().getText();
            LocalDateTime ldt = Schedule.getDateTime(spinDate);
            int success = 0;
            for(int i = 0; i < 10; i ++){
                int duration = hmapDuration.get(cboDisplayFor.getSelectedItem().toString());
                String schedId = UUID.randomUUID().toString();
                String message = TCPMessage.build(TCPMessage.type.REQUEST_SCHEDULE,schedId,ldt.toString(),duration+"",hmapBillboard.get(value),token);
                String response = ServerCommunication.getResponse(message);
                String[] fields = TCPMessage.receive(response);
                success = Integer.parseInt(fields[0]);
                if(Integer.parseInt(fields[0]) == 1001){
                    MessageBox.infoBox(fields[1],"Error");
                    break;
                }
                //"Every Minute", "Every 5 Minutes", "Every 30 Minutes", "Hourly", "Daily"
                String reOccur = cboReoccur.getSelectedItem().toString();
                if(reOccur.equals("Every Minute")){
                    ldt.plusMinutes(1);
                }else if(reOccur.equals("Every 30 Minutes")){
                    ldt.plusMinutes(30);
                }else if(reOccur.equals("Hourly")){
                    ldt.plusHours(1);
                }else if(reOccur.equals("Daily")){
                    ldt.plusDays(1);
                }
            }
            if(success == 1000){
                StartControlPanel.bcp.UpdateTable(2);
                this.dispose();
            }
        }


    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<String> cboBillboards;
    private javax.swing.JComboBox<String> cboDisplayFor;
    private javax.swing.JComboBox<String> cboReoccur;
    private javax.swing.JLabel lblBillboard;
    private javax.swing.JLabel lblDisplay;
    private javax.swing.JLabel lblScheduleAt;
    private javax.swing.JRadioButton radNow;
    private javax.swing.JRadioButton radReoccur;
    private javax.swing.JRadioButton radSpecific;
    private javax.swing.JSpinner spinDateTime;
    private JSpinner.DateEditor dateEditor;
    // End of variables declaration
}