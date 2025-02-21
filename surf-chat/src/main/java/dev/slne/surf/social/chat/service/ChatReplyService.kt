package dev.slne.surf.social.chat.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.UUID

object ChatReplyService {
    private val cache: Cache<UUID, UUID> = Caffeine
        .newBuilder()
        .build()

    fun get(player: UUID): UUID? {
        return cache.getIfPresent(player)
    }
}