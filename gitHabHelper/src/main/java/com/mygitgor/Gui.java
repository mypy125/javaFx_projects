package com.mygitgor;

import java.awt.*;

public class Gui {

    private final TrayIcon trayIcon;

    public Gui() {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/logo.png"));

        trayIcon = new TrayIcon(image, "Github Helper");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Github Helper");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void showNotification(String title, String text){
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
