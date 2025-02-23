package dev.slne.surf.social.friend.command.friend

import dev.jorel.commandapi.CommandAPICommand

class FriendCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.social.friend.command")
        withSubcommand(FriendToggleCommand("toggle"))
        withSubcommand(FriendAcceptCommand("accept"))
        withSubcommands(FriendDenyCommand("deny"))
        withSubcommands(FriendSendCommand("add"))
        withSubcommands(FriendRemoveCommand("remove"))
        withSubcommands(FriendListCommand("list"))
    }
}
