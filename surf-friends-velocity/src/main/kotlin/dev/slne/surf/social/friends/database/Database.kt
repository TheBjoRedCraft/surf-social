package dev.slne.surf.social.friends.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.social.friends.SurfFriends
import dev.slne.surf.social.friends.manager.FriendData
import dev.slne.surf.social.friends.manager.FriendManager
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

    suspend fun getFriendData(player: UUID): FriendData {
        val query = "SELECT * FROM surffriends WHERE uuid = ?"

        return withContext(Dispatchers.IO) {
            try {
                val source = dataSource;

                source.connection.use { connection ->
                    connection.prepareStatement(query).use { statement ->
                        statement.setString(1, player.toString())
                        statement.executeQuery().use { resultSet ->
                            if (resultSet.next()) {
                                val friends: String = resultSet.getString("friends")
                                val friendRequests: String = resultSet.getString("friendrequests")
                                val allowRequests: Boolean = resultSet.getBoolean("allowrequests")

                                val friendsList = if (friends.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friends.split(",")
                                        .map { UUID.fromString(it.trim()) })
                                }

                                val friendRequestsList = if (friendRequests.isEmpty()) {
                                    ObjectArraySet()
                                } else {
                                    ObjectArraySet(friendRequests.split(",")
                                        .map { UUID.fromString(it.trim()) })
                                }

                                return@withContext FriendData(
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
                val source = dataSource;

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
