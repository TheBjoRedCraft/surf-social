package dev.slne.surf.social.friend.player

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.shynixn.mccoroutine.velocity.launch
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.social.friend.SurfFriends
import dev.slne.surf.social.friend.pluginContainer
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Data class representing a player with friends and friend requests.
 *
 * @property uuid The unique identifier of the player.
 * @property friends A set of UUIDs representing the player's friends.
 * @property friendRequests A set of UUIDs representing the player's friend requests.
 * @property wouldLikeToBeNotified A boolean indicating if the player wants to be notified.
 */
data class FriendPlayer (
    val uuid: UUID,
    val friends: ObjectSet<UUID> = ObjectArraySet(),
    val friendRequests: ObjectSet<UUID> = ObjectArraySet(),
    var wouldLikeToBeNotified: Boolean = true
) {
    companion object {
        /**
         * Cache for storing FriendPlayer instances.
         */
        private val cache: CoroutineLoadingCache<UUID, FriendPlayer> = Caffeine
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .removalListener { _: Any?, player: Any?, _: RemovalCause ->
                if (player == null) {
                    return@removalListener
                }
                pluginContainer.launch {
                    SurfFriends.instance.databaseService.savePlayer(player as FriendPlayer)
                }
            }
            .buildCoroutine() { uuid: UUID -> SurfFriends.instance.databaseService.loadPlayer(uuid) }

        /**
         * Retrieves a FriendPlayer from the cache or loads it from the database.
         *
         * @param uuid The UUID of the player to retrieve.
         * @return The FriendPlayer instance.
         */
        suspend fun getPlayer(uuid: UUID): FriendPlayer {
            return cache.get(uuid)
        }
    }

    /**
     * Adds a friend to the player's friend list.
     *
     * @param friend The FriendPlayer to add as a friend.
     */
    fun addFriend(friend: FriendPlayer) {
        if(this.areFriends(friend)) {
            return
        }

        this.friends.add(friend.uuid)
        friend.friends.add(this.uuid)
        this.saveToCache()
    }

    /**
     * Removes a friend from the player's friend list.
     *
     * @param friend The FriendPlayer to remove from the friend list.
     */
    fun removeFriend(friend: FriendPlayer) {
        if(this.areFriends(friend).not()) {
            return
        }

        this.friends.remove(friend.uuid)
        friend.friends.remove(this.uuid)
        this.saveToCache()
    }

    /**
     * Sends a friend request to another player.
     *
     * @param target The FriendPlayer to send the friend request to.
     */
    fun sendFriendRequest(target: FriendPlayer) {
        if(this.areFriends(target)) {
            return
        }

        if(target.hasRequest(this)) {
            return
        }

        if(this.hasRequest(target)) {
            this.acceptFriendRequest(target)
            return
        }

        target.friendRequests.add(this.uuid)
        this.saveToCache()
    }

    /**
     * Accepts a friend request from another player.
     *
     * @param target The FriendPlayer whose friend request to accept.
     */
    fun acceptFriendRequest(target: FriendPlayer) {
        if(this.areFriends(target)) {
            return
        }

        target.friendRequests.remove(this.uuid)
        this.addFriend(target)
        this.saveToCache()
    }

    /**
     * Denies a friend request from another player.
     *
     * @param target The FriendPlayer whose friend request to deny.
     */
    fun denyFriendRequest(target: FriendPlayer) {
        if(this.areFriends(target)) {
            return
        }

        target.friendRequests.remove(this.uuid)
        this.saveToCache()
    }

    /**
     * Checks if the player is friends with another player.
     *
     * @param friend The FriendPlayer to check.
     * @return True if they are friends, false otherwise.
     */
    fun areFriends(friend: FriendPlayer): Boolean {
        return this.friends.contains(friend.uuid) && friend.friends.contains(this.uuid)
    }

    /**
     * Checks if the player has a friend request from another player.
     *
     * @param sender The FriendPlayer who sent the friend request.
     * @return True if there is a friend request, false otherwise.
     */
    fun hasRequest(sender: FriendPlayer): Boolean {
        return this.friendRequests.contains(sender.uuid)
    }

    /**
     * Toggles the player's notification preference.
     */
    fun toggleNotifications() {
        this.wouldLikeToBeNotified = !this.wouldLikeToBeNotified

        this.saveToCache()
    }

    /**
     * Saves the player to the cache.
     */
    private fun saveToCache() {
        cache.put(this.uuid, this)
    }
}