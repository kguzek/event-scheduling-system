package pl.papuda.ess.client.pages;

import pl.papuda.ess.client.components.AppPanel;
import pl.papuda.ess.client.model.Event;

/**
 *
 * @author konrad
 */
public class BudgetPage extends AppPanel {

    /**
     * Creates new form BudgetPage
     */
    public BudgetPage() {
        initComponents();
    }

    public void setEvent(Event event) {
        budgetTracker1.setEvent(event);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBudgetContainer = new javax.swing.JPanel();
        btnBudgetBack = new javax.swing.JButton();
        budgetTracker1 = new pl.papuda.ess.client.components.budgetTracker.BudgetTracker();

        pnlBudgetContainer.setLayout(new java.awt.BorderLayout());

        btnBudgetBack.setText("CLOSE BUDGET TRACKER");
        btnBudgetBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBudgetBackActionPerformed(evt);
            }
        });
        pnlBudgetContainer.add(btnBudgetBack, java.awt.BorderLayout.PAGE_START);
        pnlBudgetContainer.add(budgetTracker1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1000, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(pnlBudgetContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 579, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(pnlBudgetContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBudgetBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBudgetBackActionPerformed
        switchPage("home");
    }//GEN-LAST:event_btnBudgetBackActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBudgetBack;
    private pl.papuda.ess.client.components.budgetTracker.BudgetTracker budgetTracker1;
    private javax.swing.JPanel pnlBudgetContainer;
    // End of variables declaration//GEN-END:variables
}
