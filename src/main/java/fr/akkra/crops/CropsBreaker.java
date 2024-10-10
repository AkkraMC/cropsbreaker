package fr.akkra.crops;

import fr.akkra.crops.listeners.CropsListener;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CropsBreaker extends JavaPlugin {

    @Getter public static CropsBreaker instance;
    CropsListener melonBreaker;

    public void onEnable()
    {
        this.instance = this;
        this.saveDefaultConfig();
        System.out.println("CropsBreaker - Enable (Ver 1.0.0)");
        this.melonBreaker = new CropsListener(this);
        getServer().getPluginManager().registerEvents((Listener)this.melonBreaker, (Plugin) this);
    }

    public void onDisable()
    {

    }

}
