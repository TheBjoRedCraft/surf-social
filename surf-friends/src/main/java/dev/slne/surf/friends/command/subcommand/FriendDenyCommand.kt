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
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendDenyCommand(name: String) : CommandAPICommand(name) {

    init {
        withArguments(OfflinePlayerArgument("target").replaceSafeSuggestions(
            SafeSuggestions.suggest {
                Bukkit.getOnlinePlayers().toTypedArray<Player>()
            })
        )

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getOfflinePlayerOrFail("target")

            plugin.launch {
                if (!FriendManager.hasFriendRequest(player.uniqueId, target.uniqueId)) {
                    player.sendMessage(Component.text("Du hast keine Freundschaftsanfrage von ${target.name}", PluginColor.RED))
                    return@launch
                }

                FriendManager.denyFriendRequest(player.uniqueId, target.uniqueId)
            }
        })
    }
}
