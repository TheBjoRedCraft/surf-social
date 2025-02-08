package dev.slne.surf.social.chat.object;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.bukkit.OfflinePlayer;

public class Channel {
  private OfflinePlayer owner;
  private ObjectSet<OfflinePlayer> members;
  private ObjectSet<OfflinePlayer> invites;

  private String name;
  private String description;

  private boolean closed;
}
