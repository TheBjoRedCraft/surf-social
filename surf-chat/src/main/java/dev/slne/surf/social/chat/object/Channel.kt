package dev.slne.surf.social.chat.`object`

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Channel {
    private val owner: OfflinePlayer? = null
    private val members: ObjectSet<OfflinePlayer> = ObjectArraySet()
    private val moderators: ObjectSet<OfflinePlayer> = ObjectArraySet()
    private val bannedPlayers: ObjectSet<OfflinePlayer> = ObjectArraySet()
    private val invites: ObjectSet<OfflinePlayer> = ObjectArraySet()
    private val name: String = java.util.UUID.randomUUID().toString()
    private val description = "Ein cooler Kanal!"
    private val closed = true

    fun isMember(player: OfflinePlayer): Boolean {
        return members.contains(player)
    }

    fun isMember(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return members.contains(player)
        }

        return false
    }

    fun isOwnerO(player: OfflinePlayer): Boolean {
        return owner == player
    }

    fun isOwner(sender: CommandSender): Boolean {
        return owner == sender
    }

    fun hasInvite(sender: CommandSender): Boolean {
        if (sender is OfflinePlayer) {
            return invites.contains(sender)
        }

        return false
    }

    fun isModerator(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return moderators.contains(player)
        }

        return false
    }

    fun isModeratorO(player: OfflinePlayer): Boolean {
        return moderators.contains(player)
    }

    fun invite(player: OfflinePlayer) {
        if (members.contains(player)) {
            return
        }

        if (moderators.contains(player)) {
            return
        }

        if (owner == player) {
            return
        }

        if (bannedPlayers.contains(player)) {
            return
        }

        if (invites.contains(player)) {
            return
        }

        invites.add(player)
    }

    fun ban(player: OfflinePlayer) {
        if (!members.contains(player)) {
            return
        }

        if (owner == player) {
            return
        }

        if (bannedPlayers.contains(player)) {
            return
        }

        bannedPlayers.add(player)
        this.leave(player)
    }

    fun unban(player: OfflinePlayer) {
        if (!bannedPlayers.contains(player)) {
            return
        }

        bannedPlayers.remove(player)
    }

    val onlinePlayers: ObjectSet<Player>
        get() {
            val players: ObjectSet<Player> = ObjectArraySet()

            players.addAll(
                members.stream().filter { obj: OfflinePlayer -> obj.isOnline }
                    .map<Player> { obj: OfflinePlayer -> obj.player }
                    .collect<ObjectArraySet<Player>, Any>(
                        java.util.stream.Collectors.toCollection<Player, ObjectArraySet<Player>>(
                            java.util.function.Supplier<ObjectArraySet<Player>> { ObjectArraySet() })
                    )
            )
            players.addAll(
                moderators.stream().filter { obj: OfflinePlayer -> obj.isOnline }
                    .map<Player> { obj: OfflinePlayer -> obj.player }
                    .collect<ObjectArraySet<Player>, Any>(
                        java.util.stream.Collectors.toCollection<Player, ObjectArraySet<Player>>(
                            java.util.function.Supplier<ObjectArraySet<Player>> { ObjectArraySet() })
                    )
            )

            if (this.getOwner().isOnline()) {
                players.add(this.getOwner().getPlayer())
            }

            return players
        }

    fun promote(player: OfflinePlayer) {
        if (!members.contains(player)) {
            return
        }

        if (owner == player) {
            return
        }

        if (moderators.contains(player)) {
            return
        }

        members.remove(player)
        moderators.add(player)
    }

    fun demote(player: OfflinePlayer) {
        if (!moderators.contains(player)) {
            return
        }

        moderators.remove(player)
    }

    fun kick(player: OfflinePlayer) {
        if (!members.contains(player)) {
            return
        }

        if (owner == player) {
            return
        }

        this.leave(player)
    }

    fun acceptInvite(player: OfflinePlayer) {
        if (!invites.contains(player)) {
            return
        }

        val channel = getChannelO(player)

        channel?.leave(player)

        this.join(player)
    }

    fun revokeInvite(player: OfflinePlayer) {
        if (!invites.contains(player)) {
            return
        }

        invites.remove(player)
    }

    fun move(player: OfflinePlayer, channel: Channel?) {
        this.leave(player)


        if (channel == null) {
            return
        }

        channel.join(player)
    }

    fun join(player: OfflinePlayer) {
        if (members.contains(player)) {
            return
        }

        if (moderators.contains(player)) {
            return
        }

        if (owner == player) {
            return
        }

        if (invites.contains(player)) {
            invites.remove(player)
        }

        if (getChannelO(player) != null) {
            return
        }

        members.add(player)

        this.message(
            MessageBuilder().primary(player.name!!)
                .success(" ist dem Nachrichtenkanal beigetreten.")
        )
    }

    fun leave(player: OfflinePlayer) {
        if (owner == player) {
            this.delete()
            return
        }

        moderators.remove(player)
        members.remove(player)

        this.message(
            MessageBuilder().primary(player.name!!).error(" hat den Nachrichtenkanal verlassen.")
        )
    }

    fun delete(): Boolean {
        this.message(
            MessageBuilder().primary("Der Nachrichtenkanal ").info(
                this.getName()
            ).error(" wurde gelöscht.")
        )

        val uuid: java.util.UUID = this.getOwner().getUniqueId()

        members.clear()
        moderators.clear()

        return this.unregister(uuid)
    }

    private fun message(messageBuilder: MessageBuilder) {
        onlinePlayers.forEach(java.util.function.Consumer<Player> { player: Player ->
            SurfChat.Companion.send(
                player,
                messageBuilder
            )
        })
    }

    fun register() {
        ChannelProvider.getInstance().channels.put(owner!!.uniqueId, this)
    }

    fun unregister(uuid: java.util.UUID?): Boolean {
        ChannelProvider.getInstance().channels.remove(uuid)

        return !ChannelProvider.getInstance().channels.containsKey(uuid)
    }

    companion object {
        fun getChannel(name: String): Channel? {
            return ChannelProvider.getInstance().channels.values.stream()
                .filter { channel: Channel -> channel.getName() == name }.findFirst().orElse(null)
        }

        fun getChannel(sender: CommandSender): Channel? {
            return ChannelProvider.getInstance().channels.values.stream()
                .filter { channel: Channel ->
                    channel.isModerator(sender) || channel.isMember(sender) || channel.isOwner(
                        sender
                    )
                }.findFirst().orElse(null)
        }

        fun getChannelO(player: OfflinePlayer): Channel? {
            return ChannelProvider.getInstance().channels.values.stream()
                .filter { channel: Channel ->
                    channel.getModerators().contains(player) || channel.getMembers()
                        .contains(player) || channel.getOwner() == player
                }.findFirst().orElse(null)
        }
    }
}