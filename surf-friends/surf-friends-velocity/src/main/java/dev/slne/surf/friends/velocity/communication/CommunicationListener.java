package dev.slne.surf.friends.velocity.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import dev.slne.surf.friends.velocity.VelocityInstance;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.UUID;

public class CommunicationListener {

  public static final MinecraftChannelIdentifier COMMUNICATION_FRIENDS = MinecraftChannelIdentifier.from("surf-friends:communication-friends");
  public static final MinecraftChannelIdentifier COMMUNICATION_REQUESTS = MinecraftChannelIdentifier.from("surf-friends:communication-requests");
  public static final MinecraftChannelIdentifier COMMUNICATION_MAIN = MinecraftChannelIdentifier.from("surf-friends:communication");

  @Subscribe
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getIdentifier().equals(COMMUNICATION_MAIN)) {
      if (event.getSource() instanceof ServerConnection connection) {

        Player player = connection.getPlayer();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String type = in.readUTF();

        switch (type) {
          case "FRIENDS" -> updateFriends(connection);
          case "REQUESTS" -> updateRequests(connection);
          case "ADD" -> VelocityInstance.instance().friendApi().addFriend(player.getUniqueId(), UUID.fromString(in.readUTF()));
          case "REMOVE" -> VelocityInstance.instance().friendApi().removeFriend(player.getUniqueId(), UUID.fromString(in.readUTF()));
          case "SEND_REQUEST" -> VelocityInstance.instance().friendApi().sendFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          case "DENY_REQUEST" -> VelocityInstance.instance().friendApi().denyFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          case "REMOVE_BOTH" -> {
            UUID friend = UUID.fromString(in.readUTF());
            VelocityInstance.instance().friendApi().removeFriend(player.getUniqueId(), friend);
            VelocityInstance.instance().friendApi().removeFriend(friend, player.getUniqueId());
          }
          case "ACCEPT_REQUEST" -> VelocityInstance.instance().friendApi().acceptFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          case "TOGGLE" -> VelocityInstance.instance().friendApi().toggle(player.getUniqueId());
        }
      }
    }
  }

  public static void updateFriends(ServerConnection connection) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    ObjectList<UUID> friends = VelocityInstance.instance().friendApi().getFriends(connection.getPlayer().getUniqueId());

    out.writeInt(friends.size());

    friends.forEach(friend -> {
      out.writeLong(friend.getMostSignificantBits());
      out.writeLong(friend.getLeastSignificantBits());
    });

    VelocityInstance.info("Sending FRIENDS data for player " + connection.getPlayer().getUsername() + ": " + friends);//REMOVE

    connection.getServer().sendPluginMessage(COMMUNICATION_FRIENDS, out.toByteArray());
  }

  public static void updateRequests(ServerConnection connection) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    ObjectList<UUID> requests = VelocityInstance.instance().friendApi().getFriendRequests(connection.getPlayer().getUniqueId());

    out.writeInt(requests.size());

    requests.forEach(request -> {
      out.writeLong(request.getMostSignificantBits());
      out.writeLong(request.getLeastSignificantBits());
    });

    VelocityInstance.info("Sending REQUESTS data for player " + connection.getPlayer().getUsername() + ": " + requests);//REMOVE

    connection.getServer().sendPluginMessage(COMMUNICATION_REQUESTS, out.toByteArray());
  }
}
