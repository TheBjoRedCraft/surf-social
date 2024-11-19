package dev.slne.surf.friends

import it.unimi.dsi.fastutil.objects.ObjectList
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.Setter
import java.util.*

@Setter
@Getter
@AllArgsConstructor

class FriendData(
    val player: UUID,
    friendsList: ObjectList<UUID>,
    friendRequestsList: ObjectList<UUID>,
    allowRequests: Boolean,
) {
    var friends: ObjectList<UUID>? = friendsList;
    var friendRequests: ObjectList<UUID>? = friendRequestsList;
    var allowRequests: Boolean? = allowRequests;
}
