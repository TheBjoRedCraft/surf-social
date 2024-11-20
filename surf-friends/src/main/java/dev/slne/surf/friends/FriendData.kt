package dev.slne.surf.friends

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

data class FriendData(
    val player: UUID,
    val friends: ObjectList<UUID>  = ObjectArrayList(),
    val friendRequests: ObjectList<UUID> = ObjectArrayList(),
    var allowRequests: Boolean = true
)
