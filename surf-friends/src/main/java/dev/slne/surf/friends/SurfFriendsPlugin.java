package dev.slne.surf.friends;

import dev.slne.surf.friends.command.FriendCommand;
import dev.slne.surf.friends.command.subcommand.FriendAddCommand;
import dev.slne.surf.friends.config.PluginConfig;
import dev.slne.surf.friends.database.Database;
import dev.slne.surf.friends.listener.PlayerQuitListener;

import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfFriendsPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    this.registerCommands();
    this.registerListener();

    PluginConfig.createConfig();
    Database.createConnection();
  }

  @Override
  public void onDisable() {
    FriendManager.instance().saveAll(true).join();
  }

  public static Component getPrefix() {
    return Component.text(">> ").color(NamedTextColor.GRAY)
        .append(Component.text("Friends").color(PluginColor.BLUE_LIGHT))
        .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY));
  }

  public static SurfFriendsPlugin getInstance() {
    return getPlugin(SurfFriendsPlugin.class);
  }

  private void registerCommands() {
    new FriendCommand("friend").register();
    new FriendAddCommand("fa").register();
  }

  private void registerListener() {
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
  }
}
