package dev.slne.surf.friends.paper.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class VelocityListener implements PluginMessageListener {

  @Override
  public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
    if (!channel.equals("surf-friends:main")) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(message);
    String menu = in.readUTF();

    switch (menu){
      case "friends:main" -> new FriendMainMenu().show(player);
    }
  }
}
