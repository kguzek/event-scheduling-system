package pl.papuda.ess.client.components.home.settings;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JRadioButton;
import pl.papuda.ess.client.components.AppPanel;
import pl.papuda.ess.client.tools.AppPreferences;
import pl.papuda.ess.client.tools.Web;

/**
 *
 * @author konrad
 */
public class Settings extends AppPanel {

    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.+[^/]$");
    
    /**
     * Creates new form Settings
     */
    public Settings() {
        initComponents();
        applySettings();
    }

    private void setNotificationMethod(String method) {
        String json = "{\"notificationMethod\":\"" + method + "\"}";
        HttpResponse<String> response;
        try {
            response = Web.sendPatchRequest("/private/user/me", json);
        } catch (IOException | InterruptedException ex) {
            System.err.println("PATCH failed: " + ex);
            showErrorPopup("Could not update the preferred notification method. Try again later.", "Operation failed");
            return;
        }
        if (response.statusCode() != 200) {
            showErrorPopup(Web.getErrorMessage(response), "Operation failed");
            return;
        }
        showInfoPopup("Successfully changed preferred notification method to " + method, "Success");
        Web.user.setPreferredNotificationMethod(method);
        
    }

    private void applySettings() {
        iptApiUrl.setText(Web.getApiUrl());
    }

    private void requestStaffRole() {
        btnRequestStaffRole.setEnabled(false);
        HttpResponse response;
        try {
            response = Web.sendPostRequest("/private/permissions/roles/staff");
        } catch (IOException | InterruptedException ex) {
            showErrorPopup(ex.getMessage(), "Error requesting role elevation");
            return;
        } finally {
            btnRequestStaffRole.setEnabled(true);
        }
        String message = Web.getErrorMessage(response);
        if (response.statusCode() != 200) {
            showErrorPopup(message, "Role elevation denied");
            return;
        }
        showInfoPopup(message, "Role elevation requested");
//        System.out.println("Successfully sent role elevation request emails");
    }

    public void onUserPermissionsEstablished(String role) {
        btnRequestStaffRole.setEnabled("USER".equals(role));
        
        String notificationMethod = Web.user.getPreferredNotificationMethod();
        if (notificationMethod == null) {
            notificationMethod = "POPUP_AND_EMAIL";
        }
        JRadioButton radioButton = switch (notificationMethod) {
            case "POPUP" ->
                rdbNotificationMethodPopup;
            case "EMAIL" ->
                rdbNotificationMethodEmail;
            case "POPUP_AND_EMAIL" ->
                rdbNotificationMethodEmailAndPopup;
            default ->
                rdbNotificationMethodEmailAndPopup;
        };
        System.out.println("Preferred notification method: " + notificationMethod);
        radioButton.setSelected(true);
    }
    
    private void updateApiUrl(String newUrl) {
        if (newUrl.isBlank()) {
            AppPreferences.unset("apiUrl");
            showInfoPopup("The API URL has been reset to its default value.\nRestart the application for it to take effect.", "Success");
            return;
        }
        Matcher matcher = URL_PATTERN.matcher(newUrl);
        if (!matcher.find()) {
            showErrorPopup("The specified URL must start with http:// or https:// and cannot end with a slash.", "Invalid URL");
            return;
        }
        AppPreferences.set("apiUrl", newUrl);
        String message = String.format("The API URL has been set to %s.\nRestart the application for it to take effect.", newUrl);
        showInfoPopup(message, "Success");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbgNotificationMethod = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        rdbNotificationMethodPopup = new javax.swing.JRadioButton();
        rdbNotificationMethodEmail = new javax.swing.JRadioButton();
        rdbNotificationMethodEmailAndPopup = new javax.swing.JRadioButton();
        btnRequestStaffRole = new javax.swing.JButton();
        lblUserRole = new javax.swing.JLabel();
        lblApiUrl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        iptApiUrl = new javax.swing.JTextField();
        btnUpdateApiUrl = new javax.swing.JButton();

        jLabel1.setText("Preferred notification method");

        rbgNotificationMethod.add(rdbNotificationMethodPopup);
        rdbNotificationMethodPopup.setText("In-app popup");
        rdbNotificationMethodPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbNotificationMethodPopupActionPerformed(evt);
            }
        });

        rbgNotificationMethod.add(rdbNotificationMethodEmail);
        rdbNotificationMethodEmail.setText("Email");
        rdbNotificationMethodEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbNotificationMethodEmailActionPerformed(evt);
            }
        });

        rbgNotificationMethod.add(rdbNotificationMethodEmailAndPopup);
        rdbNotificationMethodEmailAndPopup.setText("Both");
        rdbNotificationMethodEmailAndPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbNotificationMethodEmailAndPopupActionPerformed(evt);
            }
        });

        btnRequestStaffRole.setText("Request staff privileges");
        btnRequestStaffRole.setEnabled(false);
        btnRequestStaffRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestStaffRoleActionPerformed(evt);
            }
        });

        lblUserRole.setText("User role");

        lblApiUrl.setText("API URL (requires restart)");

        iptApiUrl.setText("https://asdfsd");
        iptApiUrl.setToolTipText("The API URL to use by the application");

        btnUpdateApiUrl.setText("Update");
        btnUpdateApiUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateApiUrlActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rdbNotificationMethodPopup)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdbNotificationMethodEmail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdbNotificationMethodEmailAndPopup))
                            .addComponent(btnRequestStaffRole)
                            .addComponent(lblUserRole)
                            .addComponent(lblApiUrl))
                        .addGap(0, 174, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iptApiUrl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateApiUrl)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbNotificationMethodPopup)
                    .addComponent(rdbNotificationMethodEmail)
                    .addComponent(rdbNotificationMethodEmailAndPopup))
                .addGap(18, 18, 18)
                .addComponent(lblUserRole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRequestStaffRole)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblApiUrl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iptApiUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateApiUrl))
                .addContainerGap(101, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rdbNotificationMethodPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbNotificationMethodPopupActionPerformed
        setNotificationMethod("POPUP");
    }//GEN-LAST:event_rdbNotificationMethodPopupActionPerformed

    private void rdbNotificationMethodEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbNotificationMethodEmailActionPerformed
        setNotificationMethod("EMAIL");
    }//GEN-LAST:event_rdbNotificationMethodEmailActionPerformed

    private void rdbNotificationMethodEmailAndPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbNotificationMethodEmailAndPopupActionPerformed
        setNotificationMethod("POPUP_AND_EMAIL");
    }//GEN-LAST:event_rdbNotificationMethodEmailAndPopupActionPerformed

    private void btnRequestStaffRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestStaffRoleActionPerformed
        new Thread(this::requestStaffRole).start();
    }//GEN-LAST:event_btnRequestStaffRoleActionPerformed

    private void btnUpdateApiUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateApiUrlActionPerformed
        updateApiUrl(iptApiUrl.getText());
    }//GEN-LAST:event_btnUpdateApiUrlActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRequestStaffRole;
    private javax.swing.JButton btnUpdateApiUrl;
    private javax.swing.JTextField iptApiUrl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblApiUrl;
    private javax.swing.JLabel lblUserRole;
    private javax.swing.ButtonGroup rbgNotificationMethod;
    private javax.swing.JRadioButton rdbNotificationMethodEmail;
    private javax.swing.JRadioButton rdbNotificationMethodEmailAndPopup;
    private javax.swing.JRadioButton rdbNotificationMethodPopup;
    // End of variables declaration//GEN-END:variables
}
