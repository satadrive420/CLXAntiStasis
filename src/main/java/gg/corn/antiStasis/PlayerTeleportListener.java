package gg.corn.antiStasis;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class PlayerTeleportListener implements Listener {

    private final AntiStasis plugin;

    public PlayerTeleportListener(AntiStasis plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Check if the teleport cause is an ender pearl
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        Player player = event.getPlayer();

        // Check if the player has bypass permission
        if (player.hasPermission("antistasis.bypass")) {
            return;
        }

        // Get the destination location
        Location to = event.getTo();

        // Check if destination is null and if we should block null teleports
        if (to == null) {
            if (plugin.getConfig().getBoolean("block-null-teleport", false)) {
                // Get CombatLogX API to check if player is in combat
                if (isPlayerInCombat(player)) {
                    event.setCancelled(true);
                    sendErrorMessage(player);
                }
            }
            return;
        }

        // Check if the player is in combat using CombatLogX
        if (!isPlayerInCombat(player)) {
            return;
        }

        // Calculate the distance between from and to locations
        Location from = event.getFrom();
        double distance;

        // Check if we should ignore vertical distance
        boolean ignoreVertical = plugin.getConfig().getBoolean("ignore-vertical", false);

        if (ignoreVertical) {
            // Calculate horizontal distance only (X and Z axes)
            double deltaX = to.getX() - from.getX();
            double deltaZ = to.getZ() - from.getZ();
            distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        } else {
            // Calculate full 3D distance
            distance = from.distance(to);
        }

        // Get the maximum allowed distance from config
        double maxDistance = plugin.getConfig().getDouble("max-distance", 64.0);

        // If the distance exceeds the maximum, cancel the teleport
        if (distance > maxDistance) {
            event.setCancelled(true);
            sendErrorMessage(player);
        }
    }

    private boolean isPlayerInCombat(Player player) {
        try {
            // Get the CombatLogX plugin
            Plugin combatLogXPlugin = plugin.getServer().getPluginManager().getPlugin("CombatLogX");
            if (combatLogXPlugin == null || !(combatLogXPlugin instanceof ICombatLogX)) {
                return false;
            }

            ICombatLogX combatLogX = (ICombatLogX) combatLogXPlugin;
            ICombatManager combatManager = combatLogX.getCombatManager();

            // Check if the player is in combat
            return combatManager.isInCombat(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check combat status for player " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    private void sendErrorMessage(Player player) {
        String message = plugin.getConfig().getString("message", "&cYou cannot use stasis during combat.");
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player.sendMessage(component);
    }
}

