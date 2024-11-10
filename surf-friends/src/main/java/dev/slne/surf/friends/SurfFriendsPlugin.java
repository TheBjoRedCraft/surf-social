package dev.slne.surf.friends;

import dev.slne.surf.friends.command.FriendCommand;
import dev.slne.surf.friends.command.subcommand.FriendAddCommand;
import dev.slne.surf.friends.core.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfFriendsPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    this.registerCommands();
  }

  public static Component getPrefix() {
    return Component.text(">> ").color(NamedTextColor.GRAY)
        .append(Component.text("Friends").color(PluginColor.LIGHT_BLUE))
        .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY));
  }

  public static SurfFriendsPlugin getInstance() {
    return getPlugin(SurfFriendsPlugin.class);
  }

  private void registerCommands() {
    new FriendCommand("friend").register();
    new FriendAddCommand("fa").register();
  }
}
