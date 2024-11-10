package dev.slne.surf.friends.config;

import dev.slne.surf.friends.SurfFriendsPlugin;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {
  public static FileConfiguration config() {
    return SurfFriendsPlugin.getInstance().getConfig();
  }

  public static void createConfig() {
    SurfFriendsPlugin.getInstance().saveDefaultConfig();
  }
}
