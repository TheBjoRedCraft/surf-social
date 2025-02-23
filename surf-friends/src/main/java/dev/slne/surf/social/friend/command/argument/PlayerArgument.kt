package dev.slne.surf.social.friend.command.argument

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.social.friend.SurfFriends
import dev.thebjoredcraft.offlinevelocity.api.OfflineVelocityAPI
import dev.thebjoredcraft.offlinevelocity.player.PlayerData
import org.jetbrains.annotations.Contract

class PlayerArgument private constructor(nodeName: String) : StringArgument(nodeName) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            SurfFriends.instance.proxyServer.allPlayers.stream()
                .map { obj: Player -> obj.username }
                .toList()
        })
    }

    companion object {
        @Contract("_ -> new")
        fun player(nodeName: String): PlayerArgument {
            return PlayerArgument(nodeName)
        }

        suspend fun getPlayer(nodeName: String, args: CommandArguments): PlayerData {
            return OfflineVelocityAPI.getPlayer(args.getUnchecked<String>(nodeName).toString())
        }
    }
}