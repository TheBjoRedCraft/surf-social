package dev.slne.surf.social.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.slne.surf.social.chat.object.Message;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class ChatHistoryService {
  @Getter
  private static final ChatHistoryService instance = new ChatHistoryService();
  private final Cache<UUID, Int2ObjectMap<Message>> chatHistoryCache = Caffeine
      .newBuilder()
      .build();

  public void clearInternalChatHistory(UUID player) {
    chatHistoryCache.invalidate(player);
  }

  public void clearChat() {
    this.chatHistoryCache.invalidateAll();

    Bukkit.getOnlinePlayers().forEach(player -> {
      int index = 0;
      while (index < 100) {
        player.sendMessage(Component.empty());
        index++;
      }
    });
  }

  public void insertNewMessage(UUID player, Message message) {
      Int2ObjectMap<Message> chatHistory = chatHistoryCache.getIfPresent(player);

      if (chatHistory == null) {
          chatHistory = new Int2ObjectOpenHashMap<>();
          chatHistoryCache.put(player, chatHistory);
      }

      int timestamp = (int) (System.currentTimeMillis() / 1000);
      chatHistory.put(timestamp, message);
  }

  public Int2ObjectMap<Message> getChatHistory(UUID player) {
    return chatHistoryCache.getIfPresent(player);
  }

  public void removeMessage(UUID player, int index) {
    Int2ObjectMap<Message> chatHistory = chatHistoryCache.getIfPresent(player);

    if (chatHistory != null) {
      chatHistory.remove(index);
    }
  }

  public void resend(UUID player) {
      Int2ObjectMap<Message> chatHistory = chatHistoryCache.getIfPresent(player);

      Bukkit.getOnlinePlayers().forEach(online -> {
          int index = 0;
          while (index < 100) {
              online.sendMessage(Component.empty());
              index++;
          }
      });

      if (chatHistory != null) {
          chatHistory.int2ObjectEntrySet()
              .stream()
              .sorted(Map.Entry.comparingByKey())
              .forEach(entry -> {
                  Bukkit.getOnlinePlayers().forEach(target -> target.sendMessage(entry.getValue().message()));
              });
      }
  }
}
