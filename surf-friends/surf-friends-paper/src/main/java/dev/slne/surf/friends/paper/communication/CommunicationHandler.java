package dev.slne.surf.friends.paper.communication;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dev.slne.surf.friends.paper.PaperInstance;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.UUID;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Accessors(fluent = true)
@Getter
public class CommunicationHandler {
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
}
