package dev.slne.surf.friends.paper.communication;

import dev.slne.surf.friends.paper.PaperInstance;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

@Accessors(fluent = true)
public class CommunicationHandler {
  @Getter
  private static CommunicationHandler instance = new CommunicationHandler();

  private final PaperInstance plugin = PaperInstance.instance();

  public void sendRequest(RequestType type, Player player, Player target){
    try {
      switch (type) {
        case ADD, REMOVE, CONNECTED, SEND_REQUEST, DENY_REQUEST, REQUEST_SERVER -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

          out.writeUTF(type.toString());
          out.writeUTF(target.getUniqueId().toString());

          player.sendPluginMessage(plugin, "surf-friends:communication", byteArrayOutputStream.toByteArray());

          out.close();
          byteArrayOutputStream.close();
        }
        case FRIENDS, REQUESTS -> {
          player.sendPluginMessage(plugin, "surf-friends:communication", type.toString().getBytes(StandardCharsets.UTF_8));
        }
      }
    }catch (Exception e){
      plugin.logger().error(e);
    }
  }

}
