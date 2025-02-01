package dev.slne.surf.social.friends.player

import com.velocitypowered.api.proxy.Player

suspend fun Player.toFriendPlayer(): FriendPlayer {
    return FriendPlayerFactory.getFriendPlayer(uniqueId)
}