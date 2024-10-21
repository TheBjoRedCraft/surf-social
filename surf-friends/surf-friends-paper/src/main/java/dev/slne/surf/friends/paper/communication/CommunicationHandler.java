package dev.slne.surf.friends.paper.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dev.slne.surf.friends.paper.PaperInstance;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Arrays;
import java.util.UUID;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@Getter
public class CommunicationHandler implements PluginMessageListener {
  @Getter
  private static CommunicationHandler instance = new CommunicationHandler();

  private final PaperInstance plugin = PaperInstance.instance();
  private final Object2ObjectMap<UUID, ObjectList<UUID>> cachedFriends = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectMap<UUID, ObjectList<UUID>> cachedRequests = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectMap<UUID, String> cachedServer = new Object2ObjectOpenHashMap<>();

  public void sendRequest(RequestType type, Player player, UUID target){
    try {
      switch (type) {
        case ADD, REMOVE, SEND_REQUEST, DENY_REQUEST, REMOVE_BOTH, ACCEPT_REQUEST -> {
          ByteArrayDataOutput out = ByteStreams.newDataOutput();

          out.writeUTF(type.name());
          out.writeUTF(target.toString());

          player.sendPluginMessage(plugin, "surf-friends:communication", out.toByteArray());
        }

        case FRIENDS, REQUESTS, TOGGLE -> {
          ByteArrayDataOutput out = ByteStreams.newDataOutput();

          out.writeUTF(type.name());
          out.writeUTF(UUID.randomUUID().toString());

          player.sendPluginMessage(plugin, "surf-friends:communication", out.toByteArray());
        }
      }
    }catch (Exception e){
      plugin.logger().error(e);
    }
  }

  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
    if (channel.equalsIgnoreCase("surf-friends:communication-friends")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      String players = in.readUTF();
      ObjectList<String> friends = (ObjectList<String>) Arrays.asList(players.split(", "));
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      friends.forEach(friend -> uuids.add(UUID.fromString(friend)));
      cachedFriends.put(player.getUniqueId(), uuids);

    }else if (channel.equalsIgnoreCase("surf-friends:communication-requests")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      String players = in.readUTF();
      ObjectList<String> requests = (ObjectList<String>) Arrays.asList(players.split(", "));
      ObjectList<UUID> uuids = new ObjectArrayList<>();

      requests.forEach(friend -> uuids.add(UUID.fromString(friend)));
      cachedRequests.put(player.getUniqueId(), uuids);

    } else if (channel.equalsIgnoreCase("surf-friends:communication-server")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);

      cachedServer.put(player.getUniqueId(), in.readUTF());
    }
  }
}
