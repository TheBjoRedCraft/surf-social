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
  private static final FriendManager instance = new FriendManager();
  private final AsyncCache<UUID, FriendData> cache = Caffeine.newBuilder().buildAsync(Database::getFriendData);

  /* Friend Management */

  public void addFriend(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriends().contains(target)) {
        friendData.getFriends().add(target);

        cache.put(player, CompletableFuture.completedFuture(friendData));
      }
    });

    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriends().contains(player)) {
        friendData.getFriends().add(player);

        cache.put(target, CompletableFuture.completedFuture(friendData));
      }
    });
  }

  public void removeFriend(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriends().remove(target);

        cache.put(player, CompletableFuture.completedFuture(friendData));
      }
    });

    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriends().remove(player);

        cache.put(target, CompletableFuture.completedFuture(friendData));
      }
    });
  }

  public void sendFriendRequest(UUID player, UUID target) {
    this.queryFriendData(target).thenAccept(friendData -> {
      if (friendData != null && !friendData.getFriendRequests().contains(player)) {
        friendData.getFriendRequests().add(player);

        cache.put(target, CompletableFuture.completedFuture(friendData));
      }
    });
  }

  public void acceptFriendRequests(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null && friendData.getFriendRequests().contains(target)) {
        friendData.getFriendRequests().remove(target);

        cache.put(player, CompletableFuture.completedFuture(friendData));
        this.addFriend(player, target);
      }
    });
  }

  public void denyFriendRequest(UUID player, UUID target) {
    this.queryFriendData(player).thenAccept(friendData -> {
      if (friendData != null) {
        friendData.getFriendRequests().remove(target);

        cache.put(player, CompletableFuture.completedFuture(friendData));
      }
    });
  }

  public boolean areFriends(UUID player, UUID target) {
    return this.queryFriendData(player)
        .thenApply(friendData -> friendData != null && friendData.getFriends().contains(target))
        .join();
  }

  public CompletableFuture<ObjectList<UUID>> getFriends(UUID player) {
    return this.queryFriendData(player).thenApply(friendData -> friendData != null ? friendData.getFriends() : ObjectArrayList.of());
  }

  public CompletableFuture<ObjectList<UUID>> getFriendRequests(UUID player) {
    return this.queryFriendData(player).thenApply(friendData -> friendData != null ? friendData.getFriendRequests() : ObjectArrayList.of());
  }

  public boolean toggle(UUID player) {
    this.queryFriendData(player).thenApply(friendData -> {
      friendData.setAllowRequests(!friendData.getAllowRequests());

      cache.put(player, CompletableFuture.completedFuture(friendData));
      return friendData.getAllowRequests();
    });

    return true;
  }

  /* Async FriendData Management */

  private CompletableFuture<FriendData> queryFriendData(UUID player) {
    return cache.get(player, Database::getFriendData);
  }

  public CompletableFuture<Void> saveFriendData(UUID player) {
    return this.queryFriendData(player).thenCompose(friendData -> {
      if (friendData == null) {
        return CompletableFuture.completedFuture(null);
      }

      return CompletableFuture.runAsync(() -> Database.saveFriendData(friendData)).thenRun(() -> this.cache.synchronous().invalidate(player));
    });
  }
  public void saveAll() {
    ObjectList<CompletableFuture<Void>> futures = new ObjectArrayList<>();

    for (UUID player : cache.synchronous().asMap().keySet()) {
      CompletableFuture<Void> future = this.saveFriendData(player);

      futures.add(future);
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  public void sendPlayer(UUID player, UUID target) {
    //TODO: Send to Server
  }
}
