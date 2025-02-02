package dev.slne.surf.social.friends.player

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.social.friends.SurfFriends
import dev.slne.surf.social.friends.util.PluginColor
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

data class FriendPlayer (
    val uuid: UUID,
    val name: String,
    val friends: ObjectSet<UUID> = ObjectArraySet(),
    val friendRequests: ObjectSet<UUID> = ObjectArraySet(),
    var allowRequests: Boolean = true
) {
    fun toPlayer(): Player? = SurfFriends.instance.proxy.getPlayer(uuid).getOrNull()

    private suspend fun save() {
        FriendPlayerFactory.savePlayer(this)
    }

    private fun sendMessage(message: Component = Component.text("???")) {
        val player: Player = this.toPlayer() ?: return

        player.sendMessage(message)
    }

    suspend fun addFriend(target: FriendPlayer) {
        if(this.friends.contains(target.uuid)) {
            return
        }

        if(target.friends.contains(this.uuid)) {
            return
        }

        this.friends.add(target.uuid)
        target.friends.add(this.uuid)


        this.sendMessage(Component.text("Du bist nun mit ")
            .append(Component.text(target.name, PluginColor.GOLD))
            .append(Component.text(" befreundet."))
        )

        this.save()
        target.save()
    }

    suspend fun removeFriend(target: FriendPlayer) {
        if(!this.friends.contains(target.uuid)) {
            return
        }

        if(!target.friends.contains(this.uuid)) {
            return
        }

        this.friends.remove(target.uuid)
        target.friends.remove(this.uuid)

        this.sendMessage(Component.text("Du hast ")
                .append(Component.text(target.name, PluginColor.GOLD))
                .append(Component.text(" aus deiner Freundesliste entfernt."))
        )

        this.save()
    }

    suspend fun sendFriendRequest(target: FriendPlayer) {
        if(this.friends.contains(target.uuid)) {
            return
        }

        if(this.friendRequests.contains(target.uuid)) {
            return
        }

        if(target == this) {
            return
        }

        target.friendRequests.add(this.uuid)
        target.save()

        if (target.allowRequests) {
            sendMessage(Component.text("Du hast eine Freundschaftsanfrage von ")
                .append(Component.text(target.name, PluginColor.GOLD))
                .append(Component.text(" erhalten. "))
                .append(Component.text("[Akzeptieren]", PluginColor.LIGHT_GREEN).clickEvent(ClickEvent.runCommand("/friend accept " + target.name)))
                .append(Component.text(" [Ablehnen]", PluginColor.RED).clickEvent(ClickEvent.runCommand("/friend deny " + target.name)))
            )
        }
    }

    suspend fun acceptFriendRequest(requester: FriendPlayer) {
        this.friendRequests.remove(requester.uuid)
        this.save()
        this.addFriend(requester)
        this.sendMessage(Component.text("Du hast die Freundschaftsanfrage von ")
            .append(Component.text(requester.name, PluginColor.GOLD))
            .append(Component.text(" angenommen."))
        )

        requester.friendRequests.remove(this.uuid)
        requester.save()
        requester.addFriend(this)
        requester.sendMessage(Component.text("Deine Freundschaftsanfrage an ")
                .append(Component.text(this.name, PluginColor.GOLD))
                .append(Component.text(" wurde angenommen."))
        )
    }

    suspend fun denyFriendRequest(requester: FriendPlayer) {
        this.friendRequests.remove(requester.uuid)
        this.save()

        this.sendMessage(Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(requester.name, PluginColor.GOLD))
                .append(Component.text(" abgelehnt."))
        )

        requester.sendMessage(Component.text("Deine Freundschaftsanfrage an ")
                .append(Component.text(this.name, PluginColor.GOLD))
                .append(Component.text(" wurde abgelehnt."))
        )
    }

    suspend fun toggleRequests(): Boolean {
        this.allowRequests = !(this.allowRequests)
        this.save()

        return this.allowRequests
    }

    fun getOnlineFriends(): ObjectSet<Player> {
        val players = ObjectArraySet<Player>()

        for (friend in this.getFriends()) {
            val online = SurfFriends.instance.proxy.getPlayer(friend).getOrNull() ?: continue

            players.add(online)
        }

        return players
    }

    fun getServer(): String {
        val player = this.toPlayer() ?: return "???"
        val server = player.currentServer.getOrNull() ?: return "???"

        return server.server.serverInfo.name
    }


    fun getFriendRequests(): ObjectSet<UUID> = this.friendRequests
    fun getFriends(): ObjectSet<UUID> = this.friends
    fun isAllowingRequests(): Boolean = this.allowRequests
    fun hasFriendRequest(target: UUID): Boolean = this.friendRequests.contains(target)
    fun areFriends(target: UUID): Boolean = this.friends.contains(target)
}