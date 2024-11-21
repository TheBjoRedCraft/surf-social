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
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.plugin
import dev.slne.surf.friends.prefix
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendJumpCommand(commandName: String) : CommandAPICommand(commandName) {
    
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
                    throw CommandAPI.failWithString("Du bist nicht mit " + target.name + " befreundet.")
                }

                player.sendMessage(
                    prefix.append(Component.text("Du wirst mit dem Server von " + target.name + " verbunden."))
                )

                //TODO: Cloud implementation: send player
                player.sendMessage(
                    prefix.append(
                        Component.text("Du wurdest erfolgreich mit dem Server von " + target.name + " verbunden.")
                            .color(PluginColor.LIGHT_GREEN)
                    )
                )
            }
        })
    }
}
