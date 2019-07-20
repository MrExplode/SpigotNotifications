package me.mrexplode.notifications;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.JOptionPane;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TrayManager {
    
    private SystemTray tray;
    private TrayIcon trayicon;
    private Plugin plugin;
    
    public TrayManager(Plugin plugin) {
        tray = SystemTray.getSystemTray();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.getImage(getClass().getResource("/trayicon.png"));
        
        PopupMenu menu = new PopupMenu();
        
        MenuItem playerListItem = new MenuItem("Player list");
        playerListItem.addActionListener(e -> {
            StringBuilder b = new StringBuilder();
            Bukkit.getOnlinePlayers().forEach((t) -> b.append(t.getName() + "\n"));
            JOptionPane.showMessageDialog(null, "Online players:\n" + b.toString(), "Spigot Server", JOptionPane.INFORMATION_MESSAGE);
        });
        
        MenuItem restartItem = new MenuItem("Restart server");
        restartItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure about that?", "Spigot Server", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                onServerThread(() -> Bukkit.getServer().spigot().restart());
            }
        });
        
        MenuItem stopItem = new MenuItem("Stop server");
        stopItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure about that?", "Spigot Server", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                onServerThread(() -> Bukkit.getServer().shutdown());
            }
        });
        
        menu.add(playerListItem);
        menu.add(restartItem);
        menu.add(stopItem);
        
        trayicon = new TrayIcon(img, "Spigot Server", menu);
        trayicon.setImageAutoSize(true);
        trayicon.setToolTip("Spigot Server");
        try {
            tray.add(trayicon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }
    
    public void displayMessage(String msg) {
        trayicon.displayMessage("Spigot Server", msg, MessageType.INFO);
    }
    
    public void clearTray() {
        tray.remove(trayicon);
    }
    
    private void onServerThread(Runnable r) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

}
