package gg.corn.antiStasis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AntiStasisCommand implements CommandExecutor, TabCompleter {

    private final AntiStasis plugin;

    public AntiStasisCommand(AntiStasis plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if the sender has permission to reload
        if (!sender.hasPermission("antistasis.reload")) {
            Component message = LegacyComponentSerializer.legacyAmpersand().deserialize("&cYou don't have permission to use this command.");
            sender.sendMessage(message);
            return true;
        }

        // Check if the command is "reload"
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            Component message = LegacyComponentSerializer.legacyAmpersand().deserialize("&cUsage: /antistasis reload");
            sender.sendMessage(message);
            return true;
        }

        // Reload the configuration
        plugin.reloadConfig();

        // Send success message
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize("&aConfiguration reloaded successfully!");
        sender.sendMessage(message);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Only provide tab completion if the sender has permission
        if (!sender.hasPermission("antistasis.reload")) {
            return completions;
        }

        // If they're typing the first argument, suggest "reload"
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }

        return completions;
    }
}

