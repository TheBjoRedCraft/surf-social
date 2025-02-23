package dev.slne.surf.social.friend.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.social.friend.SurfFriends
import dev.slne.surf.social.friend.player.FriendPlayer
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.util.*

class DatabaseService {
    private var dataSource: HikariDataSource? = null
    private val logger = ComponentLogger.logger(DatabaseService::class.java)
    private val config: ConfigService = SurfFriends.instance.configService

    private val table: String = "surf_friends"

    fun connect() {
        val config = HikariConfig()
        config.jdbcUrl = this.config.getValue("database.url")
        config.username = this.config.getValue("database.username")
        config.password = this.config.getValue("database.password")

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.maximumPoolSize = 10
        config.maxLifetime = 600000

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            logger.error("MySQL JDBC Driver not found", e)
        }

        dataSource = HikariDataSource(config)
    }

    suspend fun loadPlayer(uuid: UUID): FriendPlayer = withContext(Dispatchers.IO) {
        val query = "SELECT uuid, friends, friendRequests, wouldLikeToBeNotified FROM $table WHERE uuid = ?"

        try {
            val dataSource = dataSource ?: return@withContext FriendPlayer(uuid)

            dataSource.connection.use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, uuid.toString())
                    val resultSet = statement.executeQuery()

                    if (resultSet.next()) {
                        val friends = parseList(resultSet.getString("friends"))
                        val friendRequests = parseList(resultSet.getString("friendRequests"))
                        val wouldLikeToBeNotified = resultSet.getBoolean("wouldLikeToBeNotified")

                        return@withContext FriendPlayer(uuid, friends, friendRequests, wouldLikeToBeNotified)
                    } else {
                        return@withContext FriendPlayer(uuid)
                    }
                }
            }
        } catch (e: SQLException) {
            logger.error("An error occurred while loading player data", e)
        }

        FriendPlayer(uuid)
    }

    suspend fun savePlayer(player: FriendPlayer) = withContext(Dispatchers.IO) {
        val query = "INSERT INTO $table (uuid, friends, friendRequests, wouldLikeToBeNotified) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE friends = VALUES(friends), friendRequests = VALUES(friendRequests), wouldLikeToBeNotified = VALUES(wouldLikeToBeNotified)"

        try {
            val dataSource = this@DatabaseService.dataSource ?: return@withContext

            dataSource.connection.use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, player.uuid.toString())
                    statement.setString(2, java.lang.String.join(",", player.friends.stream().map { obj: UUID -> obj.toString() }.toList()))
                    statement.setString(3, java.lang.String.join(",", player.friendRequests.stream().map { obj: UUID -> obj.toString() }.toList()))
                    statement.setBoolean(4, player.wouldLikeToBeNotified)
                    statement.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            logger.error("An error occurred while saving player data", e)
        }
    }

    fun disconnect() {
        val dataSource = this.dataSource ?: return

        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }

    private fun parseList(data: String?): ObjectOpenHashSet<UUID> {
        val set = ObjectOpenHashSet<UUID>()
        if (!data.isNullOrEmpty()) {
            for (uuidStr in data.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                set.add(UUID.fromString(uuidStr))
            }
        }
        return set
    }
}