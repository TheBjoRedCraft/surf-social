package dev.slne.surf.social.chat.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.configuration.file.FileConfiguration
import java.sql.SQLException
import java.util.*

class DatabaseService {
    private val config: FileConfiguration = SurfChat.instance.config
    private val logger: ComponentLogger = ComponentLogger.logger(DatabaseService::class.java)

    private var dataSource: HikariDataSource? = null

    fun connect() {
        val config = HikariConfig()

        config.jdbcUrl = "jdbc:mysql://" + this.config.getString("database.host") + ":" + this.config.getInt("database.port") + "/" + this.config.getString("database.database")
        config.username = this.config.getString("database.username")
        config.password = this.config.getString("database.password")
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        dataSource = HikariDataSource(config)

        this.createTable()
    }

    fun disconnect() {
        val dataSource: HikariDataSource = this.dataSource ?: return

        dataSource.close()
    }

    private fun createTable() {
        val sql = "CREATE TABLE IF NOT EXISTS surf_chat_user (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "pm_enabled BOOLEAN NOT NULL," +
                "ignore_list TEXT" +
                ");"

        try {
            val dataSource: HikariDataSource = this.dataSource ?: return

            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute(sql)
                }
            }
        } catch (e: SQLException) {
            logger.error("Failed to create table", e)
        }
    }

    suspend fun loadUser(uuid: UUID): ChatUser {
        val sql = "SELECT uuid, pm_enabled, ignore_list FROM surf_chat_user WHERE uuid = ?"

        return withContext(Dispatchers.IO) {
            try {
                val dataSource: HikariDataSource = this@DatabaseService.dataSource ?: return@withContext this@DatabaseService.createUser(uuid)

                dataSource.connection.use { conn ->
                    conn.prepareStatement(sql).use { pstmt ->
                        pstmt.setString(1, uuid.toString())
                        val rs: java.sql.ResultSet = pstmt.executeQuery()
                        if (rs.next()) {
                            val ignoreList: ObjectSet<UUID> = ObjectOpenHashSet()
                            val ignoreListStr: String = rs.getString("ignore_list")

                            if (ignoreListStr.isNotEmpty()) {
                                for (id in ignoreListStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                                    ignoreList.add(UUID.fromString(id))
                                }
                            }

                            return@withContext ChatUser(UUID.fromString(rs.getString("uuid")), rs.getBoolean("pm_enabled"), ignoreList)
                        }
                    }
                }
            } catch (e: SQLException) {
                logger.error("Failed to load user", e)
            }

            return@withContext this@DatabaseService.createUser(uuid)
        }
    }

    suspend fun saveUser(user: ChatUser) {
        val sql = "INSERT INTO surf_chat_user (uuid, pm_enabled, ignore_list) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE pm_enabled = ?, ignore_list = ?"

        withContext(Dispatchers.IO) {
            try {
                val dataSource: HikariDataSource = this@DatabaseService.dataSource ?: return@withContext

                dataSource.connection.use { conn ->
                    conn.prepareStatement(sql).use { pstmt ->
                        pstmt.setString(1, user.uuid.toString())
                        pstmt.setBoolean(2, user.toggledPM)

                        val ignoreListStr: String = java.lang.String.join(",",
                            *user.ignoreList.stream()
                                .map { obj: UUID -> obj.toString() }
                                .toArray { arrayOfNulls<String>(it) })

                        pstmt.setString(3, ignoreListStr)
                        pstmt.setBoolean(4, user.toggledPM)
                        pstmt.setString(5, ignoreListStr)
                        pstmt.executeUpdate()
                    }
                }
            } catch (e: SQLException) {
                logger.error("Failed to save user", e)
            }
        }
    }

    private fun createUser(uuid: UUID): ChatUser = ChatUser(uuid, false, ObjectOpenHashSet())

    companion object {
        val instance = DatabaseService()
    }
}