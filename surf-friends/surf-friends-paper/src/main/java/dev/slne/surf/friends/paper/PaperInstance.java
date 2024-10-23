package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.command.FriendCommand;
import dev.slne.surf.friends.paper.mysqlbased.api.listener.PlayerConnectionListener;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.util.Services;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PaperInstance extends JavaPlugin {

  private final FriendLogger logger = new FriendLogger();
  private final FriendApi friendApi = Services.serviceWithFallback(FriendApi.class).orElse(null);

  @Override
  public void onLoad() {
    logger.sender = Bukkit.getConsoleSender();
  }

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);

    new FriendCommand("friend").register();

    logger.info("Successfully enabled.");
  }

  public static PaperInstance instance(){
    return getPlugin(PaperInstance.class);
  }
}
