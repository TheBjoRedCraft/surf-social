package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.util.Components
import dev.slne.surf.social.chat.util.MessageBuilder

import me.clip.placeholderapi.PlaceholderAPI

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

import java.security.SecureRandom

object VelocityProxyService : PluginMessageListener {
    private val random: SecureRandom = SecureRandom()

    private val server: String = SurfChat.instance.config["globalization.server"] as String
    private val group: String = SurfChat.instance.config["globalization.group"] as String

    fun sendPrivateMessage(sender: Player, target: String, message: String) {
        val out = ByteArrayOutputStream()
        val dataOut = DataOutputStream(out)

        dataOut.writeUTF("directMessage")
        dataOut.writeUTF(server)
        dataOut.writeUTF(group)
        dataOut.writeUTF(message)
        dataOut.writeUTF(sender.name)
        dataOut.writeUTF(target)


        sender.sendPluginMessage(SurfChat.instance, "BungeeCord", out.toByteArray())
    }

    fun sendGlobalMessage(sender: Player, message: String) {
        val out = ByteArrayOutputStream()
        val dataOut = DataOutputStream(out)

        dataOut.writeUTF("globalMessage")
        dataOut.writeUTF(server)
        dataOut.writeUTF(group)
        dataOut.writeUTF(message)
        dataOut.writeUTF(sender.name)


        sender.sendPluginMessage(SurfChat.instance, "BungeeCord", out.toByteArray())
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "BungeeCord") return

        val inStream = DataInputStream(message.inputStream())
        val subChannel = inStream.readUTF()

        val senderGroup: String = inStream.readUTF()
        val senderServer: String = inStream.readUTF()

        if(senderGroup.equalsIgnoreCase(group)) {
            return
        }

        if(senderServer.equalsIgnoreCase(server)) {
            return
        }

        val messageID: Int = random.nextInt(1000000)

        if (subChannel == "directMessage") {
            val msg = inStream.readUTF()
            val sender = inStream.readUTF()
            val target = inStream.readUTF()

            val targetPlayer = Bukkit.getPlayer(target)

            if (targetPlayer != null) {
                SurfChat.send(targetPlayer, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(sender).darkSpacer(" ->").variableValue(" Dich: ").white(msg), MessageBuilder().primary("Klicke, um zu antworten."), "/msg " + sender + " "))
            }
        } else if (subChannel == "globalMessage") {
            val msg = inStream.readUTF()

            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                SurfChat.send(onlinePlayer, MessageBuilder().component(Components.getDeleteComponent(onlinePlayer, messageID)).component(Components.getTeleportComponent(onlinePlayer, player.name)).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").miniMessage("<white>$msg"), messageID)
            }
        }
    }

    private fun String.equalsIgnoreCase(other: String): Boolean {
        return this.equals(other, ignoreCase = true)
    }
}
