package dev.slne.surf.friends.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import kotlinx.coroutines.launch
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FriendListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            SurfFriendsPlugin.instance.launch {
                var message = "Du hast keine Freunde."
                val friends = FriendManager.getFriends(player.uniqueId)
                val builder = StringBuilder("Momentan hast du <yellow>" + (friends?.size) + "<white> Freunde: ")
                var current = 0

                for (uuid in friends) {
                    current++

                    if (current == friends.size) {
                        builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                    } else {
                        builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).name)
                            .append("<gray>, ")
                    }
                }

                if (!friends.isEmpty()) {
                    message = builder.toString()
                }

                player.sendMessage(SurfFriendsPlugin.prefix.append(MiniMessage.miniMessage().deserialize(message)))
            }
        })
    }
}
