@file:OptIn(DelicateCoroutinesApi::class)

package dev.slne.surf.friends

import dev.slne.surf.friends.listener.util.PluginColor
import com.github.benmanes.caffeine.cache.Caffeine
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.friends.database.Database
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class FriendManager {
    val cache: CoroutineLoadingCache<UUID, FriendData> =
        Caffeine.newBuilder().buildCoroutine() { player: UUID ->
            loadFriendData(player)
        }

    /* Friend Management */
    suspend fun addFriend(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        if (playerData.friends.contains(target) || targetData.friends.contains(player)) {
            return
        }

        playerData.friends.add(target)
        cache.put(player, playerData)

        targetData.friends.add(player)
        cache.put(target, targetData)

        sendMessage(
            player, Component.text("Du bist nun mit ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" befreundet."))
        )
        sendMessage(
            target, Component.text("Du bist nun mit ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" befreundet."))
        )
    }

    suspend fun removeFriend(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        if (!playerData.friends.contains(target)) {
            return
        }

        if (!targetData.friends.contains(player)) {

            return
        }

        playerData.friends.remove(target)
        cache.put(player, playerData)

        targetData.friends.remove(player)
        cache.put(target, targetData)

        sendMessage(
            player, Component.text("Du hast ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" als Freund entfernt."))
        )
        sendMessage(
            target, Component.text("Du wurdest von ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" als Freund entfernt."))
        )
    }

    suspend fun sendFriendRequest(player: UUID, target: UUID) {
        val targetData = queryFriendData(target)

        if (targetData.friendRequests.contains(player)) {
            return
        }

        targetData.friendRequests.add(player)
        cache.put(target, targetData)

        sendMessage(
            player, Component.text("Du hast eine Freundschaftsanfrage an ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" gesendet."))
        )

        if (targetData.allowRequests) {
            sendMessage(
                target, Component.text("Du hast eine Freundschaftsanfrage von ")
                    .append(Component.text(getName(player), PluginColor.GOLD))
                    .append(Component.text(" erhalten."))
            )
        }
    }

    suspend fun acceptFriendRequest(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        if (!playerData.friendRequests.contains(target)) {
            return
        }

        playerData.friendRequests.remove(target)
        cache.put(player, playerData)

        addFriend(player, target)

        sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" akzeptiert."))
        )
        sendMessage(
            target, Component.text("Die Freundschaftsanfrage an ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde akzeptiert."))
        )
    }

    suspend fun denyFriendRequest(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)

        playerData.friendRequests.remove(target)
        cache.put(player, playerData)

        sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(getName(target), PluginColor.GOLD))
                .append(Component.text(" abgelehnt."))
        )
        sendMessage(
            target, Component.text("Die Freundschaftsanfrage an ")
                .append(Component.text(getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde abgelehnt."))
        )
    }

    suspend fun hasFriendRequest(player: UUID, target: UUID): Boolean {
        return queryFriendData(player).friendRequests.contains(target)
    }

    suspend fun getFriendRequests(player: UUID): ObjectList<UUID> {
        return queryFriendData(player).friendRequests
    }

    suspend fun toggle(player: UUID): Boolean {
        val playerData = queryFriendData(player)

        playerData.allowRequests = !(playerData.allowRequests)
        cache.put(player, playerData)

        return playerData.allowRequests
    }

    suspend fun isAllowingRequests(player: UUID): Boolean {
        return queryFriendData(player).allowRequests
    }

    suspend fun queryFriendData(player: UUID): FriendData {
        return cache.get(player) ?: newFriendData(player)
    }

    suspend fun saveFriendData(player: UUID) {
        val friendData = queryFriendData(player)
        Database.saveFriendData(friendData)
    }

    suspend fun saveAll(closeConnection: Boolean) {
        cache.synchronous().asMap().map { player -> saveFriendData(player.key) }

        if (closeConnection) {
            withContext(Dispatchers.IO) {
                Database.closeConnection()
            }
        }
    }

    private fun sendMessage(uuid: UUID, message: Component) {
        val player = Bukkit.getPlayer(uuid)
        player?.sendMessage(SurfFriendsPlugin.prefix.append(message))
    }

    private fun getName(uuid: UUID): String {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        return offlinePlayer.name?: "Unbekannt"
    }

    suspend fun getOnlineFriends(player: UUID): ObjectList<Player> {
        // TODO: Cloud implementation
        return ObjectArrayList()
    }

    suspend fun getServer(player: UUID): String {
        // TODO: Cloud implementation
        return ""
    }

    suspend fun getFriends(player: UUID): ObjectList<UUID> {
        return queryFriendData(player).friends
    }

    suspend fun areFriends(player: UUID, target: UUID): Boolean {
        return queryFriendData(player).friends.contains(target)
    }

    companion object {
        val instance: FriendManager = FriendManager()

        suspend fun loadFriendData(player: UUID): FriendData {
            return Database.getFriendData(player)
        }


        fun newFriendData(player: UUID): FriendData { return FriendData(player, ObjectArrayList(), ObjectArrayList(), true) }
    }
}
