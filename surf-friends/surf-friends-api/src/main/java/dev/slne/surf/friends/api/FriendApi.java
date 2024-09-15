package dev.slne.surf.friends.api;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class FriendApi {

  /**
   * Add a friend for a player.
   *
   * @param player The UUID of the player who is adding the friend.
   * @param target The UUID of the player to be added as a friend.
   * @return A boolean indicating the success or failure of the operation.
   **/
  public abstract CompletableFuture<Boolean> addFriend(UUID player, UUID target);

  /**
   * Remove a friend for a player.
   *
   * @param player The UUID of the player who is removing the friend.
   * @param target The UUID of the friend to be removed.
   * @return A boolean indicating the success or failure of the operation.
   */
  public abstract CompletableFuture<Boolean> removeFriend(UUID player, UUID target);

  /**
   * Get a list of friends for a player.
   *
   * @param player The UUID of the player.
   * @return A List containing the list of friend UUIDs.
   */
  public abstract CompletableFuture<List<UUID>> getFriends(UUID player);

  /**
   * Check if two players are friends.
   *
   * @param player The UUID of the first player.
   * @param target The UUID of the second player.
   * @return A boolean indicating if they are friends.
   */
  public abstract CompletableFuture<Boolean> areFriends(UUID player, UUID target);

  /**
   * Send a friend request to a player.
   *
   * @param player The UUID of the first player.
   * @param target The UUID of the second player.
   * @return A boolean indicating the success or failure of the operation.
   */
  public abstract CompletableFuture<Boolean> sendFriendRequest(UUID player, UUID target);

  /**
   * Get a list of friends.
   *
   * @param player The UUID of the player.
   * @return A List of UUIDs
   */
  public abstract CompletableFuture<List<UUID>> getFriendRequests(UUID player);

  /**
   * Accept a friend request.
   *
   * @param player The UUID of the first player.
   * @param target The UUID of the second player.
   * @return A boolean indicating the success or failure of the operation.
   */
  public abstract CompletableFuture<Boolean> acceptFriendRequest(UUID player, UUID target);

  /**
   * Deny a friend request.
   *
   * @param player The UUID of the first player.
   * @param target The UUID of the second player.
   * @return A boolean indicating if they are friends.
   */
  public abstract CompletableFuture<Boolean> denyFriendRequest(UUID player, UUID target);


  /**
   * Get server name from player.
   *
   * @param player The UUID of the player.
   * @return The name of the server from the player
   */
  public abstract CompletableFuture<String> getServerFromPlayer(UUID player);

  /**
   * Init Tasks for starting.
   *
   * @return A boolean indicating the success or failure of the operation.
   */
  public abstract CompletableFuture<Boolean> init();

  /**
   * Exit Tasks for stopping.
   *
   * @return A boolean indicating the success or failure of the operation.
   */
  public abstract CompletableFuture<Boolean> exit();
}
