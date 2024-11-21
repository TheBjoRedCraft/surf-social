package dev.slne.surf.friends.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.command.getOfflinePlayerOrFail
import dev.slne.surf.friends.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendRemoveCommand(name: String) : CommandAPICommand(name) {
    
    init {
        OfflinePlayerArgument("target").replaceSafeSuggestions(
            SafeSuggestions.suggest {
                Bukkit.getOnlinePlayers().toTypedArray<Player>()
            }
        )

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getOfflinePlayerOrFail("target")

            plugin.launch {
                if (!FriendManager.areFriends(player.uniqueId, target.uniqueId)) {
                    throw CommandAPI.failWithString(
                        String.format(
                            "Du bist nicht mit %s befreundet.",
                            target.name
                        )
                    )
                }

                FriendManager.removeFriend(player.uniqueId, target.uniqueId)
                FriendManager.removeFriend(target.uniqueId, player.uniqueId)
            }
        })
    }
}
