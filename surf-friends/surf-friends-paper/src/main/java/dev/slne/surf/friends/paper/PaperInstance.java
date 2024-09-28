package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;
import dev.slne.surf.friends.paper.listener.VelocityListener;

import java.util.UUID;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.util.Services;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PaperInstance extends JavaPlugin {
  private final FriendLogger logger = new FriendLogger();
  private final FriendApi api = Services.serviceWithFallback(FriendApi.class).orElseThrow();

  @Override
  public void onLoad() {
    logger.sender = Bukkit.getConsoleSender();
  }

  @Override
  public void onEnable() {
    //Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

    this.getServer().getMessenger().registerIncomingPluginChannel(this, "surf-friends:main", new VelocityListener());
  }

  public static PaperInstance instance(){
    return getPlugin(PaperInstance.class);
  }
}
