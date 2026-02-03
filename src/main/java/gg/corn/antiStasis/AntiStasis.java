package gg.corn.antiStasis;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiStasis extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default configuration if it doesn't exist
        saveDefaultConfig();

        // Register the player teleport listener
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);

        // Register the command
        AntiStasisCommand commandExecutor = new AntiStasisCommand(this);
        getCommand("antistasis").setExecutor(commandExecutor);
        getCommand("antistasis").setTabCompleter(commandExecutor);

        // Log successful startup
        getLogger().info("AntiStasis has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("AntiStasis has been disabled!");
    }
}
