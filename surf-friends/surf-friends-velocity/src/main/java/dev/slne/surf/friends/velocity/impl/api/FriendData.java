package dev.slne.surf.friends.velocity.impl.api;

import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendData {
  private ObjectList<UUID> friendList;
  private ObjectList<UUID> friendRequests;
  private Boolean allowRequests;

  public FriendData friendList(ObjectList<UUID> friendList){
    this.friendList = friendList;
    return this;
  }

  public FriendData friendRequests(ObjectList<UUID> friendRequests){
    this.friendRequests = friendRequests;
    return this;
  }

  public FriendData allowRequests(Boolean allowRequests){
    this.allowRequests = allowRequests;
    return this;
  }
}
