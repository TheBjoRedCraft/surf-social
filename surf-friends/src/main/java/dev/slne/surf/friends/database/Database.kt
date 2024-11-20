package dev.slne.surf.friends.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.friends.FriendData
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.config.PluginConfig
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.sql.SQLException
import java.util.*
import javax.xml.crypto.Data


object Database {
    private var dataSource: HikariDataSource? = null

    fun createConnection() {
        val config = HikariConfig()
        config.jdbcUrl = PluginConfig.config().getString("mysql.url")
        config.username = PluginConfig.config().getString("mysql.user")
        config.password = PluginConfig.config().getString("mysql.password")

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.maximumPoolSize = 10
        config.maxLifetime = 600000

        dataSource = HikariDataSource(config)
        createTable()
    }

    private fun createTable() {
        val table: String = """
            CREATE TABLE IF NOT EXISTS surffriends (
            uuid VARCHAR(36) PRIMARY KEY,
            friends TEXT,
            friendrequests TEXT,
            allowrequests boolean
            );
            
            """.trimIndent()

        try {
            if(dataSource == null) {
                return
            }

            val source = dataSource ?: return;

            source.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute(table)
                }
            }
        } catch (e: SQLException) {
            Bukkit.getConsoleSender().sendMessage(
                SurfFriendsPlugin.prefix.append(
                    Component.text(
                        e.message ?: "Unknown (SQL Exeption in Database: createTable)"
                    )
                )
            )
        }
    }

    suspend fun getFriendData(player: UUID): FriendData {
        val query = "SELECT * FROM surffriends WHERE uuid = ?"

        return withContext(Dispatchers.IO) {
            try {
                if(dataSource == null) {
                    return@withContext FriendManager.newFriendData(player)
                }

                val source = dataSource ?: return@withContext FriendManager.newFriendData(player);

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, player.toString())
                        statement.executeQuery().use { resultSet ->
                            if (resultSet.next()) {
                                val friends: String = resultSet.getString("friends")
                                val friendRequests: String = resultSet.getString("friendrequests")
                                val allowRequests: Boolean = resultSet.getBoolean("allowrequests")

                                val friendsList = if (friends.isEmpty()) {
                                    ObjectArrayList()
                                } else {
                                    ObjectArrayList(friends.split(",")
                                        .map { UUID.fromString(it.trim()) })
                                }

                                val friendRequestsList = if (friendRequests.isEmpty()) {
                                    ObjectArrayList()
                                } else {
                                    ObjectArrayList(friendRequests.split(",")
                                        .map { UUID.fromString(it.trim()) })
                                }

                                return@withContext FriendData(player, friendsList, friendRequestsList, allowRequests)
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Bukkit.getConsoleSender().sendMessage(
                    SurfFriendsPlugin.prefix.append(
                        Component.text(e.message ?: "Unknown SQL error")
                    )
                )
            }

            return@withContext FriendManager.newFriendData(player)
        }
    }

    suspend fun saveFriendData(friendData: FriendData) {
        val query = """
        INSERT INTO surffriends (uuid, friends, friendrequests, allowrequests)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
        friends = VALUES(friends),
        friendrequests = VALUES(friendrequests),
        allowrequests = VALUES(allowrequests);
    """.trimIndent()

        val friends = friendData.friends.joinToString(",") { it.toString() }
        val friendRequests = friendData.friendRequests.joinToString(",") { it.toString() }

        withContext(Dispatchers.IO) {
            try {
                if(dataSource == null) {
                    return@withContext FriendManager.newFriendData(friendData.player)
                }

                val source = dataSource ?: return@withContext FriendManager.newFriendData(friendData.player);

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, friendData.player.toString())
                        statement.setString(2, friends)
                        statement.setString(3, friendRequests)
                        statement.setBoolean(4, friendData.allowRequests)
                        statement.executeUpdate()
                    }
                }
            } catch (e: SQLException) {
                Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.prefix.append(Component.text(e.message ?: "Unknown SQL error"))
                )
            }
        }
    }


    fun closeConnection() {
        if (dataSource != null) {
            val source = dataSource ?: return

            if (!source.isClosed) {
                source.close()
            }
        }
    }
}
