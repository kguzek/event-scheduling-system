package pl.papuda.ess.client.home.calendar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;

public class CalendarCell extends JButton {
    private Date date;
    private boolean isTitleCell = false;
    private boolean isToday = false;
    private Long eventId = null;
    
    public CalendarCell() {
        setContentAreaFilled(false);
        setBorder(null);
        setHorizontalAlignment(JLabel.CENTER);
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void updateColor(boolean isCurrentMonth) {
        setForeground(isCurrentMonth ? new Color(68, 68, 68) : new Color(169, 169, 169));
    }
    
    public void markAsTitleCell() {
        isTitleCell = true;
    }
    
    public void markAsToday() {
        isToday = true;
        setForeground(Color.WHITE);
        requestFocusInWindow();
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        if (isTitleCell) {
            graphics.setColor(new Color(213, 213, 213));
            graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        } else {
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isToday) {
                g2d.setColor(new Color(50, 122, 122));
                int width = 30;
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - width) / 2;
                g2d.fillRoundRect(x, y, width, width, 100, 100);
            }
            if (eventId != null) {
                g2d.setColor(new Color(211, 88, 88));
                int width = 12;
                int x = (getWidth() - width) / 6;
                int y = (getHeight() - width) / 6;
                g2d.fillRoundRect(x, y, width, width, 100, 100);
            }
        }
        super.paintComponent(graphics);
    }
}
