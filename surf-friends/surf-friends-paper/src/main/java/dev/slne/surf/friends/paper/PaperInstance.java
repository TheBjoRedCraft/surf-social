package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;
import dev.slne.surf.friends.paper.listener.VelocityListener;
import dev.slne.surf.friends.velocity.VelocityInstance;
import java.util.UUID;
import java.util.logging.Level;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PaperInstance extends JavaPlugin {
  private final FriendLogger logger = new FriendLogger();

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

  public static void openMenu(UUID player){
    OfflinePlayer target = Bukkit.getOfflinePlayer(player);

    if(!target.isOnline()){
      VelocityInstance.getInstance().getLogger().log(Level.WARNING, target.getName() + ": Failed to open menu. The player is not reachable.");
      PaperInstance.instance().logger().warn(target.getName() + ": Failed to open menu. The player is not reachable.");
      return;
    }

    new FriendMainMenu().show(target.getPlayer());
  }
}
