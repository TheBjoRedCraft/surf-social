package dev.slne.surf.social.chat.provider

import dev.slne.surf.social.chat.`object`.Channel
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

import org.bukkit.entity.Player
import java.util.*

class ChannelProvider {
    val channels: Object2ObjectMap<UUID, Channel> = Object2ObjectOpenHashMap()

    fun exists(name: String): Boolean {
        return channels.values.stream().filter { channel: Channel -> channel.name == name }
            .findFirst()
            .orElse(null) != null
    }

    fun handleQuit(player: Player) {
        val channel: Channel = Channel.getChannel(player) ?: return

        if (channel.owner == player) {
            channel.delete()
        }
    }

    companion object {
        val instance = ChannelProvider()
    }
}
