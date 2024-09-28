package dev.slne.surf.friends.velocity.impl.api;

import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class FriendData {
  private List<UUID> friendList;
  private List<UUID> friendRequests;
  private Boolean allowRequests;

  public FriendData friendList(List<UUID> friendList){
    this.friendList = friendList;
    return this;
  }

  public FriendData friendRequests(List<UUID> friendRequests){
    this.friendRequests = friendRequests;
    return this;
  }

  public FriendData allowRequests(Boolean allowRequests){
    this.allowRequests = allowRequests;
    return this;
  }

  public String serialize() {
    return new Gson().toJson(this);
  }

  public static FriendData deserialize(String json) {
    return new Gson().fromJson(json, FriendData.class);
  }
}
