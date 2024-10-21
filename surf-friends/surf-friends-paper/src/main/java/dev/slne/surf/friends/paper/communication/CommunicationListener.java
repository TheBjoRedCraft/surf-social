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
    Bukkit.getConsoleSender().sendMessage("GOT SOMETHING");//REMOVE
    if (channel.equalsIgnoreCase("surf-friends:communication-friends")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      String players = in.readUTF();
      ObjectList<String> friends = (ObjectList<String>) Arrays.asList(players.split(", "));
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      friends.forEach(friend -> uuids.add(UUID.fromString(friend)));
      CommunicationHandler.instance().cachedFriends().put(player.getUniqueId(), uuids);

      Bukkit.getConsoleSender().sendMessage("GOT");//REMOVE

    } else if (channel.equalsIgnoreCase("surf-friends:communication-requests")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      String players = in.readUTF();
      ObjectList<String> requests = (ObjectList<String>) Arrays.asList(players.split(", "));
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      requests.forEach(friend -> uuids.add(UUID.fromString(friend)));
      CommunicationHandler.instance().cachedRequests().put(player.getUniqueId(), uuids);

      Bukkit.getConsoleSender().sendMessage("GOT");//REMOVE

    } else if (channel.equalsIgnoreCase("surf-friends:communication-server")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      CommunicationHandler.instance().cachedServer().put(player.getUniqueId(), in.readUTF());
    }
  }
}
