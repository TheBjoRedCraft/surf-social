package dev.slne.surf.friends;

import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FriendData {
  private UUID player;
  private ObjectList<UUID> friends;
  private ObjectList<UUID> friendRequests;
  private Boolean allowRequests;
}
