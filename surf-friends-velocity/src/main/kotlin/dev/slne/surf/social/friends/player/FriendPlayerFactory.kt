package dev.slne.surf.social.friends.player

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.social.friends.database.Database
import java.util.*
import java.util.concurrent.TimeUnit

object FriendPlayerFactory {
    private val cache: CoroutineLoadingCache<UUID, FriendPlayer> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .removalListener<UUID, FriendPlayer> { uuid, player, cause ->
            if (cause == RemovalCause.EXPIRED && player != null) {
                /* Save to db */
            }
        }
        .buildCoroutine(::loadFriendPlayer)

    private suspend fun loadFriendPlayer(uuid: UUID): FriendPlayer {
        return Database.getPlayer(uuid) ?: return FriendPlayer(uuid, "Unknown")
    }

    suspend fun getFriendPlayer(uuid: UUID): FriendPlayer {
        return cache.get(uuid) ?: loadFriendPlayer(uuid)
    }

    suspend fun savePlayer(player: FriendPlayer) {
        cache.put(player.uuid, player)
    }
}