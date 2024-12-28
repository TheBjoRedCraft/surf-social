package dev.slne.surf.friends.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.plugin
import dev.slne.surf.friends.prefix
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class FriendToggleCommand(name: String) : CommandAPICommand(name) {
    
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            plugin.launch {
                if (FriendManager.toggle(player.uniqueId)) {
                    player.sendMessage(prefix.append(Component.text("Du hast Freundschaftsanfragen nun aktiviert.")))
                } else {
                    player.sendMessage(prefix.append(Component.text("Du hast Freundschaftsanfragen nun deaktiviert.")))
                }
            }
        })
    }
}
