package dev.slne.surf.friends.paper.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class CommunicationListener implements PluginMessageListener {
  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
    if (channel.equalsIgnoreCase("surf-friends:communication-friends")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      int size = in.readInt();

      for (int i = 0; i < size; i++) {
        long most = in.readLong();
        long least = in.readLong();

        uuids.add(new UUID(most, least));
      }

      CommunicationHandler.instance().cachedFriends().remove(player.getUniqueId());
      CommunicationHandler.instance().cachedFriends().put(player.getUniqueId(), uuids);

    } else if (channel.equalsIgnoreCase("surf-friends:communication-requests")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      int size = in.readInt();

      Bukkit.getConsoleSender().sendMessage("GOT SIZE: " + size);

      for (int i = 0; i < size; i++) {
        long most = in.readLong();
        long least = in.readLong();

        uuids.add(new UUID(most, least));
      }

      Bukkit.getConsoleSender().sendMessage("GOT");
      Bukkit.getConsoleSender().sendMessage("GOT: [" + uuids + "]");

      CommunicationHandler.instance().cachedRequests().remove(player.getUniqueId());
      CommunicationHandler.instance().cachedRequests().put(player.getUniqueId(), uuids);

      Bukkit.getConsoleSender().sendMessage("PUT");
    } else if (channel.equalsIgnoreCase("surf-friends:communication-server")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      CommunicationHandler.instance().cachedServer().remove(player.getUniqueId());
      CommunicationHandler.instance().cachedServer().put(player.getUniqueId(), in.readUTF());
      
    }
  }
}
