package dev.slne.surf.social.friend.command.friend

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.friend.SurfFriends
import dev.slne.surf.social.friend.player.FriendPlayer
import dev.slne.surf.social.friend.pluginContainer
import dev.slne.surf.social.friend.util.MessageBuilder
import dev.slne.surf.social.friend.util.PageableMessageBuilder
import dev.thebjoredcraft.offlinevelocity.api.OfflineVelocityAPI
import dev.thebjoredcraft.offlinevelocity.player.PlayerData

class FriendListCommand(nodeName: String): CommandAPICommand(nodeName) {
    init {
        withPermission("surf.social.friend.command.list")
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val page = args.getOrDefaultUnchecked("page", 1) as Int

            pluginContainer.launch {
                val user = FriendPlayer.getPlayer(player.uniqueId)
                val friends = user.friends

                if(friends.isEmpty()) {
                    SurfFriends.send(player, MessageBuilder().error("Du hast keine Freunde."))
                    return@launch
                }

                val onlineFriends = mutableListOf<PlayerData>()
                val offlineFriends = mutableListOf<PlayerData>()

                for (friend in friends) {
                    val friendUser = OfflineVelocityAPI.getPlayer(friend) ?: continue

                    if (SurfFriends.instance.proxyServer.getPlayer(friend).isPresent) {
                        onlineFriends.add(friendUser)
                    } else {
                        offlineFriends.add(friendUser)
                    }
                }

                val sortedFriends = onlineFriends + offlineFriends
                val messageBuilder = PageableMessageBuilder().setPageCommand("/friend list %page%")

                for (friendUser in sortedFriends) {
                    val status = if (SurfFriends.instance.proxyServer.getPlayer(friendUser.uuid).isPresent) " (Online)" else " (Offline)"
                    messageBuilder.addLine(MessageBuilder().primary(friendUser.name).darkSpacer(status).build())
                }

                messageBuilder.send(player, page)
            }
        })
    }
}