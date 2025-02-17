package dev.slne.surf.social.chat.listener

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.util.Colors
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.PluginColor
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

        if (ChatFilterService.instance.containsLink(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
            return
        }

        if (ChatFilterService.instance.containsBlocked(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
            return
        }

        if (ChatFilterService.instance.isSpamming(event.player.uniqueId)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"))
            return
        }

        if (!ChatFilterService.instance.isValidInput(plainMessage)) {
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
                    SurfChat.send(onlinePlayer, MessageBuilder().component(this.getDeleteComponent(onlinePlayer, messageID)).component(this.getTeleportComponent(onlinePlayer, player.name)).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").component(getChannelComponent(channel)).miniMessage("<white>$plainMessage"))
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

                SurfChat.send(onlinePlayer, MessageBuilder().component(this@PlayerAsyncChatListener.getDeleteComponent(onlinePlayer, messageID)).component(this@PlayerAsyncChatListener.getTeleportComponent(onlinePlayer, player.name)).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").miniMessage("<white>$plainMessage"))
            }
        }
    }

    private fun getDeleteComponent(player: Player, id: Int): Component {
        return if (player.hasPermission(this.deletePerms)) Component.text("[", Colors.DARK_SPACER)
            .append(Component.text("DEL", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
            .clickEvent(ClickEvent.runCommand("/surfchat delete $id"))
            .hoverEvent(Component.text("Nachricht löschen", PluginColor.RED)) else Component.empty()
    }

    private fun getChannelComponent(channel: Channel): Component {
        return MessageBuilder().darkSpacer("[").variableKey(channel.name).darkSpacer("] ").build()
    }

    private fun getTeleportComponent(player: Player, name: String): Component {
        return if (player.hasPermission(this.teleportPerms)) Component.text("[", Colors.DARK_SPACER)
            .append(Component.text("TP", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
            .clickEvent(ClickEvent.runCommand("/tp $name"))
            .hoverEvent(Component.text("Zum Spieler teleportieren", PluginColor.BLUE_MID)) else Component.empty()
    }
}
