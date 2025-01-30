
package pl.papuda.ess.client;

import javax.swing.JPanel;


public class AppPanel extends JPanel {
    protected MainWindow getMainWindow() {
        return (MainWindow) getTopLevelAncestor();
    }
    
    protected void switchPage(String page) {
        MainWindow mainWindow = getMainWindow();
        mainWindow.showLayoutCard(page);
    }
}
