package dev.slne.surf.social.chat;

import dev.slne.surf.social.chat.command.PrivateMessageCommand;
import dev.slne.surf.social.chat.service.ChatFilterService;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfChat extends JavaPlugin {
  @Override
  public void onLoad() {
    ChatFilterService.getInstance().loadBlockedWords();
  }

  @Override
  public void onEnable() {
    new PrivateMessageCommand("msg").register();
  }

  public static SurfChat getInstance() {
    return getPlugin(SurfChat.class);
  }
}
