package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat")
        withSubcommand(SurfChatDeleteCommand("delete"))
        withSubcommand(SurfChatChatClearCommand("clear"))
        withSubcommand(SurfChatSaveCommand("saveUsers"))
    }
}
