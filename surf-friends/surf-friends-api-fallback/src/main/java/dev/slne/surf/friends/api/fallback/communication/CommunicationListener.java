package dev.slne.surf.friends.api.fallback.communication;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.slne.surf.friends.api.fallback.FriendApiFallbackInstance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Optional;
import java.util.UUID;

public class CommunicationListener {
  public static final MinecraftChannelIdentifier COMMUNICATION_FRIENDS = MinecraftChannelIdentifier.from("surf-friends:communication-friends");
  public static final MinecraftChannelIdentifier COMMUNICATION_REQUESTS = MinecraftChannelIdentifier.from("surf-friends:communication-requests");
  public static final MinecraftChannelIdentifier COMMUNICATION_SERVER = MinecraftChannelIdentifier.from("surf-friends:communication-server");

  @Subscribe
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getIdentifier().getId().equals("surf-friends:communication")) {
      if (event.getSource() instanceof Player player) {
        try {
          ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getData());
          DataInputStream in = new DataInputStream(byteArrayInputStream);

          String type = in.readUTF();

          switch (type){
            case "FRIENDS" -> {
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

              StringBuilder builder = new StringBuilder();
              FriendApiFallbackInstance.instance().friendApi().getFriends(player.getUniqueId()).getNow(new ObjectArrayList<>()).forEach(friend -> builder.append(friend.toString()).append(", "));

              out.writeUTF(builder.toString());

              player.sendPluginMessage(COMMUNICATION_FRIENDS, byteArrayOutputStream.toByteArray());
            }
            case "REQUESTS" -> {
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

              StringBuilder builder = new StringBuilder();
              FriendApiFallbackInstance.instance().friendApi().getFriendRequests(player.getUniqueId()).getNow(new ObjectArrayList<>()).forEach(request -> builder.append(request.toString()).append(", "));

              out.writeUTF(builder.toString());

              player.sendPluginMessage(COMMUNICATION_REQUESTS, byteArrayOutputStream.toByteArray());
            }
            case "SERVER" -> {
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

              out.writeUTF(player.getCurrentServer().get().getServerInfo().getName());

              player.sendPluginMessage(COMMUNICATION_SERVER, byteArrayOutputStream.toByteArray());
            }
            case "ADD" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().addFriend(player.getUniqueId(), target);
            }
            case "REMOVE" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().removeFriend(player.getUniqueId(), target);
            }
            case "SEND_REQUEST" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().sendFriendRequest(player.getUniqueId(), target);
            }
            case "DENY_REQUEST" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().denyFriendRequest(player.getUniqueId(), target);
            }
            case "REMOVE_BOTH" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().removeFriend(player.getUniqueId(), target);
              FriendApiFallbackInstance.instance().friendApi().removeFriend(target, player.getUniqueId());
            }
            case "ACCEPT_REQUEST" -> {
              UUID target = in.readUTF().isEmpty() ? null : UUID.fromString(in.readUTF());

              if(target == null){
                return;
              }

              FriendApiFallbackInstance.instance().friendApi().acceptFriendRequest(player.getUniqueId(), target);
            }
            case "TOGGLE" -> {
              FriendApiFallbackInstance.instance().friendApi().toggle(player.getUniqueId());
            }
            case "SEND" -> {
              String server = in.readUTF();
              Optional<RegisteredServer> optional = FriendApiFallbackInstance.instance().proxy().getServer(server);

              if(optional.isEmpty()){
                return;
              }

              player.createConnectionRequest(optional.get());
            }
          }

          in.close();
          byteArrayInputStream.close();
        }catch (Exception e) {
          FriendApiFallbackInstance.error(e.getMessage());
        }
      }
    }
  }
}
