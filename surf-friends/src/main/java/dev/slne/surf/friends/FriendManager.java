package dev.slne.surf.friends;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import dev.slne.surf.friends.database.Database;
import dev.slne.surf.friends.util.PluginColor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public class FriendManager {
  @Getter
  private static final FriendManager instance = new FriendManager();
  private final LoadingCache<UUID, FriendData> cache = Caffeine.newBuilder().build(FriendManager::loadFriendData);

  /* Friend Management */

  public void addFriend(UUID player, UUID target) {
    FriendData playerData = this.queryFriendData(player);
    FriendData targetData = this.queryFriendData(target);

    if(playerData.getFriends().contains(target)) {
      return;
    }

    if(targetData.getFriends().contains(player)) {
      return;
    }

    playerData.getFriends().add(target);
    cache.put(player, playerData);

    targetData.getFriends().add(player);
    cache.put(target, targetData);

    this.sendMessage(player, Component.text("Du bist nun mit ")
        .append(Component.text(this.getName(target), PluginColor.GOLD))
        .append(Component.text(" befreundet.")));
    this.sendMessage(target, Component.text("Du bist nun mit ")
        .append(Component.text(this.getName(player), PluginColor.GOLD))
        .append(Component.text(" befreundet.")));

  }

  public void removeFriend(UUID player, UUID target) {
    FriendData playerData = this.queryFriendData(player);
    FriendData targetData = this.queryFriendData(target);

    if(!playerData.getFriends().contains(target)) {
      return;
    }

    if(!targetData.getFriends().contains(player)) {
      return;
    }


    playerData.getFriends().remove(target);
    cache.put(player, playerData);

    targetData.getFriends().remove(player);
    cache.put(target, targetData);

    this.sendMessage(player, Component.text("Du hast ")
        .append(Component.text(this.getName(target), PluginColor.GOLD))
        .append(Component.text(" als Freund entfernt.")));
    this.sendMessage(target, Component.text("Du wurdest von ")
        .append(Component.text(this.getName(player), PluginColor.GOLD))
        .append(Component.text(" als Freund entfernt.")));

  }

  public void sendFriendRequest(UUID player, UUID target) {
    FriendData targetData = this.queryFriendData(target);

    if (targetData.getFriendRequests().contains(player)) {
      return;
    }

    targetData.getFriendRequests().add(player);
    cache.put(target, targetData);

    this.sendMessage(player, Component.text("Du hast eine Freundschaftsanfrage an ")
        .append(Component.text(this.getName(target), PluginColor.GOLD))
        .append(Component.text(" gesendet.")));

    if(targetData.getAllowRequests()){
      this.sendMessage(target, Component.text("Du hast eine Freundschaftsanfrage von ")
          .append(Component.text(this.getName(player), PluginColor.GOLD))
          .append(Component.text(" erhalten.")));
    }
  }

  public void acceptFriendRequests(UUID player, UUID target) {
    FriendData playerData = this.queryFriendData(player);
    if (!playerData.getFriendRequests().contains(target)) {
      return;
    }

    playerData.getFriendRequests().remove(target);
    cache.put(player, playerData);

    this.addFriend(player, target);

    this.sendMessage(player, Component.text("Du hast die Freundschaftsanfrage von ")
        .append(Component.text(this.getName(target), PluginColor.GOLD))
        .append(Component.text(" akzeptiert.")));
    this.sendMessage(target, Component.text("Die Freundschaftsanfrage an ")
        .append(Component.text(this.getName(player), PluginColor.GOLD))
        .append(Component.text(" wurde akzeptiert.")));
  }

  public void denyFriendRequest(UUID player, UUID target) {
    FriendData playerData = this.queryFriendData(player);

    playerData.getFriendRequests().remove(target);
    cache.put(player, playerData);

    this.sendMessage(player, Component.text("Du hast die Freundschaftsanfrage von ")
        .append(Component.text(this.getName(target), PluginColor.GOLD))
        .append(Component.text(" abgelehnt.")));
    this.sendMessage(target, Component.text("Die Freundschaftsanfrage an ")
        .append(Component.text(this.getName(player), PluginColor.GOLD))
        .append(Component.text(" wurde abgelehnt.")));
  }

  public boolean areFriends(UUID player, UUID target) {
    return this.queryFriendData(player).getFriends().contains(target);
  }

  public boolean hasFriendRequest(UUID player, UUID target) {
    return this.queryFriendData(player).getFriendRequests().contains(target);
  }

  public ObjectList<UUID> getFriends(UUID player) {
    return this.queryFriendData(player).getFriends();
  }

  public ObjectList<UUID> getFriendRequests(UUID player) {
    return this.queryFriendData(player).getFriendRequests();
  }

  public boolean toggle(UUID player) {
    FriendData playerData = this.queryFriendData(player);

    playerData.setAllowRequests(!playerData.getAllowRequests());
    cache.put(player, playerData);

    return playerData.getAllowRequests();
  }

  public boolean isAllowingRequests(UUID player) {
    return this.queryFriendData(player).getAllowRequests();
  }

  public FriendData queryFriendData(UUID player) {
    if (cache.get(player) == null) {
      return newFriendData(player);
    }

    return cache.get(player);
  }

  public static FriendData loadFriendData(UUID player) {
    Database.getFriendData(player).thenApply(friendData -> {
      if (friendData == null) {
        return newFriendData(player);
      }

      return friendData;
    });
    return null;
  }

  public static @NotNull CompletableFuture<FriendData> loadFriendDataAsync(UUID player) {
    return Database.getFriendData(player).thenApply(friendData -> Objects.requireNonNullElseGet(friendData, () -> newFriendData(player)));
  }


  public CompletableFuture<Void> saveFriendData(UUID player) {
    return CompletableFuture.runAsync(() -> {
      FriendData friendData = this.queryFriendData(player);
      Database.saveFriendData(friendData).join();

      cache.invalidate(player);
    });
  }

  public CompletableFuture<Void> saveAll(boolean closeConnection) {
    ObjectList<CompletableFuture<Void>> futures = new ObjectArrayList<>();

    for (UUID player : cache.asMap().keySet()) {
      CompletableFuture<Void> future = this.saveFriendData(player);
      futures.add(future);
    }

    CompletableFuture<Void> allSaves = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    if (closeConnection) {
      return allSaves.thenCompose(v -> CompletableFuture.runAsync(Database::closeConnection));
    } else {
      return allSaves;
    }
  }


  public void sendPlayer(UUID player, UUID target) {
    // TODO: Send to Server
  }

  private void sendMessage(UUID uuid, Component message) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      player.sendMessage(SurfFriendsPlugin.getPrefix().append(message));
    }
  }
  
  private String getName(UUID uuid) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    
    return offlinePlayer.getName() == null ? "Unbekannt" : offlinePlayer.getName();
  }

  public static FriendData newFriendData(UUID player){
    return new FriendData(player, new ObjectArrayList<>(), new ObjectArrayList<>(), true);
  }

  public ObjectList<Player> getOnlineFriends(UUID player) {
    //TODO: Cloud implementation

    return null;
  }
}
