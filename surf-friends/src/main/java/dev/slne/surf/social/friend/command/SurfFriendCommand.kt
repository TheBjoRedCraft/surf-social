package dev.slne.surf.social.friend.command

import dev.jorel.commandapi.CommandAPICommand

class SurfFriendCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.social.friends.command")
        withSubcommand(SurfFriendReloadCommand("reload"))
    }
}
