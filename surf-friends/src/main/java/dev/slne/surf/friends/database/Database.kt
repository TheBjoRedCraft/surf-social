package dev.slne.surf.friends.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.friends.FriendData
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.config.PluginConfig
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

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
            dataSource!!.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute(table)
                }
            }
        } catch (e: SQLException) {
            Bukkit.getConsoleSender().sendMessage(
                SurfFriendsPlugin.prefix.append(
                    Component.text(
                        e.message!!
                    )
                )
            )
        }
    }

    fun getFriendData(player: UUID): CompletableFuture<FriendData?> {
        return CompletableFuture.supplyAsync<FriendData?> {
            val query = "SELECT * FROM surffriends WHERE uuid = ?"
            try {
                dataSource!!.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, player.toString())
                        statement.executeQuery().use { resultSet ->
                            if (resultSet.next()) {
                                val friends: String = resultSet.getString("friends")
                                val friendRequests: String =
                                    resultSet.getString("friendrequests")
                                val allowRequests: Boolean =
                                    resultSet.getBoolean("allowrequests")

                                val friendsList: ObjectList<UUID> =
                                    if (friends.isEmpty())
                                        ObjectArrayList()
                                    else
                                        ObjectArrayList(
                                            Arrays.stream(
                                                friends.split(",".toRegex())
                                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                            )
                                                .map { name: String? ->
                                                    UUID.fromString(
                                                        name
                                                    )
                                                }
                                                .collect(
                                                    Collectors.toList<UUID>()
                                                ))

                                val friendRequestsList: ObjectList<UUID> =
                                    if (friendRequests.isEmpty())
                                        ObjectArrayList()
                                    else
                                        ObjectArrayList(
                                            Arrays.stream(
                                                friendRequests.split(",".toRegex())
                                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                            )
                                                .map { name: String? ->
                                                    UUID.fromString(
                                                        name
                                                    )
                                                }
                                                .collect(
                                                    Collectors.toList()
                                                ))

                                return@supplyAsync FriendData(player, friendsList, friendRequestsList, allowRequests);
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Bukkit.getConsoleSender().sendMessage(
                    SurfFriendsPlugin.prefix.append(
                        Component.text(
                            e.message!!
                        )
                    )
                )
            }
            null
        }
    }

    fun saveFriendData(friendData: FriendData): CompletableFuture<Void?> {
        return CompletableFuture.runAsync {
            val query: String = """
          INSERT INTO surffriends (uuid, friends, friendrequests, allowrequests)
          VALUES (?, ?, ?, ?)
          ON DUPLICATE KEY UPDATE
          friends = VALUES(friends),
          friendrequests = VALUES(friendrequests),
          allowrequests = VALUES(allowrequests);
          
          """.trimIndent()
            val friends = friendData.friends?.stream()
              ?.map { obj: UUID -> obj.toString() }
                ?.collect(Collectors.joining(","))
            val friendRequests = friendData.friendRequests?.stream()
              ?.map { obj: UUID -> obj.toString() }
                ?.collect(Collectors.joining(","))
            try {
                dataSource!!.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, friendData.player.toString())
                        statement.setString(2, friends)
                        statement.setString(3, friendRequests)
                      friendData.allowRequests?.let { statement.setBoolean(4, it) }
                        statement.executeUpdate()
                    }
                }
            } catch (e: SQLException) {
                Bukkit.getConsoleSender().sendMessage(
                    SurfFriendsPlugin.prefix.append(
                        Component.text(
                            e.message!!
                        )
                    )
                )
            }
        }
    }

    fun closeConnection() {
        if (dataSource != null && !dataSource!!.isClosed) {
            dataSource!!.close()
        }
    }
}
