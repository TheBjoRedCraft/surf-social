package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.DatabaseService
import org.bukkit.entity.Player

class SurfChatSaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.save")
        executesPlayer(PlayerCommandExecutor { _: Player, _: CommandArguments ->
            SurfChat.instance.launch {
                ChatUser.cache.synchronous().asMap().values.forEach { user ->
                    DatabaseService.instance.saveUser(user)
                }
            }
        })
    }
}