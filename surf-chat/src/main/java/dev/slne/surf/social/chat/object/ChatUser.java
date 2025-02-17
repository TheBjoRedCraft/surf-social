package dev.slne.surf.social.chat.object;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import dev.slne.surf.social.chat.service.DatabaseService;

import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChatUser {
  @Getter
  private static final LoadingCache<UUID, ChatUser> cache = Caffeine
      .newBuilder()
      .removalListener((object, user, cause) -> DatabaseService.getInstance().saveUser((ChatUser) user))
      .expireAfterWrite(30, TimeUnit.MINUTES)
      .build(DatabaseService.getInstance()::loadUser);

  public static ChatUser getUser(UUID uuid) {
    return cache.get(uuid);
  }

  private final UUID uuid;
  private boolean toggledPM;
  private ObjectSet<UUID> ignoreList;

  public boolean isIgnoring(UUID target) {
    return ignoreList.contains(target);
  }
}