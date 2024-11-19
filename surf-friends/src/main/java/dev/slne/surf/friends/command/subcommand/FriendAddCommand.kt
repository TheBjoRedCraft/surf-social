package dev.slne.surf.friends.command.subcommand

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.FriendManager
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Function

class FriendAddCommand(name: String) : CommandAPICommand(name) {
    init {
        withArguments(
            OfflinePlayerArgument("target").replaceSafeSuggestions(
                SafeSuggestions.suggest<OfflinePlayer, CommandSender?> { info: SuggestionInfo<CommandSender?>? ->
                    Bukkit.getOnlinePlayers().toTypedArray<Player>()
                }
            )
        )

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<OfflinePlayer>("target")
                ?: throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.")
            if (FriendManager.instance.hasFriendRequest(player.uniqueId, target.uniqueId)) {
                throw CommandAPI.failWithString("Du hast bereits Freundschaftsanfrage von " + target.name)
            }

            if (FriendManager.instance.hasFriendRequest(target.uniqueId, player.uniqueId)) {
                throw CommandAPI.failWithString("Du hast bereits eine Freundschaftsanfrage an " + target.name + " gesendet.")
            }

            if (target == player) {
                throw CommandAPI.failWithString("Du kannst nicht mit dir selbst befreundet sein.")
            }
            FriendManager.instance.sendFriendRequest(player.uniqueId, target.uniqueId)
        })
    }
}
