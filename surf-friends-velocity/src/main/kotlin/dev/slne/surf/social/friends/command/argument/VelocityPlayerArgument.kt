package dev.slne.surf.social.friends.command.argument

import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.social.friends.player.FriendPlayer

class VelocityPlayerArgument(nodeName: String) : StringArgument(nodeName) {
    companion object {
        fun parse(node: String, args: CommandArguments): FriendPlayer? {
            val name = args.getUnchecked<String>(node)

            return null
        }
    }
}
