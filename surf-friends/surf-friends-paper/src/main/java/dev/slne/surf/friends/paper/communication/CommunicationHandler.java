package dev.slne.surf.friends.paper.communication;

import dev.slne.surf.friends.paper.PaperInstance;
import dev.slne.surf.friends.paper.gui.sub.friend.FriendFriendsMenu;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

          out.writeUTF(type.toString());
          out.writeUTF(target.toString());

          player.sendPluginMessage(plugin, "surf-friends:communication", byteArrayOutputStream.toByteArray());

          out.close();
          byteArrayOutputStream.close();
        }

        case FRIENDS, REQUESTS, SEND -> {
          player.sendPluginMessage(plugin, "surf-friends:communication", type.toString().getBytes(StandardCharsets.UTF_8));
        }
      }
    }catch (Exception e){
      plugin.logger().error(e);
    }
  }

  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
    if (channel.equalsIgnoreCase("surf-friends:communication-friends")) {
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

      try {
        String players = in.readUTF();
        ObjectList<String> friends = (ObjectList<String>) Arrays.asList(players.split(", "));
        ObjectList<UUID> uuids = new ObjectArrayList<>();

        friends.forEach(friend -> uuids.add(UUID.fromString(friend)));

        cachedFriends.put(player.getUniqueId(), uuids);

      } catch (IOException e) {
        plugin.logger().error(e);
      }
    }else if (channel.equalsIgnoreCase("surf-friends:communication-requests")) {
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

      try {
        String players = in.readUTF();
        ObjectList<String> requests = (ObjectList<String>) Arrays.asList(players.split(", "));
        ObjectList<UUID> uuids = new ObjectArrayList<>();

        requests.forEach(friend -> uuids.add(UUID.fromString(friend)));

        cachedRequests.put(player.getUniqueId(), uuids);

      } catch (IOException e) {
        plugin.logger().error(e);
      }
    }else if (channel.equalsIgnoreCase("surf-friends:communication-server")) {
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

      try {
        String players = in.readUTF();
        ObjectList<String> requests = (ObjectList<String>) Arrays.asList(players.split(", "));
        ObjectList<UUID> uuids = new ObjectArrayList<>();

        requests.forEach(friend -> uuids.add(UUID.fromString(friend)));

        cachedRequests.put(player.getUniqueId(), uuids);

      } catch (IOException e) {
        plugin.logger().error(e);
      }
    }
  }
}
