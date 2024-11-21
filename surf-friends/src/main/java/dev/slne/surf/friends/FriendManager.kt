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

object FriendManager {
    val cache: CoroutineLoadingCache<UUID, FriendData> = Caffeine.newBuilder().buildCoroutine() { player: UUID -> loadFriendData(player) }

    /* Friend Management */
    suspend fun addFriend(player: UUID, target: UUID) {
        val playerData = queryFriendData(player)
        val targetData = queryFriendData(target)

        if(!playerData.friends.add(target)) {
            return
        }

        if(!targetData.friends.add(player)) {
            return
        }

        cache.put(player, playerData)
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

        if(!playerData.friends.remove(target)) {
            return
        }

        if(!targetData.friends.remove(player)) {
            return
        }

        cache.put(player, playerData)
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

        if(!targetData.friendRequests.add(player)) {
            return
        }

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

        if(!playerData.friendRequests.remove(target)) {
            return
        }
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

    suspend fun hasFriendRequest(player: UUID, target: UUID): Boolean = queryFriendData(player).friendRequests.contains(target)

    suspend fun getFriendRequests(player: UUID): ObjectList<UUID> = queryFriendData(player).friendRequests

    suspend fun toggle(player: UUID): Boolean {
        val playerData = queryFriendData(player)

        playerData.allowRequests = !(playerData.allowRequests)
        cache.put(player, playerData)

        return playerData.allowRequests
    }

    suspend fun isAllowingRequests(player: UUID): Boolean = queryFriendData(player).allowRequests

    suspend fun queryFriendData(player: UUID): FriendData = cache.get(player) ?: newFriendData(player)

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
        val player = Bukkit.getPlayer(uuid) ?: return

        player.sendMessage(SurfFriendsPlugin.prefix.append(message))
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

    suspend fun getFriends(player: UUID): ObjectList<UUID> = queryFriendData(player).friends

    suspend fun areFriends(player: UUID, target: UUID): Boolean = queryFriendData(player).friends.contains(target)

    suspend fun loadFriendData(player: UUID): FriendData = Database.getFriendData(player)


    fun newFriendData(player: UUID): FriendData = FriendData(player)
}
