/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uk.guzek.ess.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.CardLayout;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import uk.guzek.ess.client.error.LoginException;
import uk.guzek.ess.client.error.web.body.ErrorResponse;
import uk.guzek.ess.client.error.web.body.LoginResponse;

/**
 *
 * @author konrad
 */
public class MainWindow extends javax.swing.JFrame {

    private final String API_URL = "http://localhost:8080/api/v1";
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[#?!@$ %^&*-]).{8,}$");
    
    private final Pattern emailPattern = Pattern.compile("^[^@]+@[^@]+$");
    
    private String accessToken = null;
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        // Custom code
        this.getContentPane().setBackground(this.getBackground());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        pnlMain = new javax.swing.JPanel();
        pnlLogInContainer = new javax.swing.JPanel();
        pnlLogIn = new javax.swing.JPanel();
        lblLogInHeader = new javax.swing.JLabel();
        lblLogInBody = new javax.swing.JLabel();
        lblLogInUsername = new javax.swing.JLabel();
        iptLogInUsername = new javax.swing.JTextField();
        lblLogInPassword = new javax.swing.JLabel();
        iptLogInPassword = new javax.swing.JPasswordField();
        cbxRememberPassword = new javax.swing.JCheckBox();
        btnForgotPassword = new javax.swing.JButton();
        btnLogIn = new javax.swing.JButton();
        lblPromptSignUp = new javax.swing.JLabel();
        btnShowSignUp = new javax.swing.JButton();
        lblLogInError = new javax.swing.JLabel();
        pnlSignUpContainer = new javax.swing.JPanel();
        pnlSignUp = new javax.swing.JPanel();
        lblSignUpHeader = new javax.swing.JLabel();
        lblSignUpDescription = new javax.swing.JLabel();
        lblSignUpEmail = new javax.swing.JLabel();
        iptSignUpEmail = new javax.swing.JTextField();
        lblSignUpUsername = new javax.swing.JLabel();
        iptSignUpUsername = new javax.swing.JTextField();
        lblSignUpPassword = new javax.swing.JLabel();
        iptSignUpPassword = new javax.swing.JPasswordField();
        lblSignUpPassword2 = new javax.swing.JLabel();
        iptSignUpPassword2 = new javax.swing.JPasswordField();
        btnSignUp = new javax.swing.JButton();
        lblPromptLogIn = new javax.swing.JLabel();
        btnShowLogIn = new javax.swing.JButton();
        lblSignUpError = new javax.swing.JLabel();
        pnlForgotPasswordContainer = new javax.swing.JPanel();
        pnlForgotPassword = new javax.swing.JPanel();
        lblForgotPassword = new javax.swing.JLabel();
        lblForgotPasswordBody = new javax.swing.JLabel();
        lblForgotPasswordUsername = new javax.swing.JLabel();
        iptForgotPasswordUsername = new javax.swing.JTextField();
        lblForgotPasswordEmail = new javax.swing.JLabel();
        iptForgotPasswordEmail = new javax.swing.JTextField();
        btnForgotPasswordNext = new javax.swing.JButton();
        lblPromptLogIn2 = new javax.swing.JLabel();
        btnShowLogIn2 = new javax.swing.JButton();
        lblForgotPasswordError = new javax.swing.JLabel();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Event Scheduling System");
        setBackground(new java.awt.Color(255, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(51, 255, 0));
        setPreferredSize(new java.awt.Dimension(800, 500));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 500));

        pnlMain.setLayout(new java.awt.CardLayout());

        pnlLogInContainer.setBackground(new java.awt.Color(0, 102, 102));
        pnlLogInContainer.setPreferredSize(new java.awt.Dimension(800, 500));
        pnlLogInContainer.setLayout(new java.awt.GridBagLayout());

        pnlLogIn.setBackground(new java.awt.Color(255, 255, 255));
        pnlLogIn.setPreferredSize(new java.awt.Dimension(300, 450));

        lblLogInHeader.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblLogInHeader.setForeground(new java.awt.Color(51, 51, 51));
        lblLogInHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogInHeader.setText("Log In");

        lblLogInBody.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogInBody.setText("Welcome to ESS! Please sign in.");

        lblLogInUsername.setText("Username");

        iptLogInUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iptLogInUsernameActionPerformed(evt);
            }
        });

        lblLogInPassword.setText("Password");

        cbxRememberPassword.setText("Remember me");

        btnForgotPassword.setText("Forgot password?");
        btnForgotPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForgotPasswordActionPerformed(evt);
            }
        });

        btnLogIn.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        btnLogIn.setText("LOG IN");
        btnLogIn.setToolTipText("");
        btnLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogInActionPerformed(evt);
            }
        });

        lblPromptSignUp.setFont(new java.awt.Font("Cantarell", 2, 15)); // NOI18N
        lblPromptSignUp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPromptSignUp.setText("Don't have an account?");

        btnShowSignUp.setText("SIGN UP");
        btnShowSignUp.setBorder(btnLogIn.getBorder());
        btnShowSignUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowSignUpActionPerformed(evt);
            }
        });

        lblLogInError.setForeground(new java.awt.Color(255, 51, 51));
        lblLogInError.setToolTipText("");

        javax.swing.GroupLayout pnlLogInLayout = new javax.swing.GroupLayout(pnlLogIn);
        pnlLogIn.setLayout(pnlLogInLayout);
        pnlLogInLayout.setHorizontalGroup(
            pnlLogInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogInLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLogInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLogInHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLogInBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iptLogInUsername)
                    .addComponent(iptLogInPassword)
                    .addComponent(lblPromptSignUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnShowSignUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLogInError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlLogInLayout.createSequentialGroup()
                        .addGroup(pnlLogInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLogInUsername)
                            .addComponent(lblLogInPassword))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlLogInLayout.createSequentialGroup()
                        .addComponent(cbxRememberPassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnForgotPassword)))
                .addContainerGap())
        );
        pnlLogInLayout.setVerticalGroup(
            pnlLogInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogInLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblLogInHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLogInBody)
                .addGap(56, 56, 56)
                .addComponent(lblLogInUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptLogInUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLogInPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptLogInPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlLogInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxRememberPassword)
                    .addComponent(btnForgotPassword))
                .addGap(18, 18, 18)
                .addComponent(btnLogIn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLogInError)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(lblPromptSignUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnShowSignUp, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlLogInContainer.add(pnlLogIn, new java.awt.GridBagConstraints());

        pnlMain.add(pnlLogInContainer, "logIn");

        pnlSignUpContainer.setBackground(new java.awt.Color(0, 102, 102));
        pnlSignUpContainer.setLayout(new java.awt.GridBagLayout());

        pnlSignUp.setBackground(new java.awt.Color(255, 255, 255));
        pnlSignUp.setName(""); // NOI18N
        pnlSignUp.setPreferredSize(new java.awt.Dimension(300, 450));

        lblSignUpHeader.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblSignUpHeader.setForeground(new java.awt.Color(51, 51, 51));
        lblSignUpHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSignUpHeader.setText("Sign Up");

        lblSignUpDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSignUpDescription.setText("Welcome to ESS! Please create an account.");

        lblSignUpEmail.setText("Email address");

        lblSignUpUsername.setText("Username (used for login and display)");

        lblSignUpPassword.setText("Password");

        lblSignUpPassword2.setText("Repeat password");

        btnSignUp.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        btnSignUp.setText("SIGN UP");
        btnSignUp.setToolTipText("");
        btnSignUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignUpActionPerformed(evt);
            }
        });

        lblPromptLogIn.setFont(new java.awt.Font("Cantarell", 2, 15)); // NOI18N
        lblPromptLogIn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPromptLogIn.setText("Already have an account?");

        btnShowLogIn.setText("LOG IN");
        btnShowLogIn.setBorder(btnLogIn.getBorder());
        btnShowLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowLogInActionPerformed(evt);
            }
        });

        lblSignUpError.setForeground(new java.awt.Color(255, 51, 51));
        lblSignUpError.setToolTipText("");

        javax.swing.GroupLayout pnlSignUpLayout = new javax.swing.GroupLayout(pnlSignUp);
        pnlSignUp.setLayout(pnlSignUpLayout);
        pnlSignUpLayout.setHorizontalGroup(
            pnlSignUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSignUpLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSignUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSignUpPassword2)
                    .addComponent(lblSignUpPassword)
                    .addComponent(lblSignUpEmail)
                    .addComponent(lblSignUpUsername)
                    .addComponent(btnSignUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSignUpHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSignUpDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addComponent(iptSignUpPassword)
                    .addGroup(pnlSignUpLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(lblPromptLogIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnShowLogIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iptSignUpPassword2)
                    .addComponent(iptSignUpEmail)
                    .addComponent(iptSignUpUsername)
                    .addComponent(lblSignUpError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSignUpLayout.setVerticalGroup(
            pnlSignUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSignUpLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblSignUpHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSignUpDescription)
                .addGap(18, 18, 18)
                .addComponent(lblSignUpEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptSignUpEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSignUpPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptSignUpPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblSignUpPassword2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptSignUpPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSignUpUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptSignUpUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSignUp, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSignUpError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(lblPromptLogIn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnShowLogIn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlSignUpContainer.add(pnlSignUp, new java.awt.GridBagConstraints());

        pnlMain.add(pnlSignUpContainer, "signUp");

        pnlForgotPasswordContainer.setBackground(new java.awt.Color(0, 102, 102));
        pnlForgotPasswordContainer.setPreferredSize(new java.awt.Dimension(800, 500));
        pnlForgotPasswordContainer.setLayout(new java.awt.GridBagLayout());

        pnlForgotPassword.setBackground(new java.awt.Color(255, 255, 255));
        pnlForgotPassword.setPreferredSize(new java.awt.Dimension(300, 450));

        lblForgotPassword.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblForgotPassword.setForeground(new java.awt.Color(51, 51, 51));
        lblForgotPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblForgotPassword.setText("Password Recovery");

        lblForgotPasswordBody.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblForgotPasswordBody.setText("Please enter your account details to proceed.");

        lblForgotPasswordUsername.setText("Username");

        iptForgotPasswordUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iptForgotPasswordUsernameActionPerformed(evt);
            }
        });

        lblForgotPasswordEmail.setText("Email address");

        iptForgotPasswordEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iptForgotPasswordEmailActionPerformed(evt);
            }
        });

        btnForgotPasswordNext.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        btnForgotPasswordNext.setText("NEXT");
        btnForgotPasswordNext.setToolTipText("");
        btnForgotPasswordNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForgotPasswordNextActionPerformed(evt);
            }
        });

        lblPromptLogIn2.setFont(new java.awt.Font("Cantarell", 2, 15)); // NOI18N
        lblPromptLogIn2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPromptLogIn2.setText("Wrong page?");

        btnShowLogIn2.setText("GO BACK");
        btnShowLogIn2.setBorder(btnLogIn.getBorder());
        btnShowLogIn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowLogIn2ActionPerformed(evt);
            }
        });

        lblForgotPasswordError.setForeground(new java.awt.Color(255, 51, 51));
        lblForgotPasswordError.setToolTipText("");

        javax.swing.GroupLayout pnlForgotPasswordLayout = new javax.swing.GroupLayout(pnlForgotPassword);
        pnlForgotPassword.setLayout(pnlForgotPasswordLayout);
        pnlForgotPasswordLayout.setHorizontalGroup(
            pnlForgotPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlForgotPasswordLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlForgotPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iptForgotPasswordEmail)
                    .addComponent(btnForgotPasswordNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblForgotPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblForgotPasswordBody, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addComponent(iptForgotPasswordUsername)
                    .addComponent(lblPromptLogIn2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnShowLogIn2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblForgotPasswordError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlForgotPasswordLayout.createSequentialGroup()
                        .addGroup(pnlForgotPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblForgotPasswordUsername)
                            .addComponent(lblForgotPasswordEmail))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlForgotPasswordLayout.setVerticalGroup(
            pnlForgotPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlForgotPasswordLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblForgotPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblForgotPasswordBody)
                .addGap(56, 56, 56)
                .addComponent(lblForgotPasswordUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptForgotPasswordUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblForgotPasswordEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iptForgotPasswordEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblForgotPasswordError, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnForgotPasswordNext, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(lblPromptLogIn2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnShowLogIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlForgotPasswordContainer.add(pnlForgotPassword, new java.awt.GridBagConstraints());

        pnlMain.add(pnlForgotPasswordContainer, "forgotPassword");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private LoginResponse logIn(String endpoint, Map<String, String> body) throws LoginException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException("Request JSON parsing error");
        }
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .setHeader("Content-Type", "application/json")
            .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            throw new LoginException("Network error, try again later");
        }
        int code = response.statusCode();
        if (code != 200) {
            String errorMessage = "Invalid credentials";
            if (code != 403) {
                try {
                    errorMessage = objectMapper.readValue(response.body(), ErrorResponse.class).getMessage();
                } catch (JsonProcessingException ex) {
                    errorMessage = "Non-200 response with unparsable body";
                }
            }
            throw new LoginException(errorMessage);
        }
        try {
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (JsonProcessingException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException("Response JSON parsing error");
        }
    }
    
    
    private void showLayoutCard(JComponent parent, String cardName) {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.show(parent, cardName);
    }
    
    private void btnShowLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowLogInActionPerformed
        showLayoutCard(pnlMain, "logIn");
    }//GEN-LAST:event_btnShowLogInActionPerformed

    private void btnSignUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignUpActionPerformed
        String email = iptSignUpEmail.getText();
        if (!emailPattern.matcher(email).matches()) {
            lblSignUpError.setText("Invalid email address");
            return;
        }
        String password = new String(iptSignUpPassword.getPassword());
        if (password.length() < 8) {
            lblSignUpError.setText("Password must be at least 8 characters long");
            return;
        }
        if (!passwordPattern.matcher(password).matches()) {
            lblSignUpError.setText("Password must have special and capital chars");
            return;
        }
        if (!password.equals(new String(iptSignUpPassword2.getPassword()))) {
            lblSignUpError.setText("Passwords do not match");
            return;
        }
        String username = iptSignUpUsername.getText();
        if (username.length() == 0) {
            lblSignUpError.setText("Username cannot be empty");
        }
        lblSignUpError.setText("");
        btnSignUp.setEnabled(false);
        Map<String, String> body = new HashMap();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        LoginResponse response;
        try {
            response = logIn("/auth/register", body);
        } catch (LoginException ex) {
            lblSignUpError.setText(ex.getMessage());
            return;
        } finally {
            btnSignUp.setEnabled(true);
        }
        accessToken = response.getToken();
        System.out.println("Successfully created user #" + response.getUserId().toString());
    }//GEN-LAST:event_btnSignUpActionPerformed

    private void btnShowSignUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowSignUpActionPerformed
        showLayoutCard(pnlMain, "signUp");
    }//GEN-LAST:event_btnShowSignUpActionPerformed

    private void btnLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogInActionPerformed
        lblLogInError.setText("");
        btnLogIn.setEnabled(false);
        Map<String, String> body = new HashMap();
        body.put("username", iptLogInUsername.getText());
        body.put("password", new String(iptLogInPassword.getPassword()));
        LoginResponse response;
        try {
            response = logIn("/auth/login", body);
        } catch (LoginException ex) {
            lblLogInError.setText(ex.getMessage());
            return;
        } finally {
            btnLogIn.setEnabled(true);
        }
        accessToken = response.getToken();
        System.out.println("Logged in as user #" + response.getUserId().toString());
    }//GEN-LAST:event_btnLogInActionPerformed

    private void iptLogInUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iptLogInUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iptLogInUsernameActionPerformed

    private void iptForgotPasswordUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iptForgotPasswordUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iptForgotPasswordUsernameActionPerformed

    private void btnForgotPasswordNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForgotPasswordNextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnForgotPasswordNextActionPerformed

    private void btnShowLogIn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowLogIn2ActionPerformed
        showLayoutCard(pnlMain, "logIn");
    }//GEN-LAST:event_btnShowLogIn2ActionPerformed

    private void btnForgotPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForgotPasswordActionPerformed
        showLayoutCard(pnlMain, "forgotPassword");
    }//GEN-LAST:event_btnForgotPasswordActionPerformed

    private void iptForgotPasswordEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iptForgotPasswordEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iptForgotPasswordEmailActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                System.out.println(info);
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnForgotPassword;
    private javax.swing.JButton btnForgotPasswordNext;
    private javax.swing.JButton btnLogIn;
    private javax.swing.JButton btnShowLogIn;
    private javax.swing.JButton btnShowLogIn2;
    private javax.swing.JButton btnShowSignUp;
    private javax.swing.JButton btnSignUp;
    private javax.swing.JCheckBox cbxRememberPassword;
    private javax.swing.JTextField iptForgotPasswordEmail;
    private javax.swing.JTextField iptForgotPasswordUsername;
    private javax.swing.JPasswordField iptLogInPassword;
    private javax.swing.JTextField iptLogInUsername;
    private javax.swing.JTextField iptSignUpEmail;
    private javax.swing.JPasswordField iptSignUpPassword;
    private javax.swing.JPasswordField iptSignUpPassword2;
    private javax.swing.JTextField iptSignUpUsername;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel lblForgotPassword;
    private javax.swing.JLabel lblForgotPasswordBody;
    private javax.swing.JLabel lblForgotPasswordEmail;
    private javax.swing.JLabel lblForgotPasswordError;
    private javax.swing.JLabel lblForgotPasswordUsername;
    private javax.swing.JLabel lblLogInBody;
    private javax.swing.JLabel lblLogInError;
    private javax.swing.JLabel lblLogInHeader;
    private javax.swing.JLabel lblLogInPassword;
    private javax.swing.JLabel lblLogInUsername;
    private javax.swing.JLabel lblPromptLogIn;
    private javax.swing.JLabel lblPromptLogIn2;
    private javax.swing.JLabel lblPromptSignUp;
    private javax.swing.JLabel lblSignUpDescription;
    private javax.swing.JLabel lblSignUpEmail;
    private javax.swing.JLabel lblSignUpError;
    private javax.swing.JLabel lblSignUpHeader;
    private javax.swing.JLabel lblSignUpPassword;
    private javax.swing.JLabel lblSignUpPassword2;
    private javax.swing.JLabel lblSignUpUsername;
    private javax.swing.JPanel pnlForgotPassword;
    private javax.swing.JPanel pnlForgotPasswordContainer;
    private javax.swing.JPanel pnlLogIn;
    private javax.swing.JPanel pnlLogInContainer;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSignUp;
    private javax.swing.JPanel pnlSignUpContainer;
    // End of variables declaration//GEN-END:variables
}
