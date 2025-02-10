package dev.slne.surf.social.chat.object;

import dev.slne.surf.social.chat.provider.ChannelProvider;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
@Builder
public class Channel {
  private OfflinePlayer owner = null;
  private ObjectSet<OfflinePlayer> members = new ObjectArraySet<>();
  private ObjectSet<OfflinePlayer> moderators = new ObjectArraySet<>();
  private ObjectSet<OfflinePlayer> bannedPlayers = new ObjectArraySet<>();
  private ObjectSet<OfflinePlayer> invites = new ObjectArraySet<>();

  private String name;
  private String description;

  private boolean closed;

  public boolean isMember(OfflinePlayer player) {
    return members.contains(player);
  }

  public boolean isMember(CommandSender player) {
    if(player instanceof OfflinePlayer sender) {
      return members.contains(sender);
    }

    return false;
  }

  public boolean isOwnerO(OfflinePlayer player) {
    return owner.equals(player);
  }

  public boolean isOwner(CommandSender sender) {
    return owner.equals(sender);
  }

  public boolean hasInvite(CommandSender sender) {
    if(sender instanceof OfflinePlayer player) {
      return invites.contains(player);
    }

    return false;
  }

  public boolean isModerator(CommandSender player) {
    if(player instanceof OfflinePlayer sender) {
      return moderators.contains(sender);
    }

    return false;
  }

  public void invite(OfflinePlayer player) {
    if(members.contains(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(bannedPlayers.contains(player)) {
      return;
    }

    if(invites.contains(player)) {
      return;
    }

    invites.add(player);
  }

  public void ban(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(bannedPlayers.contains(player)) {
      return;
    }

    bannedPlayers.add(player);
    this.leave(player);
  }

  public void unban(OfflinePlayer player) {
    if(!bannedPlayers.contains(player)) {
      return;
    }

    bannedPlayers.remove(player);
  }

  public ObjectSet<Player> getOnlinePlayers() {
    return members.stream().filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).collect(Collectors.toCollection(ObjectArraySet::new));
  }

  public void promote(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    members.remove(player);
    moderators.add(player);
  }

  public void demote(OfflinePlayer player) {
    if(!moderators.contains(player)) {
      return;
    }

    moderators.remove(player);
  }

  public void kick(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    this.leave(player);
  }

  public void acceptInvite(OfflinePlayer player) {
    if(!invites.contains(player)) {
      return;
    }

    Channel channel = Channel.getChannelO(player);

    if(channel != null) {
      channel.leave(player);
    }

    this.join(player);
  }

  public void revokeInvite(OfflinePlayer player) {
    if(!invites.contains(player)) {
      return;
    }

    invites.remove(player);
  }

  public void move(OfflinePlayer player, @Nullable Channel channel) {
    this.leave(player);


    if(channel == null) {
      return;
    }

    channel.join(player);
  }

  public void join(OfflinePlayer player) {

    if(members.contains(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(invites.contains(player)) {
      invites.remove(player);
    }

    members.add(player);
  }

  public void leave(OfflinePlayer player) {
    if(owner.equals(player)) {
      this.delete();
      return;
    }

    if(!members.contains(player)) {
      return;
    }

    members.remove(player);
  }

  public void delete() {
    this.members.clear();

    this.unregister();
  }

  public void register() {
    ChannelProvider.getInstance().getChannels().put(owner.getUniqueId(), this);
  }

  public void unregister() {
    ChannelProvider.getInstance().getChannels().remove(owner.getUniqueId());
  }

  public static Channel getChannel(String name) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel -> channel.getName().equals(name)).findFirst().orElse(null);
  }

  public static Channel getChannel(CommandSender sender) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel ->  channel.isMember(sender) || channel.isOwner(sender)).findFirst().orElse(null);
  }

  public static Channel getChannelO(OfflinePlayer player) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel ->  channel.getMembers().contains(player) || channel.getOwner().equals(player)).findFirst().orElse(null);
  }
}