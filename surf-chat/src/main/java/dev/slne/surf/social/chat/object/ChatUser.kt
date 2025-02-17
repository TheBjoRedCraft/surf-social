package dev.slne.surf.social.chat.`object`

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.RemovalCause
import dev.slne.surf.social.chat.service.DatabaseService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*


class ChatUser(
    val uuid: UUID,
    var toggledPM: Boolean = false,
    val ignoreList: ObjectSet<UUID> = ObjectArraySet()
) {

    fun isIgnoring(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    companion object {
        private val cache: LoadingCache<UUID, ChatUser> = Caffeine
            .newBuilder()
            .removalListener<Any, Any> { _: Any?, user: Any?, _: RemovalCause? -> DatabaseService.instance.saveUser(user as ChatUser) }
            .expireAfterWrite(30, java.util.concurrent.TimeUnit.MINUTES)
            .build { uuid: UUID -> DatabaseService.instance.loadUser(uuid) }

        fun getUser(uuid: UUID): ChatUser {
            return cache[uuid]
        }
    }
}