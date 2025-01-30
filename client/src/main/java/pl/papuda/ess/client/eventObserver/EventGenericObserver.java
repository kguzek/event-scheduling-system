package pl.papuda.ess.client.eventObserver;

import javax.swing.JOptionPane;
import pl.papuda.ess.client.interfaces.Observer;

public abstract class EventGenericObserver implements Observer {

    private void showErrorMessage(String errorMessage) {
        System.err.println("Observer error message: " + errorMessage);
        // eventsList1.setErrorText(errorMessage);
        JOptionPane.showMessageDialog(null, errorMessage, "Problem performing action", JOptionPane.ERROR_MESSAGE);
    }

    abstract void handleMessage(Object messageBody);

    @Override
    public void update(Object messageBody) {
        if (messageBody instanceof String errorMessage) {
            showErrorMessage(errorMessage);
            return;
        }
        handleMessage(messageBody);
    }
}
