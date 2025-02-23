package dev.slne.surf.social.friend.command

import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.friend.SurfFriends
import dev.slne.surf.social.friend.util.MessageBuilder

class SurfFriendReloadCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.social.friend.command.reload")
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            val start = System.currentTimeMillis()
            SurfFriends.instance.databaseService.disconnect()
            SurfFriends.instance.configService.loadConfig()
            SurfFriends.instance.databaseService.connect()

            val end = System.currentTimeMillis()
            SurfFriends.send(player,
                MessageBuilder().primary("Surf Friends wurde ").success("neugeladen!")
                    .darkSpacer(" (").darkSpacer((end - start).toString() + "ms").darkSpacer(")")
            )
        })
    }
}
