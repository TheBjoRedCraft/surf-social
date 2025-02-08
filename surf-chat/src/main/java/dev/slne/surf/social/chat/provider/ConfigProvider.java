package dev.slne.surf.social.chat.provider;

import dev.slne.surf.social.chat.SurfChat;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigProvider {
  @Getter
  public static ConfigProvider instance = new ConfigProvider();
  private final FileConfiguration config = SurfChat.getInstance().getConfig();

  private String PRIVATE_MESSAGE_FORMAT;

  public void reload() {
    PRIVATE_MESSAGE_FORMAT = config.getString("private-message-format");
  }
}
