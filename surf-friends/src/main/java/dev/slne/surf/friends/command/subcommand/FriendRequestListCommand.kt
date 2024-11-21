package dev.slne.surf.friends.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.plugin
import dev.slne.surf.friends.prefix
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendRequestListCommand(commandName: String) : CommandAPICommand(commandName) {

    init {
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            plugin.launch {
                val requests = FriendManager.getFriendRequests(player.uniqueId)

                val message = buildString {
                    append("Du hast aktuell <yellow>").append(requests.size)
                        .append("<white> Freundschaftsanfragen offen: ")

                    requests.forEachIndexed { index, uuid ->
                        if (index == requests.size - 1) {
                            append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                        } else {
                            append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                                .append("<gray>, ")
                        }
                    }
                }

                player.sendMessage(
                    prefix.append(MiniMessage.miniMessage().deserialize(message))
                )
            }
        })
    }
}
