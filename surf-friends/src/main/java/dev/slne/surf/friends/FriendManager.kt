package dev.slne.surf.friends

import dev.slne.surf.friends.listener.util.PluginColor

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import dev.slne.surf.friends.database.Database
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import lombok.Getter
import lombok.experimental.Accessors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

@Getter
@Accessors(fluent = true)
class FriendManager {
    val cache: LoadingCache<UUID, FriendData> =
        Caffeine.newBuilder().build { player: UUID ->
            loadFriendData(player)
        }

    /* Friend Management */
    fun addFriend(player: UUID, target: UUID) {
        val playerData = this.queryFriendData(player)
        val targetData = this.queryFriendData(target)

        if (playerData.friends?.contains(target) == true) {
            return
        }

        if (targetData.friends?.contains(player) == true) {
            return
        }

        playerData.friends?.add(target)
        cache.put(player, playerData)

        targetData.friends?.add(player)
        cache.put(target, targetData)

        this.sendMessage(
            player, Component.text("Du bist nun mit ")
                .append(Component.text(this.getName(target), PluginColor.GOLD))
                .append(Component.text(" befreundet."))
        )
        this.sendMessage(
            target, Component.text("Du bist nun mit ")
                .append(Component.text(this.getName(player), PluginColor.GOLD))
                .append(Component.text(" befreundet."))
        )
    }

    fun removeFriend(player: UUID, target: UUID) {
        val playerData = this.queryFriendData(player)
        val targetData = this.queryFriendData(target)

        if (!playerData.friends?.contains(target)!!) {
            return
        }

        if (!targetData.friends?.contains(player)!!) {
            return
        }


        playerData.friends?.remove(target)
        cache.put(player, playerData)

        targetData.friends?.remove(player)
        cache.put(target, targetData)

        this.sendMessage(
            player, Component.text("Du hast ")
                .append(Component.text(this.getName(target), PluginColor.GOLD))
                .append(Component.text(" als Freund entfernt."))
        )
        this.sendMessage(
            target, Component.text("Du wurdest von ")
                .append(Component.text(this.getName(player), PluginColor.GOLD))
                .append(Component.text(" als Freund entfernt."))
        )
    }

    fun sendFriendRequest(player: UUID, target: UUID) {
        val targetData = this.queryFriendData(target)

        if (targetData.friendRequests?.contains(player) == true) {
            return
        }

        targetData.friendRequests?.add(player)
        cache.put(target, targetData)

        this.sendMessage(
            player, Component.text("Du hast eine Freundschaftsanfrage an ")
                .append(Component.text(this.getName(target), PluginColor.GOLD))
                .append(Component.text(" gesendet."))
        )

        if (targetData.allowRequests == true) {
            this.sendMessage(
                target, Component.text("Du hast eine Freundschaftsanfrage von ")
                    .append(Component.text(this.getName(player), PluginColor.GOLD))
                    .append(Component.text(" erhalten."))
            )
        }
    }

    fun acceptFriendRequests(player: UUID, target: UUID) {
        val playerData = this.queryFriendData(player)
        if (!playerData.friendRequests?.contains(target)!!) {
            return
        }

        playerData.friendRequests?.remove(target)
        cache.put(player, playerData)

        this.addFriend(player, target)

        this.sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(this.getName(target), PluginColor.GOLD))
                .append(Component.text(" akzeptiert."))
        )
        this.sendMessage(
            target, Component.text("Die Freundschaftsanfrage an ")
                .append(Component.text(this.getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde akzeptiert."))
        )
    }

    fun denyFriendRequest(player: UUID, target: UUID) {
        val playerData = this.queryFriendData(player)

        playerData.friendRequests?.remove(target)
        cache.put(player, playerData)

        this.sendMessage(
            player, Component.text("Du hast die Freundschaftsanfrage von ")
                .append(Component.text(this.getName(target), PluginColor.GOLD))
                .append(Component.text(" abgelehnt."))
        )
        this.sendMessage(
            target, Component.text("Die Freundschaftsanfrage an ")
                .append(Component.text(this.getName(player), PluginColor.GOLD))
                .append(Component.text(" wurde abgelehnt."))
        )
    }

    fun hasFriendRequest(player: UUID, target: UUID?): Boolean {
        return queryFriendData(player).friendRequests?.contains(target) == true
    }

    fun getFriendRequests(player: UUID): ObjectList<UUID>? {
        return queryFriendData(player).friendRequests
    }

    fun toggle(player: UUID): Boolean? {
        val playerData = this.queryFriendData(player)

        playerData.allowRequests = !playerData.allowRequests!!
        cache.put(player, playerData)

        return playerData.allowRequests
    }

    fun isAllowingRequests(player: UUID): Boolean? {
        return queryFriendData(player).allowRequests
    }

    fun queryFriendData(player: UUID): FriendData {
        if (cache[player] == null) {
            return newFriendData(player)
        }

        return cache[player]
    }

    fun saveFriendData(player: UUID): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val friendData = this.queryFriendData(player)
            Database.saveFriendData(friendData).join()
            cache.invalidate(player)
        }
    }

    fun saveAll(closeConnection: Boolean): CompletableFuture<Void> {
        val futures: ObjectList<CompletableFuture<Void>> = ObjectArrayList()

        for (player in cache.asMap().keys) {
            val future = this.saveFriendData(player)
            futures.add(future)
        }

        val allSaves = CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>>())

        return if (closeConnection) {
            allSaves.thenCompose { v: Void? ->
                CompletableFuture.runAsync {
                    Database.closeConnection()
                }
            }
        } else {
            allSaves
        }
    }


    fun sendPlayer(player: UUID?, target: UUID?) {
        // TODO: Send to Server
    }

    private fun sendMessage(uuid: UUID, message: Component) {
        val player = Bukkit.getPlayer(uuid)
        player?.sendMessage(SurfFriendsPlugin.prefix.append(message))
    }

    private fun getName(uuid: UUID): String {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)

        return if (offlinePlayer.name == null) "Unbekannt" else offlinePlayer.name!!
    }

    fun getOnlinefriends(player: UUID?): ObjectList<Player>? {
        //TODO: Cloud implementation

        return null
    }

    fun getServer(player: UUID?): String {
        //TODO: Cloud implementation

        return "???"
    }

    fun getFriends(player: UUID): ObjectList<UUID>? {
        return queryFriendData(player).friends;
    }

    fun areFriends(player: UUID, target: UUID): Boolean? {
        return queryFriendData(player).friends!!.contains(target)
    }

    companion object {
        @Getter
        val instance: FriendManager = FriendManager()
        fun loadFriendData(player: UUID): FriendData? {
            Database.getFriendData(player).thenApply { friendData: FriendData? ->
                if (friendData == null) {
                    return@thenApply newFriendData(player)
                }
                friendData
            }
            return null
        }

        fun loadFriendDataAsync(player: UUID): CompletableFuture<FriendData?> {
            return Database.getFriendData(player).thenApply { friendData: FriendData? ->
                Objects.requireNonNullElseGet(
                    friendData
                ) { newFriendData(player) }
            }
        }


        fun newFriendData(player: UUID?): FriendData {
            return FriendData(player!!, ObjectArrayList(), ObjectArrayList(), true)
        }
    }
}
