package dev.slne.surf.friends.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.friends.command.subcommand.*
import dev.slne.surf.friends.menu.FriendMainMenu
import org.bukkit.entity.Player

class FriendCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.friends.command.friend")

        withSubcommand(FriendAcceptCommand("accept"))
        withSubcommand(FriendAddCommand("add"))
        withSubcommand(FriendDenyCommand("deny"))
        withSubcommand(FriendRemoveCommand("remove"))
        withSubcommand(FriendToggleCommand("toggle"))
        withSubcommand(FriendJumpCommand("jump"))
        withSubcommand(FriendListCommand("list"))
        withSubcommand(FriendRequestListCommand("requests"))

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            FriendMainMenu().show(
                player
            )
        })
    }
}
