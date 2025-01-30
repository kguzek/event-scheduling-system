package pl.papuda.ess.client.home.panels;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.Timer;
import pl.papuda.ess.client.AppPanel;
import pl.papuda.ess.client.Web;

/**
 *
 * @author konrad
 */
public class VerifyEmailPage extends AppPanel {

    private String userEmail = null;

    private Timer timer;
    private Timer resendButtonEnableTimer;

    /**
     * Creates new form VerifyEmailPage
     */
    public VerifyEmailPage() {
        initComponents();
    }

    private void enableResendButton() {
        resendButtonEnableTimer.stop();
        btnVerifyEmailResend.setEnabled(true);
        btnVerifyEmailResend.setText("RESEND");
    }

    private void pollEmailVerification() {
        String errorMessage = "An error occurred while checking the verification state.";
        HttpResponse<String> response;
        try {
            response = Web.sendGetRequest("/auth/email/poll?email=" + userEmail);
        } catch (IOException | InterruptedException ex) {
            lblVerifyEmailError.setText(errorMessage);
            return;
        }
        if (lblVerifyEmailError.getText().equals(errorMessage)) {
            lblVerifyEmailError.setText("");
        }
        if (response.statusCode() != 200) {
            return;
        }
        // Email is verified, continue
        timer.stop();
        switchPage("home");
    }

    public void sendEmailChallengeRequest() {
        HttpResponse<String> response;
        try {
            response = Web.sendPostRequest("/auth/email/challenge", "{\"email\": \"" + userEmail + "\"}");
        } catch (IOException | InterruptedException ex) {
            lblVerifyEmailError.setText("Could not send verification email. Try again later.");
            enableResendButton();
            return;
        }
        if (response.statusCode() == 201) {
            return;
        }
        enableResendButton();
        lblVerifyEmailError
                .setText("An unknown error occurred while sending the verification email. Try again later.");
    }

    private void sendVerificationEmail() {
        DecimalFormat df = new DecimalFormat("#.#");
        btnVerifyEmailResend.setEnabled(false);
        lblVerifyEmailError.setText("");
        Date cooldownStart = new Date();
        resendButtonEnableTimer = new Timer(1000, e -> {
            Date now = new Date();
            Long elapsed = now.getTime() - cooldownStart.getTime();

            if (elapsed >= 60000) {
                enableResendButton();
                return;
            }
            float remaining = Math.round(60000 - elapsed) / 1000;
            btnVerifyEmailResend.setText(df.format(remaining) + " s");
        });
        resendButtonEnableTimer.start();

        new Thread(this::sendEmailChallengeRequest).start();
    }

    public void startEmailVerification(String email) {
        btnVerifyEmailResend.setEnabled(false);
        userEmail = email;
        lblVerifyEmailDescription2.setText("to your address at '" + email + "'.");
        sendVerificationEmail();
        Timer preTimer = new Timer(10000, ev -> {
            timer = new Timer(4000, e -> {
                pollEmailVerification();
            });
            timer.start();
        });
        preTimer.setRepeats(false);
        preTimer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlVerifyEmail = new javax.swing.JPanel();
        lblVerifyEmailHeader = new javax.swing.JLabel();
        lblVerifyEmailDescription = new javax.swing.JLabel();
        lblPromptLogIn1 = new javax.swing.JLabel();
        btnVerifyEmailGoBack = new javax.swing.JButton();
        lblVerifyEmailDescription2 = new javax.swing.JLabel();
        btnVerifyEmailResend = new javax.swing.JButton();
        lblVerifyEmailResend = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblVerifyEmailError = new javax.swing.JTextArea();

        pnlVerifyEmail.setBackground(new java.awt.Color(255, 255, 255));
        pnlVerifyEmail.setMinimumSize(new java.awt.Dimension(400, 500));
        pnlVerifyEmail.setName(""); // NOI18N

        lblVerifyEmailHeader.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblVerifyEmailHeader.setForeground(new java.awt.Color(51, 51, 51));
        lblVerifyEmailHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVerifyEmailHeader.setText("Verify Email");

        lblVerifyEmailDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVerifyEmailDescription.setText("An activation email has been sent to your address at");

        lblPromptLogIn1.setFont(new java.awt.Font("Cantarell", 2, 15)); // NOI18N
        lblPromptLogIn1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPromptLogIn1.setText("Wrong account?");

        btnVerifyEmailGoBack.setText("GO BACK");
        btnVerifyEmailGoBack.setToolTipText("Return to the login page");
        btnVerifyEmailGoBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyEmailGoBackActionPerformed(evt);
            }
        });

        lblVerifyEmailDescription2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVerifyEmailDescription2.setText("somebody@example.com");

        btnVerifyEmailResend.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        btnVerifyEmailResend.setText("RESEND");
        btnVerifyEmailResend.setToolTipText("Resend the verification email");
        btnVerifyEmailResend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyEmailResendActionPerformed(evt);
            }
        });

        lblVerifyEmailResend.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVerifyEmailResend.setText("Still haven't received it? Press the button below.");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        lblVerifyEmailError.setEditable(false);
        lblVerifyEmailError.setBackground(new java.awt.Color(255, 255, 255));
        lblVerifyEmailError.setColumns(20);
        lblVerifyEmailError.setForeground(new java.awt.Color(255, 51, 51));
        lblVerifyEmailError.setLineWrap(true);
        lblVerifyEmailError.setRows(5);
        lblVerifyEmailError.setToolTipText("");
        lblVerifyEmailError.setWrapStyleWord(true);
        lblVerifyEmailError.setFocusable(false);
        lblVerifyEmailError.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(lblVerifyEmailError);

        javax.swing.GroupLayout pnlVerifyEmailLayout = new javax.swing.GroupLayout(pnlVerifyEmail);
        pnlVerifyEmail.setLayout(pnlVerifyEmailLayout);
        pnlVerifyEmailLayout.setHorizontalGroup(
            pnlVerifyEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVerifyEmailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVerifyEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVerifyEmailDescription2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVerifyEmailHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVerifyEmailDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerifyEmailGoBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerifyEmailResend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVerifyEmailResend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPromptLogIn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlVerifyEmailLayout.setVerticalGroup(
            pnlVerifyEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVerifyEmailLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblVerifyEmailHeader)
                .addGap(61, 61, 61)
                .addComponent(lblVerifyEmailDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVerifyEmailDescription2)
                .addGap(50, 50, 50)
                .addComponent(lblVerifyEmailResend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVerifyEmailResend, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPromptLogIn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVerifyEmailGoBack, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnlVerifyEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 494, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnlVerifyEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerifyEmailGoBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyEmailGoBackActionPerformed
        timer.stop();
        switchPage("logIn");
    }//GEN-LAST:event_btnVerifyEmailGoBackActionPerformed

    private void btnVerifyEmailResendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyEmailResendActionPerformed
        sendVerificationEmail();
    }//GEN-LAST:event_btnVerifyEmailResendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerifyEmailGoBack;
    private javax.swing.JButton btnVerifyEmailResend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPromptLogIn1;
    private javax.swing.JLabel lblVerifyEmailDescription;
    private javax.swing.JLabel lblVerifyEmailDescription2;
    private javax.swing.JTextArea lblVerifyEmailError;
    private javax.swing.JLabel lblVerifyEmailHeader;
    private javax.swing.JLabel lblVerifyEmailResend;
    private javax.swing.JPanel pnlVerifyEmail;
    // End of variables declaration//GEN-END:variables
}
