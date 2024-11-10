package dev.slne.surf.friends;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import dev.slne.surf.friends.database.Database;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class FriendManager {
  @Getter
  public static final FriendManager instance = new FriendManager();
  private final AsyncCache<UUID, FriendData> cache = Caffeine.newBuilder().buildAsync(Database::getFriendData);

  /* Friend Management */

  public void addFriend(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriends().contains(target)) {
        friendData.getFriends().add(target);
      }
    });

    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriends().contains(player)) {
        friendData.getFriends().add(player);
      }
    });
  }

  public void removeFriend(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriends().remove(target);
      }
    });

    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriends().remove(player);
      }
    });
  }

  public ObjectList<UUID> getFriends(UUID player) {
    return this.queryFriendData(player)
        .thenApply(friendData -> friendData != null ? friendData.getFriends() : new ObjectArrayList<UUID>())
        .join();
  }

  public void sendFriendRequest(UUID player, UUID target) {
    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriendRequests().contains(player)) {
        friendData.getFriendRequests().add(player);
      }
    });
  }

  public void acceptFriendRequests(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null && friendData.getFriendRequests().contains(target)) {
        friendData.getFriendRequests().remove(target);
        this.addFriend(player, target);
      }
    });
  }

  public void denyFriendRequest(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriendRequests().remove(target);
      }
    });
  }

  public boolean areFriends(UUID player, UUID target) {
    return this.queryFriendData(player)
        .thenApply(friendData -> friendData != null && friendData.getFriends().contains(target))
        .join();
  }

  public ObjectList<UUID> getFriendRequests(UUID player) {
    this.queryFriendData(player).thenAccept(friendData -> {

    });

    return null;
  }

  /* Async FriendData Management */


  private CompletableFuture<FriendData> queryFriendData(UUID player) {
    return cache.get(player, Database::getFriendData);
  }

  private CompletableFuture<Void> saveFriendData(UUID player) {
    return this.queryFriendData(player).thenCompose(friendData -> {
      if (friendData == null) {
        return CompletableFuture.completedFuture(null);
      }

      return CompletableFuture.runAsync(() -> Database.saveFriendData(friendData)).thenRun(() -> this.cache.synchronous().invalidate(player));
    });
  }
}
