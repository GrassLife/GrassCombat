package life.grass.grasscombat;

import life.grass.grasscombat.listener.BowListener;
import life.grass.grasscombat.listener.DamageListener;
import life.grass.grasscombat.listener.RewriteListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassCombat extends JavaPlugin implements Listener {
    private static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new RewriteListener(), this);
        Bukkit.getPluginManager().registerEvents(new BowListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
