package dev.slne.surf.social.friends.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import dev.slne.surf.social.friends.SurfFriends
import dev.slne.surf.social.friends.player.FriendPlayer

import it.unimi.dsi.fastutil.objects.ObjectArraySet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.gradle.internal.cc.base.logger

import java.sql.SQLException
import java.util.*


object Database {
    private lateinit var dataSource: HikariDataSource

    fun createConnection() {
        val config = HikariConfig()
        config.jdbcUrl = SurfFriends.instance.node.getString("mysql.url")
        config.username = SurfFriends.instance.node.getString("mysql.user")
        config.password = SurfFriends.instance.node.getString("mysql.password")

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        config.maximumPoolSize = 10
        config.maxLifetime = 600000

        dataSource = HikariDataSource(config)

        createTable()
    }

    fun reconnect() {
        closeConnection()
        createConnection()
    }

    private fun createTable() {
        val table: String = """
            CREATE TABLE IF NOT EXISTS surffriends (
            uuid VARCHAR(36) PRIMARY KEY,
            name TEXT,
            friends TEXT,
            friendrequests TEXT,
            allowrequests boolean
            );
            
            """.trimIndent()

        try {

            val source = dataSource;

            source.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute(table)
                }
            }
        } catch (e: SQLException) {
            SurfFriends.instance.logger.error(e.message)

        }
    }

    suspend fun getPlayer(player: UUID): FriendPlayer? {
        val query = "SELECT * FROM surffriends WHERE uuid = ?"

        return withContext(Dispatchers.IO) {
            try {
                val source = dataSource;

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, player.toString())
                        statement.executeQuery().use { resultSet ->
                            if (resultSet.next()) {
                                val name: String = resultSet.getString("name")
                                val friends: String = resultSet.getString("friends")
                                val friendRequests: String = resultSet.getString("friendrequests")
                                val allowRequests: Boolean = resultSet.getBoolean("allowrequests")

                                val friendsList = if (friends.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friends.split(",").map { UUID.fromString(it.trim()) })
                                }

                                val friendRequestsList = if (friendRequests.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friendRequests.split(",").map { UUID.fromString(it.trim()) })
                                }

                                return@withContext FriendPlayer(
                                    player,
                                    name,
                                    friendsList,
                                    friendRequestsList,
                                    allowRequests
                                )
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                logger.error(e.message)
            }

            return@withContext null
        }
    }

    suspend fun getPlayer(player: String): FriendPlayer? {
        val query = "SELECT * FROM surffriends WHERE name = ?"

        return withContext(Dispatchers.IO) {
            try {
                val source = dataSource;

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(2, player)
                        statement.executeQuery().use { resultSet ->
                            if (resultSet.next()) {
                                val uuid: UUID = UUID.fromString(resultSet.getString("uuid"))
                                val friends: String = resultSet.getString("friends")
                                val friendRequests: String = resultSet.getString("friendrequests")
                                val allowRequests: Boolean = resultSet.getBoolean("allowrequests")

                                val friendsList = if (friends.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friends.split(",").map { UUID.fromString(it.trim()) })
                                }

                                val friendRequestsList = if (friendRequests.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friendRequests.split(",").map { UUID.fromString(it.trim()) })
                                }

                                return@withContext FriendPlayer(
                                    uuid,
                                    player,
                                    friendsList,
                                    friendRequestsList,
                                    allowRequests
                                )
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                logger.error(e.message)
            }

            return@withContext null
        }
    }

    suspend fun saveFriendPlayer(player: FriendPlayer) {
        val query = """
        INSERT INTO surffriends (uuid, name, friends, friendrequests, allowrequests)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
        name = VALUES(name),
        friends = VALUES(friends),
        friendrequests = VALUES(friendrequests),
        allowrequests = VALUES(allowrequests);
    """.trimIndent()

        val friends = player.friends.joinToString(",") { it.toString() }
        val friendRequests = player.friendRequests.joinToString(",") { it.toString() }

        withContext(Dispatchers.IO) {
            try {
                val source = dataSource;

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, player.uuid.toString())
                        statement.setString(2, player.name)
                        statement.setString(3, friends)
                        statement.setString(4, friendRequests)
                        statement.setBoolean(5, player.allowRequests)
                        statement.executeUpdate()
                    }
                }
            } catch (e: SQLException) {
                logger.error(e.message)
            }
        }
    }


    fun closeConnection() {
        val source = dataSource

        if (!source.isClosed) {
            source.close()
        }
    }
}
