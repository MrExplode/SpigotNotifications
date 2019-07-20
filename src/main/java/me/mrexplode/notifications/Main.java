package me.mrexplode.notifications;

import java.awt.SystemTray;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    private Logger logger;
    private static TrayManager manager;
    
    @Override
    public void onEnable() {
        logger = getLogger();
        if (!SystemTray.isSupported()) {
            logger.warning("[SpigotNotifications] System Tray is not supported! disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
        manager = new TrayManager(this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        manager.clearTray();
    }
    
    protected Logger getLog() {
        return logger;
    }
    
    public static TrayManager getTrayManager() {
        if (manager == null)
            throw new IllegalStateException("Wrong call");
        return manager;
    }
    
}
