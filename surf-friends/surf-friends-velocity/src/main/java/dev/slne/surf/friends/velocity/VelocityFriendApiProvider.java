package dev.slne.surf.friends.velocity;

import dev.slne.surf.friends.api.FriendApi;

import dev.slne.surf.friends.velocity.impl.api.FriendApiFallback;

public class VelocityFriendApiProvider {
//  private static final FriendApi friendApi = Services.serviceWithFallback(FriendApi.class).orElse(null);
private static final FriendApi friendApi = new FriendApiFallback();

  public static FriendApi get(){
    if (friendApi == null) {

    } else {
      System.out.println("VelocityFriendApiProvider wurde erfolgreich geladen.");
    }

    return friendApi;
  }
}
