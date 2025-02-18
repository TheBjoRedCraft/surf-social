package dev.slne.surf.social.chat.listener

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.util.Colors
import dev.slne.surf.social.chat.util.Components
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.PluginColor
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.security.SecureRandom

class PlayerAsyncChatListener : Listener {
    private val random: SecureRandom = SecureRandom()
    private val deletePerms = "surf.chat.delete"
    private val teleportPerms = "surf.chat.teleport"

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (event.isCancelled) {
            return
        }

        if (ChatFilterService.containsLink(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
            return
        }

        if (ChatFilterService.containsBlocked(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
            return
        }

        if (ChatFilterService.isSpamming(event.player.uniqueId)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"))
            return
        }

        if (!ChatFilterService.isValidInput(plainMessage)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"))
            return
        }

        if (BasicPunishApi.isMuted(player)) {
            SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht chatten."))
            event.isCancelled = true
            return
        }

        event.isCancelled = true

        val channel: Channel? = Channel.getChannel(player)
        val messageID: Int = random.nextInt(1000000)
        var found = false

        if (channel != null) {
            if (plainMessage.startsWith("@all")) {
                plainMessage = plainMessage.replaceFirst("@all".toRegex(), "").trim { it <= ' ' }
                found = true
            } else if (plainMessage.startsWith("@a")) {
                plainMessage = plainMessage.replaceFirst("@a".toRegex(), "").trim { it <= ' ' }
                found = true
            }

            if (!found) {
                for (onlinePlayer in channel.onlinePlayers) {
                    SurfChat.send(onlinePlayer, MessageBuilder().component(Components.getDeleteComponent(onlinePlayer, messageID)).component(Components.getTeleportComponent(onlinePlayer, player.name)).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").component(Components.getChannelComponent(channel)).miniMessage("<white>$plainMessage"), messageID)
                }
            }
            return
        }

        SurfChat.instance.launch {
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                val targetUser = ChatUser.getUser(onlinePlayer.uniqueId)

                if(targetUser.isIgnoring(player.uniqueId)) {
                    continue
                }

                SurfChat.send(onlinePlayer, MessageBuilder().component(Components.getDeleteComponent(onlinePlayer, messageID)).component(Components.getTeleportComponent(onlinePlayer, player.name)).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").miniMessage("<white>$plainMessage"), messageID)
            }
        }
    }
}
