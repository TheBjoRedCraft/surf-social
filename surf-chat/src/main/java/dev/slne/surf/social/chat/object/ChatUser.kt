package dev.slne.surf.social.chat.`object`

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.RemovalCause
import it.unimi.dsi.fastutil.objects.ObjectSet


class ChatUser {
    private val uuid: java.util.UUID? = null
    private val toggledPM = false
    private val ignoreList: ObjectSet<java.util.UUID>? = null

    fun isIgnoring(target: java.util.UUID): Boolean {
        return ignoreList!!.contains(target)
    }

    companion object {
        private val cache: LoadingCache<java.util.UUID, ChatUser> = Caffeine
            .newBuilder()
            .removalListener<Any, Any> { `object`: Any?, user: Any?, cause: RemovalCause? ->
                DatabaseService.getInstance().saveUser(user as ChatUser?)
            }
            .expireAfterWrite(30, java.util.concurrent.TimeUnit.MINUTES)
            .build<java.util.UUID, ChatUser>(CacheLoader<java.util.UUID, ChatUser> { uuid: java.util.UUID? ->
                DatabaseService.getInstance().loadUser(uuid)
            })

        fun getUser(uuid: java.util.UUID): ChatUser? {
            return cache[uuid]
        }
    }
}