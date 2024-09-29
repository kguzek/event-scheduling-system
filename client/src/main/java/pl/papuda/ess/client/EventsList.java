package pl.papuda.ess.client;

import pl.papuda.ess.client.model.Event;

public class EventsList extends javax.swing.JPanel {

    /**
     * Creates new form EventsList
     */
    public EventsList() {
        initComponents();
    }
    
    public void update(Event[] events) {
        pnlEventsContainer.removeAll();
        for (Event event : events) {
            System.out.println("Adding event " + event.getTitle());
            pnlEventsContainer.add(new CalendarEvent(event));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scpEventsContainer = new javax.swing.JScrollPane();
        pnlEventsContainer = new javax.swing.JPanel();

        scpEventsContainer.setBackground(new java.awt.Color(204, 204, 204));
        scpEventsContainer.setViewportView(null);

        pnlEventsContainer.setBackground(new java.awt.Color(204, 204, 204));
        pnlEventsContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        scpEventsContainer.setViewportView(pnlEventsContainer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scpEventsContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addComponent(scpEventsContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlEventsContainer;
    private javax.swing.JScrollPane scpEventsContainer;
    // End of variables declaration//GEN-END:variables
}
