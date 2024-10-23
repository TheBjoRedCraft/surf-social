package dev.slne.surf.friends.paper.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.slne.surf.friends.paper.PaperInstance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class CommunicationListener implements PluginMessageListener {

  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
    if (channel.equalsIgnoreCase("surf-friends:communication-friends")) {
      this.handleFriendsMessage(player, message);

    } else if (channel.equalsIgnoreCase("surf-friends:communication-requests")) {
      this.handleRequestsMessage(player, message);

    }
  }

  private void handleFriendsMessage(Player player, byte[] message) {
    ByteArrayDataInput in = ByteStreams.newDataInput(message);
    ObjectList<UUID> uuids = new ObjectArrayList<>();
    int size = in.readInt();

    for (int i = 0; i < size; i++) {
      long most = in.readLong();
      long least = in.readLong();
      uuids.add(new UUID(most, least));
    }

    PaperInstance.instance().logger().info("Received FRIENDS message for player " + player.getName() + ": " + uuids);//REMOVE

    CommunicationHandler.instance().cachedFriends().remove(player.getUniqueId());
    CommunicationHandler.instance().cachedFriends().put(player.getUniqueId(), uuids);
  }

  private void handleRequestsMessage(Player player, byte[] message) {
    ByteArrayDataInput in = ByteStreams.newDataInput(message);
    ObjectList<UUID> uuids = new ObjectArrayList<>();
    int size = in.readInt();

    PaperInstance.instance().logger().info("GOT SIZE: " + size);//REMOVE

    for (int i = 0; i < size; i++) {
      long most = in.readLong();
      long least = in.readLong();
      uuids.add(new UUID(most, least));
    }

    PaperInstance.instance().logger().info("GOT UUIDs: " + uuids);//REMOVE

    CommunicationHandler.instance().cachedRequests().remove(player.getUniqueId());
    CommunicationHandler.instance().cachedRequests().put(player.getUniqueId(), uuids);

    PaperInstance.instance().logger().info("PUT Requests: " + uuids);//REMOVE
  }
}
