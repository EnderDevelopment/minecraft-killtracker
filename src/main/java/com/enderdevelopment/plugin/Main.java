package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Statistic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<UUID, Integer> playerKills = new HashMap<>();

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        Objects.requireNonNull(getCommand("kills")).setExecutor(this);
        Objects.requireNonNull(getCommand("discord")).setExecutor(this);
        Objects.requireNonNull(getCommand("team")).setExecutor(this);
        getLogger().info("FrozenForgePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FrozenForgePlugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("kills")) {
            // Handle kills command
            int kills = playerKills.getOrDefault(player.getUniqueId(), 0);
            player.sendMessage(Component.text("Your kills: ").append(Component.text(kills, NamedTextColor.GOLD)));
            playerKills.put(player.getUniqueId(), kills);
            return true;
        } else if (command.getName().equalsIgnoreCase("discord")) {
            // Handle discord command
            Component discordLink = Component.text("Join our Discord server: ")
                    .append(Component.text("FrozenForge", NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                    .clickEvent(ClickEvent.openUrl("https://discord.gg/frozenforge"));
            player.sendMessage(discordLink);
            return true;
        } else if (command.getName().equalsIgnoreCase("team")) {
            // Handle team command
            if (args.length == 0) {
                player.sendMessage(Component.text("Usage: /team <teamName>", NamedTextColor.RED));
                return true;
            }

            String teamName = args[0];
            playerTeams.put(player.getUniqueId(), teamName);
            updatePlayerListName(player);
            player.sendMessage(Component.text("Your team has been set to: ").append(Component.text(teamName, NamedTextColor.GOLD)));
            return true;
        }

        return false;
    }

    private void updatePlayerListName(Player player) {
        String teamName = playerTeams.getOrDefault(player.getUniqueId(), "");
        if (!teamName.isEmpty()) {
            player.setPlayerListName("[" + teamName + "] " + player.getName());
        } else {
            player.setPlayerListName(player.getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerListName(player);
    }
}