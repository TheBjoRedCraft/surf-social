package dev.slne.surf.social.chat.`object`

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.social.chat.SurfChat
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
        val cache: CoroutineLoadingCache<UUID, ChatUser> = Caffeine
            .newBuilder()
            .removalListener<Any, Any> { _: Any?, user: Any?, _: RemovalCause? -> SurfChat.instance.launch { DatabaseService.instance.saveUser(user as ChatUser) } }
            .expireAfterWrite(30, java.util.concurrent.TimeUnit.MINUTES)
            .buildCoroutine() { uuid: UUID -> DatabaseService.instance.loadUser(uuid) }

        suspend fun getUser(uuid: UUID): ChatUser {
            return this.cache.get(uuid)
        }
    }
}