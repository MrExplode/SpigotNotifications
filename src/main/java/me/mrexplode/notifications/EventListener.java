package me.mrexplode.notifications;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class EventListener implements Listener {
    
    private TrayManager manager = Main.getTrayManager();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        manager.displayMessage(e.getPlayer().getName() + " joined the server!");
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        manager.displayMessage(e.getPlayer().getName() + " left the server!");
    }

}
