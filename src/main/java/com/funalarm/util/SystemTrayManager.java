package com.funalarm.util;

import com.funalarm.config.DatabaseConfig;
import com.funalarm.ui.AppContext;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SystemTrayManager {
    private TrayIcon trayIcon;

    public void install(AppContext context, Stage stage) {
        if (!SystemTray.isSupported()) {
            return;
        }
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = createTrayImage();
            PopupMenu menu = new PopupMenu();

            MenuItem showItem = new MenuItem("显示窗口");
            showItem.addActionListener(e -> Platform.runLater(() -> {
                stage.show();
                stage.toFront();
            }));

            MenuItem exitItem = new MenuItem("退出");
            exitItem.addActionListener(e -> Platform.runLater(() -> {
                context.getScheduler().stop();
                DatabaseConfig.shutdown();
                Platform.exit();
                System.exit(0);
            }));

            menu.add(showItem);
            menu.add(exitItem);

            trayIcon = new TrayIcon(image, "趣味闹钟", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Platform.runLater(() -> {
                            stage.show();
                            stage.toFront();
                        });
                    }
                }
            });

            tray.add(trayIcon);

            stage.setOnCloseRequest(e -> {
                e.consume();
                stage.hide();
                if (trayIcon != null) {
                    trayIcon.displayMessage("趣味闹钟", "程序已最小化到托盘，闹钟仍在后台运行", TrayIcon.MessageType.INFO);
                }
            });
        } catch (Exception ignored) {
        }
    }

    private Image createTrayImage() {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(231, 76, 60));
        g.fillOval(1, 1, 14, 14);
        g.dispose();
        return image;
    }
}
