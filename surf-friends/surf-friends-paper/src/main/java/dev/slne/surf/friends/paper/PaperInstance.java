package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.listener.PlayerJoinListener;
import dev.slne.surf.friends.paper.listener.VelocityListener;
import dev.slne.surf.friends.velocity.VelocityFriendApiProvider;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Accessors(fluent = true)
public class PaperInstance extends JavaPlugin {

  private static final Logger log = LoggerFactory.getLogger(PaperInstance.class);
  private final FriendLogger logger = new FriendLogger();
  private final FriendApi api = VelocityFriendApiProvider.get();

  @Override
  public void onLoad() {
    logger.sender = Bukkit.getConsoleSender();
  }

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

    this.getServer().getMessenger().registerIncomingPluginChannel(this, "surf-friends:main", new VelocityListener());
  }

  public static PaperInstance instance(){
    return getPlugin(PaperInstance.class);
  }
}
