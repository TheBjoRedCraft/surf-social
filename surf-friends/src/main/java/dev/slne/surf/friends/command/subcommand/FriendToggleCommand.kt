package dev.slne.surf.friends.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class FriendToggleCommand(name: String) : CommandAPICommand(name) {
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, info: CommandArguments? ->

            GlobalScope.launch {
                if (FriendManager.toggle(player.uniqueId)) {
                    player.sendMessage(SurfFriendsPlugin.prefix.append(Component.text("Du hast Freundschaftsanfragen nun aktiviert.")))
                } else {
                    player.sendMessage(SurfFriendsPlugin.prefix.append(Component.text("Du hast Freundschaftsanfragen nun deaktiviert.")))
                }
            }
        })
    }
}
