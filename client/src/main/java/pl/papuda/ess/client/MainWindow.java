/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pl.papuda.ess.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.CardLayout;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import javax.swing.Timer;

import pl.papuda.ess.client.error.LoginException;
import pl.papuda.ess.client.model.body.ErrorResponse;
import pl.papuda.ess.client.model.body.LoginResponse;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.model.User;

public class MainWindow extends javax.swing.JFrame {

    private final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[#?!@$ %^&*-]).{8,}$");

    private final Pattern emailPattern = Pattern.compile("^[^@]+@[^@]+$");

    private String userEmail = null;

    private Timer timer;
    private Timer resendButtonEnableTimer;
    private boolean awaitingDeletion = false;

    private static List<Event> events = null;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        // Custom code
        this.getContentPane().setBackground(this.getBackground());
        loadSettings();
        createSubscriptions();
    }
    
    private void createSubscriptions() {
        Web.subscribeStompResource("/topic/events/deleted", this::handleEventDeleted);
        Web.subscribeStompResource("/topic/events/updated", this::handleEventUpdated);
        Web.subscribeStompResource("/topic/events/created", this::handleEventCreated);
    }
    
    public void attemptRemoveEvent() {
        awaitingDeletion = true;
    }

    public void removeEvent(Long eventId) {
        events.removeIf(event -> event.getId().equals(eventId));
        updateEvents();
        awaitingDeletion = false;
    }

