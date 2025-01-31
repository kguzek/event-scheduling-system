package pl.papuda.ess.client.components;

import javax.swing.JPanel;

import pl.papuda.ess.client.MainWindow;

public class AppPanel extends JPanel {

    protected MainWindow getMainWindow() {
        return (MainWindow) getTopLevelAncestor();
    }

    protected void switchPage(String page) {
        MainWindow mainWindow = getMainWindow();
        mainWindow.showLayoutCard(page);
    }
}
