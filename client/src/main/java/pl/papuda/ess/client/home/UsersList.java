package pl.papuda.ess.client.home;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import pl.papuda.ess.client.Web;
import pl.papuda.ess.client.model.User;

/**
 *
 * @author konrad
 */
public class UsersList extends javax.swing.JPanel {

    private List<User> users = null;
    
    /**
     * Creates new form UsersList
     */
    public UsersList() {
        initComponents();
        populateUsers();
    }
    
    private void populateUsers() {
        getUsers();
        if (users == null) {
            lblUsersLoading.setText("An error occurred while obtaining the users.");
            return;
        }
        if (users.size() == 0) {
            lblUsersLoading.setText("There are no users to display!");
            return;
        }
        pnlUsersList.removeAll();
        for (User user : users) {
            UserListItem userListItem = new UserListItem(user);
            pnlUsersList.add(userListItem);
        }
    }
    
    private void getUsers() {
        HttpResponse<String> response;
        try {
            response = Web.sendGetRequest("/admin/user");
        } catch (IOException | InterruptedException ex) {
            System.err.println("Could not fetch the users.");
            return;
        }
        int status = response.statusCode();
        if (status != 200) {
            System.err.println("Non-200 status code fetching users: " + status);
            return;
        }
        users = Web.readResponseBody(response, new TypeReference<List<User>>() {});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlUsersContainer = new javax.swing.JPanel();
        scpUsersList = new javax.swing.JScrollPane();
        pnlUsersList = new javax.swing.JPanel();
        pnlUsersLoading = new javax.swing.JPanel();
        lblUsersLoading = new javax.swing.JLabel();

        pnlUsersContainer.setLayout(new java.awt.CardLayout());

        scpUsersList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlUsersList.setBackground(new java.awt.Color(204, 204, 204));
        pnlUsersList.setLayout(new javax.swing.BoxLayout(pnlUsersList, javax.swing.BoxLayout.Y_AXIS));

        pnlUsersLoading.setBackground(pnlUsersList.getBackground());

        lblUsersLoading.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblUsersLoading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsersLoading.setText("Loading...");

        javax.swing.GroupLayout pnlUsersLoadingLayout = new javax.swing.GroupLayout(pnlUsersLoading);
        pnlUsersLoading.setLayout(pnlUsersLoadingLayout);
        pnlUsersLoadingLayout.setHorizontalGroup(
            pnlUsersLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsersLoadingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUsersLoading, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlUsersLoadingLayout.setVerticalGroup(
            pnlUsersLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsersLoadingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUsersLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(198, Short.MAX_VALUE))
        );

        pnlUsersList.add(pnlUsersLoading);

        scpUsersList.setViewportView(pnlUsersList);

        pnlUsersContainer.add(scpUsersList, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlUsersContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlUsersContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblUsersLoading;
    private javax.swing.JPanel pnlUsersContainer;
    private javax.swing.JPanel pnlUsersList;
    private javax.swing.JPanel pnlUsersLoading;
    private javax.swing.JScrollPane scpUsersList;
    // End of variables declaration//GEN-END:variables
}
