package dev.slne.surf.social.chat.provider;

import dev.slne.surf.social.chat.object.Channel;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ChannelProvider {
  @Getter
  private static final ChannelProvider instance = new ChannelProvider();
  private final Object2ObjectMap<UUID, Channel> channels = new Object2ObjectOpenHashMap<>();
}
