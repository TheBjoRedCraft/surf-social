package dev.slne.surf.social.friend.command.friend

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.friend.SurfFriends
import dev.slne.surf.social.friend.command.argument.PlayerArgument
import dev.slne.surf.social.friend.player.FriendPlayer
import dev.slne.surf.social.friend.pluginContainer
import dev.slne.surf.social.friend.util.MessageBuilder

class FriendToggleCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.social.friend.command.send")
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            pluginContainer.launch {
                val user = FriendPlayer.getPlayer(player.uniqueId)

                user.toggleNotifications()

                if(user.wouldLikeToBeNotified) {
                    SurfFriends.send(player, MessageBuilder().primary("Du hast die Freundschaftsbenachrichtigungen ").info("aktiviert").primary("."))
                } else {
                    SurfFriends.send(player, MessageBuilder().primary("Du hast die Freundschaftsbenachrichtigungen ").error("deaktiviert").primary("."))
                }
            }
        })
    }
}
