package dev.slne.surf.social.chat.command

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
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@PlayerCommandExecutor
            val user: ChatUser = ChatUser.getUser(player.uniqueId)
            val targetUser: ChatUser = ChatUser.getUser(target.uniqueId)

            if (user.isIgnoring(targetUser.uuid)) {
                user.ignoreList.remove(targetUser.uuid)
                SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name!!).success(" entstummt."))
            } else {
                user.ignoreList.add(targetUser.uuid)
                SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name!!).error(" stumm geschaltet."))
            }
        })
    }
}
