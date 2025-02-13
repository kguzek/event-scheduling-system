package pl.papuda.ess.client;

import pl.papuda.ess.client.tools.Web;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.CardLayout;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Map;

import pl.papuda.ess.client.model.error.LoginException;
import pl.papuda.ess.client.model.body.ErrorResponse;
import pl.papuda.ess.client.model.body.LoginResponse;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.services.EventService;
import pl.papuda.ess.client.tools.AppPreferences;

public class MainWindow extends javax.swing.JFrame {

    public EventService eventService;
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        eventService = new EventService(homePage1);
        this.getContentPane().setBackground(this.getBackground());
        loadSettings();
    }

    public void showBudgetFor(Event event) {
        budgetPage1.setEvent(event);
        showLayoutCard("budget");
    }

    public void afterLogin(String token, boolean rememberPassword) {
        Web.setAccessToken(token, rememberPassword);
        new Thread(homePage1::getUserPermissions).start();
    }

    public void afterLoginVerified(String token, boolean rememberPassword) {
        afterLogin(token, rememberPassword);
        showLayoutCard("home");
        eventService.startFetchEvents();
    }

    public void verifyEmail(String email) {
        showLayoutCard("verifyEmail");
        verifyEmailPage1.startEmailVerification(email);
    }

    private void loadSettings() {
        String token = AppPreferences.read("accessToken", "");
        String tokenGenerationDate = AppPreferences.read("tokenGenerationDate", "");
        if ("".equals(token) || "".equals(tokenGenerationDate)) {
            return;
        }
        Long generationDate;
        try {
            generationDate = Long.valueOf(tokenGenerationDate);
        } catch (NumberFormatException ex) {
            System.out.println("Removing malformatted token generation date " + tokenGenerationDate);
            AppPreferences.unset("tokenGenerationDate");
            return;
        }
        if (generationDate <= 0) {
            return;
        }
        Date now = new Date();
        long diff = now.getTime() - generationDate;
        if (diff < 12 * 3600 * 1000) {
            // Token generated less than 12 hours ago
            afterLoginVerified(token, false);
        }
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
        logInPage1 = new pl.papuda.ess.client.pages.LogInPage();
        signUpPage1 = new pl.papuda.ess.client.pages.SignUpPage();
        forgotPasswordPage1 = new pl.papuda.ess.client.pages.ForgotPasswordPage();
        verifyEmailPage1 = new pl.papuda.ess.client.pages.VerifyEmailPage();
        budgetPage1 = new pl.papuda.ess.client.pages.BudgetPage();
        pnlHomeContainer = new javax.swing.JPanel();
        homePage1 = new pl.papuda.ess.client.pages.HomePage();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Event Scheduling System");
        setBackground(new java.awt.Color(255, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(51, 255, 0));
        setMinimumSize(new java.awt.Dimension(300, 500));
        setSize(new java.awt.Dimension(900, 600));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pnlMain.setBackground(new java.awt.Color(0, 102, 102));
        pnlMain.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlMain.setPreferredSize(new java.awt.Dimension(800, 500));
        pnlMain.setLayout(new java.awt.CardLayout());
        pnlMain.add(logInPage1, "logIn");
        pnlMain.add(signUpPage1, "signUp");
        pnlMain.add(forgotPasswordPage1, "forgotPassword");
        pnlMain.add(verifyEmailPage1, "verifyEmail");
        pnlMain.add(budgetPage1, "budget");

        pnlHomeContainer.setLayout(new javax.swing.BoxLayout(pnlHomeContainer, javax.swing.BoxLayout.LINE_AXIS));
        pnlHomeContainer.add(homePage1);

        pnlMain.add(pnlHomeContainer, "home");

        getContentPane().add(pnlMain);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public LoginResponse logIn(String endpoint, Map<String, String> body) throws LoginException {
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
            throw new LoginException("Network error, please try again later");
        }
        int code = response.statusCode();
        if (code != 200) {
            if (code == 403) {
                throw new LoginException("Invalid credentials");
            }
            ErrorResponse errorResponse;
            try {
                errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
            } catch (JsonProcessingException ex) {
                System.err.println("JSON parse exception at login: " + ex);
                throw new LoginException(code + " response with unparsable body");
            }
            if (errorResponse.getError() != null) {
                System.err.println("Error response info: " + errorResponse.getError());
            }
            throw new LoginException(errorResponse.getMessage());
        }
        try {
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (JsonProcessingException ex) {
            // Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException("Response JSON parsing error");
        }
    }

    public void showLayoutCard(String cardName) {
        CardLayout layout = (CardLayout) pnlMain.getLayout();
        layout.show(pnlMain, cardName);
        System.out.println("Navigating to " + cardName + " page");
        logInPage1.setError("");
        signUpPage1.setError("");
        setMinimumSize("home".equals(cardName) ? new java.awt.Dimension(1000, 750) : new java.awt.Dimension(300, 500));
    }

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
    private pl.papuda.ess.client.pages.BudgetPage budgetPage1;
    private pl.papuda.ess.client.pages.ForgotPasswordPage forgotPasswordPage1;
    private pl.papuda.ess.client.pages.HomePage homePage1;
    private pl.papuda.ess.client.pages.LogInPage logInPage1;
    private javax.swing.JPanel pnlHomeContainer;
    private javax.swing.JPanel pnlMain;
    private pl.papuda.ess.client.pages.SignUpPage signUpPage1;
    private pl.papuda.ess.client.pages.VerifyEmailPage verifyEmailPage1;
    // End of variables declaration//GEN-END:variables
}
