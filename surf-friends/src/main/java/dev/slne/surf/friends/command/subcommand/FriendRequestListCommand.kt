package dev.slne.surf.friends.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendRequestListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            var message = "Du hast keine Freunde."
            val requests = FriendManager.instance.getFriendRequests(player.uniqueId)
            val builder = StringBuilder("Du hast aktuell <yellow>" + (requests.size) + "<white> Freundschaftsanfragen offen: ")
            var current = 0

            for (uuid in requests) {
                current++

                if (current == requests.size) {
                    builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                } else {
                    builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                        .append("<gray>, ")
                }
            }

            if (!requests.isEmpty()) {
                message = builder.toString()
            }

            player.sendMessage(SurfFriendsPlugin.prefix.append(MiniMessage.miniMessage().deserialize(message))
            )
        })
    }
}