    private void updateEvent(Event event, boolean created) {
        if (created) {
            events.add(event);
        } else {
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                if (e.getId().equals(event.getId())) {
                    events.set(i, event);
                    break;
                }
            }
        }
        updateEvents();
    }

    private void updateEvents() {
        // repaint to reflect event data
        calendarCustom1.updateCalendar(events);
        eventsList1.updateEventsList(events);
    }

    public void showBudgetFor(Event event) {
        budgetTracker1.setEvent(event);
        showLayoutCard("budget");
    }

    private void getEvents() {
        HttpResponse<String> response;
        try {
            response = Web.sendGetRequest("/private/event");
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
            return;
        }
        if (response.statusCode() != 200) {
            System.out.println("Response code " + response.statusCode());
            return;
        }
        events = Web.readResponseBody(response, new TypeReference<List<Event>>() {
        });
        updateEvents();
    }

    private void getUserPermissions() {
        HttpResponse<String> response;
        try {
            response = Web.sendGetRequest("/private/permissions");
        } catch (IOException | InterruptedException ex) {
            Web.unsetAccessToken();
            showLayoutCard("logIn");
            return;
        }
        Web.user = Web.readResponseBody(response, new TypeReference<User>() {
        });
        System.out.println("User permissions: " + Web.user);
    }

    private void afterRegister(String token) {
        Web.setAccessToken(token, cbxRememberPassword.isSelected());
        new Thread(this::getUserPermissions).start();
    }

    private void afterLogin(String token) {
        afterRegister(token);
        showLayoutCard("home");
        new Thread(this::getEvents).start();
    }

    private void loadSettings() {
        String token = Web.prefs.get("accessToken", "");
        String tokenGenerationDate = Web.prefs.get("tokenGenerationDate", "");
        if ("".equals(token) || "".equals(tokenGenerationDate)) {
            return;
        }
        Long generationDate;
        try {
            generationDate = Long.valueOf(tokenGenerationDate);
        } catch (NumberFormatException ex) {
            System.out.println("Removing malformatted token generation date " + tokenGenerationDate);
            Web.prefs.remove("tokenGenerationDate");
            return;
        }
        if (generationDate <= 0) {
            return;
        }
        Date now = new Date();
        long diff = now.getTime() - generationDate;
        if (diff < 12 * 3600 * 1000) {
            // Token generated less than 12 hours ago
            afterLogin(token);
        }
    }
    
    private void handleEventCreated(Object body) {
        messageHandler(body, true);
    }
    
    private void handleEventUpdated(Object body) {
        messageHandler(body, false);
    }

    private void messageHandler(Object body, boolean created) {
        if (body instanceof String errorMessage) {
            eventsList1.setErrorText(errorMessage);
            System.err.println("Non-OK event message response " + errorMessage);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        Event createdEvent = mapper.convertValue(body, Event.class);
        updateEvent(createdEvent, created);
        eventsList1.onEventUpdated();
    }
        
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Problem deleting event", JOptionPane.ERROR_MESSAGE);
    }
        
    private void handleEventDeleted(Object body) {
        if (body instanceof String errorMessage) {
            if (awaitingDeletion) showErrorDialog(errorMessage);
            System.err.println("Non-OK delete message response " + errorMessage);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        Long eventId = mapper.convertValue(body, Long.class);
        removeEvent(eventId);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        pnlVerifyEmailContainer = new javax.swing.JPanel();
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
        pnlHome = new javax.swing.JPanel();
        calendarCustom1 = new pl.papuda.ess.client.home.calendar.CalendarCustom();
        eventsList1 = new pl.papuda.ess.client.home.EventsList();
        btnLogOut = new javax.swing.JButton();
        pnlBudgetContainer = new javax.swing.JPanel();
        budgetTracker1 = new pl.papuda.ess.client.budgetTracker.BudgetTracker();
        btnBudgetBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Event Scheduling System");
        setBackground(new java.awt.Color(255, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(51, 255, 0));
        setMinimumSize(new java.awt.Dimension(1100, 750));
        setPreferredSize(new java.awt.Dimension(900, 600));
        setSize(new java.awt.Dimension(900, 600));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pnlMain.setPreferredSize(new java.awt.Dimension(988, 400));
        pnlMain.setLayout(new java.awt.CardLayout());

        pnlLogInContainer.setBackground(new java.awt.Color(0, 102, 102));
        pnlLogInContainer.setPreferredSize(new java.awt.Dimension(800, 500));
        pnlLogInContainer.setLayout(new java.awt.GridBagLayout());

        pnlLogIn.setBackground(new java.awt.Color(255, 255, 255));
        pnlLogIn.setMinimumSize(new java.awt.Dimension(400, 500));
        pnlLogIn.setPreferredSize(new java.awt.Dimension(400, 500));

        lblLogInHeader.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblLogInHeader.setForeground(new java.awt.Color(51, 51, 51));
        lblLogInHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogInHeader.setText("Log In");

        lblLogInBody.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogInBody.setText("Welcome to ESS! Please sign in.");

        lblLogInUsername.setText("Username");

        lblLogInPassword.setText("Password");

        cbxRememberPassword.setSelected(true);
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
        pnlSignUp.setMinimumSize(new java.awt.Dimension(400, 500));
        pnlSignUp.setName(""); // NOI18N
        pnlSignUp.setPreferredSize(new java.awt.Dimension(400, 500));

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
                    .addComponent(lblSignUpDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
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
        pnlForgotPassword.setMinimumSize(new java.awt.Dimension(400, 500));
        pnlForgotPassword.setName(""); // NOI18N
        pnlForgotPassword.setPreferredSize(new java.awt.Dimension(400, 500));

        lblForgotPassword.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        lblForgotPassword.setForeground(new java.awt.Color(51, 51, 51));
        lblForgotPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblForgotPassword.setText("Password Recovery");

        lblForgotPasswordBody.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblForgotPasswordBody.setText("Please enter your account details to proceed.");

        lblForgotPasswordUsername.setText("Username");

        lblForgotPasswordEmail.setText("Email address");

        btnForgotPasswordNext.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        btnForgotPasswordNext.setText("NEXT");
        btnForgotPasswordNext.setToolTipText("");

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
                    .addComponent(lblForgotPasswordBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addComponent(lblPromptLogIn2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnShowLogIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlForgotPasswordContainer.add(pnlForgotPassword, new java.awt.GridBagConstraints());

        pnlMain.add(pnlForgotPasswordContainer, "forgotPassword");

        pnlVerifyEmailContainer.setBackground(new java.awt.Color(0, 102, 102));
        pnlVerifyEmailContainer.setLayout(new java.awt.GridBagLayout());

        pnlVerifyEmail.setBackground(new java.awt.Color(255, 255, 255));
        pnlVerifyEmail.setMinimumSize(new java.awt.Dimension(400, 500));
        pnlVerifyEmail.setName(""); // NOI18N
        pnlVerifyEmail.setPreferredSize(new java.awt.Dimension(400, 500));

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
        btnVerifyEmailGoBack.setBorder(btnLogIn.getBorder());
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPromptLogIn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVerifyEmailGoBack, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlVerifyEmailContainer.add(pnlVerifyEmail, new java.awt.GridBagConstraints());

        pnlMain.add(pnlVerifyEmailContainer, "verifyEmail");

        pnlHome.setBackground(new java.awt.Color(0, 102, 102));

        btnLogOut.setText("Log out");
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHomeLayout = new javax.swing.GroupLayout(pnlHome);
        pnlHome.setLayout(pnlHomeLayout);
        pnlHomeLayout.setHorizontalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomeLayout.createSequentialGroup()
                .addComponent(calendarCustom1, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventsList1, javax.swing.GroupLayout.PREFERRED_SIZE, 459, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHomeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLogOut)
                        .addContainerGap())))
        );
        pnlHomeLayout.setVerticalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHomeLayout.createSequentialGroup()
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(calendarCustom1, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLogOut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventsList1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );

        pnlMain.add(pnlHome, "home");

        pnlBudgetContainer.setLayout(new java.awt.BorderLayout());
        pnlBudgetContainer.add(budgetTracker1, java.awt.BorderLayout.CENTER);

        btnBudgetBack.setText("CLOSE BUDGET TRACKER");
        btnBudgetBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBudgetBackActionPerformed(evt);
            }
        });
        pnlBudgetContainer.add(btnBudgetBack, java.awt.BorderLayout.PAGE_START);

        pnlMain.add(pnlBudgetContainer, "budget");

        getContentPane().add(pnlMain);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBudgetBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBudgetBackActionPerformed
        showLayoutCard("home");
    }// GEN-LAST:event_btnBudgetBackActionPerformed

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLogOutActionPerformed
        showLayoutCard("logIn");
        Web.unsetAccessToken();
        Web.prefs.remove("accessToken");
        Web.prefs.remove("tokenGenerationDate");
    }// GEN-LAST:event_btnLogOutActionPerformed

    private void btnVerifyEmailGoBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnVerifyEmailGoBackActionPerformed
        showLayoutCard("logIn");
        timer.stop();
    }// GEN-LAST:event_btnVerifyEmailGoBackActionPerformed

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
        showLayoutCard("home");
    }

    private void enableResendButton() {
        resendButtonEnableTimer.stop();
        btnVerifyEmailResend.setEnabled(true);
        btnVerifyEmailResend.setText("RESEND");
    }

    public class EmailChallenge extends Thread {

        public void run() {
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

        new EmailChallenge().start();
    }

    private void verifyEmail() {
        showLayoutCard("verifyEmail");
        btnVerifyEmailResend.setEnabled(false);
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
    
    private void btnVerifyEmailResendActionPerformed(java.awt.event.ActionEvent evt) {
        // GEN-FIRST:event_btnVerifyEmailResendActionPerformed
        sendVerificationEmail();
    }
    // GEN-LAST:event_btnVerifyEmailResendActionPerformed

    private LoginResponse logIn(String endpoint, Map<String, String> body) throws LoginException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
            // Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException("Request JSON parsing error");
        }

        HttpResponse<String> response;
        try {
            response = Web.sendPostRequest(endpoint, json);
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
                    Web.logException(ex);
                    errorMessage = code + " response with unparsable body";
                }
            }
            throw new LoginException(errorMessage);
        }
        try {
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (JsonProcessingException ex) {
            // Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException("Response JSON parsing error");
        }
    }

    private void showLayoutCard(String cardName) {
        CardLayout layout = (CardLayout) pnlMain.getLayout();
        layout.show(pnlMain, cardName);
        lblLogInError.setText("");
        lblSignUpError.setText("");
    }

    private void btnShowLogInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnShowLogInActionPerformed
        showLayoutCard("logIn");
    }// GEN-LAST:event_btnShowLogInActionPerformed

    private void btnSignUpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSignUpActionPerformed
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
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        new Thread(() -> {
            LoginResponse response;
            try {
                response = logIn("/auth/register", body);
            } catch (LoginException ex) {
                String message = ex.getMessage();
                lblSignUpError.setText(message);
                return;
            } finally {
                btnSignUp.setEnabled(true);
            }
            afterRegister(response.getToken());
            System.out.println("Successfully created user #" + response.getUserId().toString());
            userEmail = email;
            verifyEmail();
            lblVerifyEmailDescription2.setText("to your address at '" + email + "'.");
        }).start();
    }// GEN-LAST:event_btnSignUpActionPerformed

    private void btnShowSignUpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnShowSignUpActionPerformed
        showLayoutCard("signUp");
    }// GEN-LAST:event_btnShowSignUpActionPerformed

    private void btnLogInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLogInActionPerformed
        lblLogInError.setText("");
        btnLogIn.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("username", iptLogInUsername.getText());
        body.put("password", new String(iptLogInPassword.getPassword()));
        new Thread(() -> {
            LoginResponse response;
            try {
                response = logIn("/auth/login", body);
            } catch (LoginException ex) {
                String message = ex.getMessage();
                if (message.contains("email address is not verified")) {
                    String email = message.split("'", 3)[1];
                    userEmail = email;
                    verifyEmail();
                    lblVerifyEmailDescription2.setText("to your address at " + email + ".");
                } else {
                    lblLogInError.setText(message);
                }
                return;
            } finally {
                btnLogIn.setEnabled(true);
            }
            System.out.println("Logged in as user #" + response.getUserId().toString());
            afterLogin(response.getToken());
        }).start();
    }// GEN-LAST:event_btnLogInActionPerformed

    private void btnShowLogIn2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnShowLogIn2ActionPerformed
        showLayoutCard("logIn");
    }// GEN-LAST:event_btnShowLogIn2ActionPerformed

    private void btnForgotPasswordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnForgotPasswordActionPerformed
        showLayoutCard("forgotPassword");
    }// GEN-LAST:event_btnForgotPasswordActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Metal/Windows look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        String className = null;
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                String name = info.getName();
                if ("Metal".equals(name) && className == null) {
                    className = info.getClassName();
                }
                if ("Windows".equals(name)) {
                    className = info.getClassName();
                    break;
                }
            }
            if (className != null) {
                javax.swing.UIManager.setLookAndFeel(className);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            // error handling
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBudgetBack;
    private javax.swing.JButton btnForgotPassword;
    private javax.swing.JButton btnForgotPasswordNext;
    private javax.swing.JButton btnLogIn;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnShowLogIn;
    private javax.swing.JButton btnShowLogIn2;
    private javax.swing.JButton btnShowSignUp;
    private javax.swing.JButton btnSignUp;
    private javax.swing.JButton btnVerifyEmailGoBack;
    private javax.swing.JButton btnVerifyEmailResend;
    private pl.papuda.ess.client.budgetTracker.BudgetTracker budgetTracker1;
    private pl.papuda.ess.client.home.calendar.CalendarCustom calendarCustom1;
    private javax.swing.JCheckBox cbxRememberPassword;
    private pl.papuda.ess.client.home.EventsList eventsList1;
    private javax.swing.JTextField iptForgotPasswordEmail;
    private javax.swing.JTextField iptForgotPasswordUsername;
    private javax.swing.JPasswordField iptLogInPassword;
    private javax.swing.JTextField iptLogInUsername;
    private javax.swing.JTextField iptSignUpEmail;
    private javax.swing.JPasswordField iptSignUpPassword;
    private javax.swing.JPasswordField iptSignUpPassword2;
    private javax.swing.JTextField iptSignUpUsername;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JLabel lblPromptLogIn1;
    private javax.swing.JLabel lblPromptLogIn2;
    private javax.swing.JLabel lblPromptSignUp;
    private javax.swing.JLabel lblSignUpDescription;
    private javax.swing.JLabel lblSignUpEmail;
    private javax.swing.JLabel lblSignUpError;
    private javax.swing.JLabel lblSignUpHeader;
    private javax.swing.JLabel lblSignUpPassword;
    private javax.swing.JLabel lblSignUpPassword2;
    private javax.swing.JLabel lblSignUpUsername;
    private javax.swing.JLabel lblVerifyEmailDescription;
    private javax.swing.JLabel lblVerifyEmailDescription2;
    private javax.swing.JTextArea lblVerifyEmailError;
    private javax.swing.JLabel lblVerifyEmailHeader;
    private javax.swing.JLabel lblVerifyEmailResend;
    private javax.swing.JPanel pnlBudgetContainer;
    private javax.swing.JPanel pnlForgotPassword;
    private javax.swing.JPanel pnlForgotPasswordContainer;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlLogIn;
    private javax.swing.JPanel pnlLogInContainer;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSignUp;
    private javax.swing.JPanel pnlSignUpContainer;
    private javax.swing.JPanel pnlVerifyEmail;
    private javax.swing.JPanel pnlVerifyEmailContainer;
    // End of variables declaration//GEN-END:variables
}
