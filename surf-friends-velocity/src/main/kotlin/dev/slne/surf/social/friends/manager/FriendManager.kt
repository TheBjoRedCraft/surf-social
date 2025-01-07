package dev.slne.surf.social.friends.manager

import com.github.benmanes.caffeine.cache.Caffeine
import com.velocitypowered.api.proxy.Player
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.social.friends.SurfFriends
import dev.slne.surf.social.friends.database.Database
import dev.slne.surf.social.friends.util.PluginColor
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

object FriendManager {
    private val cache: CoroutineLoadingCache<UUID, FriendData> = Caffeine.newBuilder()
        .expireAfterAccess(30, TimeUnit.MINUTES)
        .buildCoroutine {
            player: UUID -> loadFriendData(player)
        }

    suspend fun addFriend(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        if(this.areFriends(player, target)) {
            return
        }

        playerData.friends.add(target)
        targetData.friends.add(player)

        cache.put(player, playerData)
        cache.put(target, targetData)

        sendMessage(player, Component.text("Du bist nun mit ")
            .append(Component.text(getName(target), PluginColor.GOLD))
            .append(Component.text(" befreundet."))
        )

        sendMessage(target, Component.text("Du bist nun mit ")
            .append(Component.text(getName(player), PluginColor.GOLD))
            .append(Component.text(" befreundet."))
        )
    }

    suspend fun removeFriend(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        if (!playerData.friends.remove(target)) {
            return
        }

        if (!targetData.friends.remove(player)) {
            return
        }

        cache.put(player, playerData)
        cache.put(target, targetData)

        sendMessage(
            player, Component.text("Du hast ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" aus deiner Freundesliste entfernt."))
        )

        sendMessage(
            target, Component.text("Du wurdest von ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text("´s Freundesliste entfernt."))
        )
    }

    suspend fun sendFriendRequest(player: UUID, target: UUID) {
        val targetData = queryFriendData(target)

        if (targetData.friends.contains(player)) {
            return
        }

        if(targetData.friendRequests.contains(player)) {
            return
        }

        if(player == target) {
            return
        }

        if (!targetData.friendRequests.add(player)) {
            return
        }

        cache.put(target, targetData)

        sendMessage(player, Component.text("Du hast eine Freundschaftsanfrage an ")
            .append(Component.text(getName(target), PluginColor.GOLD))
            .append(Component.text(" gesendet."))
        )

        if (targetData.allowRequests) {
            sendMessage(target, Component.text("Du hast eine Freundschaftsanfrage von ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" erhalten. "))
                .append(
                    Component.text("[AKZEPTIEREN]", PluginColor.LIGHT_GREEN).clickEvent(
                        ClickEvent.runCommand("/friend accept " + getNameOrUUID(player))))
                .append(Component.text(" [ABLEHNEN]", PluginColor.RED).clickEvent(ClickEvent.runCommand("/friend deny " + getNameOrUUID(player))))
            )
        }
    }

    suspend fun acceptFriendRequest(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        playerData.friendRequests.remove(target)
        targetData.friendRequests.remove(player)

        cache.put(player, playerData)
        cache.put(target, targetData)

        addFriend(player, target)

        sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" akzeptiert."))
        )

        sendMessage(
            target, Component.text("Deine Freundschaftsanfrage an ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde akzeptiert."))
        )
    }

    suspend fun denyFriendRequest(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        playerData.friendRequests.remove(target)
        targetData.friendRequests.remove(player)
        cache.put(player, playerData)
        cache.put(target, targetData)

        sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" abgelehnt."))
        )

        sendMessage(
            target, Component.text("Deine Freundschaftsanfrage an ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde abgelehnt."))
        )
    }

    suspend fun hasFriendRequest(player: UUID, target: UUID): Boolean =
        queryFriendData(player).friendRequests.contains(target)

    suspend fun getFriendRequests(player: UUID): ObjectSet<UUID> =
        queryFriendData(player).friendRequests

    suspend fun toggle(player: UUID): Boolean {
        val playerData = queryFriendData(player)

        playerData.allowRequests = !(playerData.allowRequests)
        cache.put(player, playerData)

        return playerData.allowRequests
    }

    suspend fun isAllowingRequests(player: UUID): Boolean = queryFriendData(player).allowRequests

    private suspend fun queryFriendData(player: UUID): FriendData =
        cache.get(player) ?: newFriendData(player)

    suspend fun saveFriendData(player: UUID) = Database.saveFriendData(queryFriendData(player))

    suspend fun saveAll(closeConnection: Boolean = true) {
        cache.synchronous().asMap().map { player -> saveFriendData(player.key) }

        if (closeConnection) {
            withContext(Dispatchers.IO) {
                Database.closeConnection()
            }
        }
    }

    fun sendMessage(uuid: UUID, message: Component = Component.text("???")) {
        val player: Player = SurfFriends.instance.proxy.getPlayer(uuid).getOrNull() ?: return

        player.sendMessage(message)
    }

    private fun getName(uuid: UUID) = "Unbekannt"
    private fun getNameOrUUID(uuid: UUID) = uuid

    suspend fun getOnlineFriends(player: UUID): ObjectSet<Player> {
        val players = ObjectArraySet<Player>()

        for (friend in this.getFriends(player)) {
            val online: Player = SurfFriends.instance.proxy.getPlayer(friend).getOrNull() ?: continue
            players.add(online)
        }
        
        return players
    }

    fun getServer(uuid: UUID): String {
        val player: Player = SurfFriends.instance.proxy.getPlayer(uuid).getOrNull() ?: return "???"
        val server = player.currentServer.getOrNull() ?: return "???"

        return server.server.serverInfo.name
    }

    suspend fun getFriends(player: UUID): ObjectSet<UUID> = queryFriendData(player).friends

    suspend fun areFriends(player: UUID, target: UUID): Boolean =
        queryFriendData(player).friends.contains(target)

    suspend fun loadFriendData(player: UUID): FriendData = Database.getFriendData(player)

    fun newFriendData(player: UUID): FriendData = FriendData(player)
}