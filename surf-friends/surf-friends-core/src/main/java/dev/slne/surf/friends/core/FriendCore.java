package dev.slne.surf.friends.core;

import dev.slne.surf.friends.core.util.PluginColor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Accessors(fluent = true)
public class FriendCore {
  @Getter
  private static final Component prefix = Component.text(">> ").color(NamedTextColor.GRAY)
      .append(Component.text("Friends").color(PluginColor.LIGHT_BLUE))
      .append(Component.text(" |").color(NamedTextColor.DARK_GRAY))
      .append(Component.text(" ").color(NamedTextColor.WHITE));
}
