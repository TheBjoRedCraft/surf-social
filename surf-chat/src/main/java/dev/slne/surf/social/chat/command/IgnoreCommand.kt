package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignore")
        withArguments(OfflinePlayerArgument("player"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            SurfChat.instance.launch {
                val target = args.getUnchecked<OfflinePlayer>("player") ?: return@launch
                val user: ChatUser = ChatUser.getUser(player.uniqueId)
                val targetUser: ChatUser = ChatUser.getUser(target.uniqueId)

                if(target == player) {
                    SurfChat.send(player, MessageBuilder().error("Du kannst dich nicht selbst stummschalten."))
                    return@launch
                }

                if (user.isIgnoring(targetUser.uuid)) {
                    user.ignoreList.remove(targetUser.uuid)
                    SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).success(" entstummt."))
                } else {
                    user.ignoreList.add(targetUser.uuid)
                    SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).error(" stumm geschaltet."))
                }
            }
        })
    }
}
