package me.mrexplode.notifications;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TrayManager {
    
    private SystemTray tray;
    private TrayIcon trayicon;
    private Plugin plugin;
    
    public TrayManager(Plugin plugin) {
        this.plugin = plugin;
        tray = SystemTray.getSystemTray();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.getImage(getClass().getResource("/trayicon.png"));
        
        PopupMenu menu = new PopupMenu();
        
        //player list
        MenuItem playerListItem = new MenuItem("Player list");
        playerListItem.addActionListener(e -> {
            StringBuilder b = new StringBuilder();
            Bukkit.getOnlinePlayers().forEach((t) -> b.append(t.getName() + "\n"));
            infoMsg("Online players:\n" + b.toString());
        });
        
        //kick player
        MenuItem playerKickItem = new MenuItem("Kick player...");
        playerKickItem.addActionListener(e -> {
            String[] data = promptUser("Kick player", "Player:", "Reason:");
            if (data == null) return;
            
            onServerThread(() -> {
                if (!Bukkit.getPlayer(data[0]).isOnline()) {
                    warnMsg(data[0] + " is not online!");
                    return;
                }
                Bukkit.getPlayer(data[0]).kickPlayer(data[1]);
            });
        });
        
        //ban
        MenuItem playerBanItem = new MenuItem("Ban player...");
        playerKickItem.addActionListener(e -> {
            String[] data = promptUser("Ban player", "Player:", "Reason:");
            if (data == null) return;
            
            onServerThread(() -> {
                Bukkit.getBanList(Type.NAME).addBan(data[0], data[1], null, "ServerManager");
                Bukkit.getPlayer(data[0]).kickPlayer(data[0]);
            });
        });
        
        //unban
        MenuItem playerUnbanItem = new MenuItem("Unban player...");
        playerUnbanItem.addActionListener(e -> {
            String[] data = promptUser("Unban player", "Player:");
            //user closed the dialog
            if (data == null) return;
            
            onServerThread(() -> {
                try {
                    Bukkit.getBanList(Type.NAME).getBanEntries().remove(Bukkit.getBanList(Type.NAME).getBanEntry(data[0]));
                } catch (Exception ex) {
                    warnMsg("No player ban exists for this name!");
                }
            });
        });
        
        //restart
        MenuItem restartItem = new MenuItem("Restart server");
        restartItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure about that?", "Spigot Server", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                onServerThread(() -> Bukkit.getServer().spigot().restart());
            }
        });
        
        //stop
        MenuItem stopItem = new MenuItem("Stop server");
        stopItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure about that?", "Spigot Server", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                onServerThread(() -> Bukkit.getServer().shutdown());
            }
        });
        
        menu.add(playerListItem);
        menu.add(playerKickItem);
        menu.add(playerBanItem);
        menu.add(playerUnbanItem);
        menu.addSeparator();
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
    
    //////////////////////////////////////////////////////////
    ///
    /// Helper methods
    ///
    //////////////////////////////////////////////////////////
    
    private void onServerThread(Runnable r) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r);
    }
    
    public void infoMsg(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Spigot Server", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void warnMsg(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Spigot Server", JOptionPane.WARNING_MESSAGE);
    }
    
    public String[] promptUser(String title, String... fields) {
        //fucking idiot users not supported. fill out every field.
        JComponent[] input = new JComponent[fields.length * 2];
        
        //pretty sketchy solve for double control
        int var1 = 0;
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i];
            input[var1] = new JLabel(fieldName);
            input[var1 + 1] = new JTextField();
            var1 +=2;
        }
        
        int res = JOptionPane.showConfirmDialog(null, input, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION)
            return null;
        
        String[] response = new String[fields.length];
        
        for (int i = 0; i < input.length; i++) {
            if (input[i] instanceof JTextField) {
                response[i / 2] = ((JTextField) input[i]).getText();
            }
        }
        
        return response;
    }

}
