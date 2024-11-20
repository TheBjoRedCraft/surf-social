package dev.slne.surf.friends.command.subcommand

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Function

class FriendDenyCommand(name: String) : CommandAPICommand(name) {
    init {
        withArguments(
            OfflinePlayerArgument("target").replaceSafeSuggestions(
                SafeSuggestions.suggest<OfflinePlayer, CommandSender?> { info: SuggestionInfo<CommandSender?>? ->
                    Bukkit.getOnlinePlayers().toTypedArray<Player>()
                }
            )
        )

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<OfflinePlayer>("target") ?: throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.")

            GlobalScope.launch {
                if (!FriendManager.hasFriendRequest(player.uniqueId, target.uniqueId)) {
                    throw CommandAPI.failWithString("Du hast keine Freundschaftsanfrage von " + target.name)
                }
                FriendManager.denyFriendRequest(player.uniqueId, target.uniqueId)
            }
        })
    }
}
