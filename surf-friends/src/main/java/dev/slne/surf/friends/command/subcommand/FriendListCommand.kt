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

class FriendListCommand(commandName: String) : CommandAPICommand(commandName) {

    init {
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            plugin.launch {
                val friends = FriendManager.getFriends(player.uniqueId)

                val message = buildString {
                    append("Du hast aktuell <yellow>").append(friends.size)
                        .append("<white> Freunde: ")

                    friends.forEachIndexed { index, uuid ->
                        if (index == friends.size - 1) {
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
