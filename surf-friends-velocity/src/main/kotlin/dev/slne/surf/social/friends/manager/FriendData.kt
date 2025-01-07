package dev.slne.surf.social.friends.manager

import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class FriendData(
    val player: UUID,
    val friends: ObjectSet<UUID>  = ObjectArraySet(),
    val friendRequests: ObjectSet<UUID> = ObjectArraySet(),
    var allowRequests: Boolean = true
)
