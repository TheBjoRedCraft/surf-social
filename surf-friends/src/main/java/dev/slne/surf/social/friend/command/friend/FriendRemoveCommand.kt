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

class FriendRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.social.friend.command.remove")
        withArguments(PlayerArgument.player("target"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            pluginContainer.launch {
                val target = PlayerArgument.getPlayer("target", args)

                if (target.exists().not()) {
                    SurfFriends.send(player, MessageBuilder().error("Der Spieler wurde nicht gefunden!"))
                    return@launch
                }

                if (target.uuid == player.uniqueId) {
                    SurfFriends.send(player, MessageBuilder().error("Du kannst dich nicht selbst entfernen!"))
                    return@launch
                }

                val targetUser = FriendPlayer.getPlayer(target.uuid)
                val user = FriendPlayer.getPlayer(player.uniqueId)

                if (user.areFriends(targetUser).not()) {
                    SurfFriends.send(player, MessageBuilder().error("Du bist nicht mit diesem Spieler befreundet!"))
                    return@launch
                }

                user.removeFriend(targetUser)
                SurfFriends.send(player, MessageBuilder().primary("Du hast den Freund ").info(target.name).error(" entfernt."))
            }
        })
    }
}
