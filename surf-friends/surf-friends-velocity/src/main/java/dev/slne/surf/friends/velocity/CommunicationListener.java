package dev.slne.surf.friends.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;

public class CommunicationListener {
  public static final MinecraftChannelIdentifier COMMUNICATION_FRIENDS = MinecraftChannelIdentifier.from("surf-friends:communication-friends");
  public static final MinecraftChannelIdentifier COMMUNICATION_REQUESTS = MinecraftChannelIdentifier.from("surf-friends:communication-requests");
  public static final MinecraftChannelIdentifier COMMUNICATION_SERVER = MinecraftChannelIdentifier.from("surf-friends:communication-server");
  public static final MinecraftChannelIdentifier COMMUNICATION_MAIN = MinecraftChannelIdentifier.from("surf-friends:communication");

  @Subscribe
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getIdentifier().equals(COMMUNICATION_MAIN)) {
      if (event.getSource() instanceof ServerConnection connection) {

        Player player = connection.getPlayer();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String type = in.readUTF();

        switch (type){
          case "FRIENDS" -> {
            VelocityInstance.error("GOT");//REMOVE

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            StringBuilder builder = new StringBuilder();
            VelocityInstance.instance().friendApi().getFriends(player.getUniqueId()).getNow(new ObjectArrayList<>()).forEach(friend -> builder.append(friend.toString()).append(", "));

            out.writeUTF(builder.toString());
            player.sendPluginMessage(COMMUNICATION_FRIENDS, out.toByteArray());

            VelocityInstance.error("SEND");//REMOVE
          }

          case "REQUESTS" -> {
            VelocityInstance.error("GOT");//REMOVE
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            StringBuilder builder = new StringBuilder();
            VelocityInstance.instance().friendApi().getFriendRequests(player.getUniqueId()).getNow(new ObjectArrayList<>()).forEach(request -> builder.append(request.toString()).append(", "));

            out.writeUTF(builder.toString());
            player.sendPluginMessage(COMMUNICATION_REQUESTS, out.toByteArray());
            VelocityInstance.error("SEND");//REMOVE
          }

          case "SERVER" -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF(player.getCurrentServer().get().getServerInfo().getName());
            player.sendPluginMessage(COMMUNICATION_SERVER, out.toByteArray());
          }

          case "ADD" -> {
            VelocityInstance.instance().friendApi().addFriend(player.getUniqueId(), UUID.fromString(in.readUTF()));
          }

          case "REMOVE" -> {
            VelocityInstance.instance().friendApi().removeFriend(player.getUniqueId(), UUID.fromString(in.readUTF()));
          }

          case "SEND_REQUEST" -> {
            VelocityInstance.instance().friendApi().sendFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          }

          case "DENY_REQUEST" -> {
            VelocityInstance.instance().friendApi().denyFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          }

          case "REMOVE_BOTH" -> {
            VelocityInstance.instance().friendApi().removeFriend(player.getUniqueId(), UUID.fromString(in.readUTF()));
            VelocityInstance.instance().friendApi().removeFriend(UUID.fromString(in.readUTF()), player.getUniqueId());
          }

          case "ACCEPT_REQUEST" -> {
            VelocityInstance.instance().friendApi().acceptFriendRequest(player.getUniqueId(), UUID.fromString(in.readUTF()));
          }

          case "TOGGLE" -> {
            VelocityInstance.instance().friendApi().toggle(player.getUniqueId());
          }

        }
      }
    }
  }
}
