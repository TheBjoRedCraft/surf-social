package dev.slne.surf.social.chat.service

import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.configuration.file.FileConfiguration

class DatabaseService {
    private val config: FileConfiguration = SurfChat.Companion.getInstance().getConfig()
    private val logger: ComponentLogger = ComponentLogger.logger(
        DatabaseService::class.java
    )

    private var dataSource: HikariDataSource? = null

    fun connect() {
        val config: HikariConfig = HikariConfig()

        config.setJdbcUrl(
            "jdbc:mysql://" + this.config.getString("database.host") + ":" + this.config.getInt(
                "database.port"
            ) + "/" + this.config.getString("database.database")
        )
        config.setUsername(this.config.getString("database.username"))
        config.setPassword(this.config.getString("database.password"))
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        dataSource = HikariDataSource(config)

        this.createTable()
    }

    fun disconnect() {
        if (dataSource != null) {
            dataSource.close()
        }
    }

    private fun createTable() {
        val sql = "CREATE TABLE IF NOT EXISTS surf_chat_user (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "pm_enabled BOOLEAN NOT NULL," +
                "ignore_list TEXT" +
                ");"

        try {
            dataSource.getConnection().use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute(sql)
                }
            }
        } catch (e: java.sql.SQLException) {
            logger.error("Failed to create table", e)
        }
    }

    fun loadUser(uuid: java.util.UUID): ChatUser {
        val sql = "SELECT uuid, pm_enabled, ignore_list FROM surf_chat_user WHERE uuid = ?"

        try {
            dataSource.getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, uuid.toString())
                    val rs: java.sql.ResultSet = pstmt.executeQuery()
                    if (rs.next()) {
                        val ignoreList: ObjectSet<java.util.UUID> =
                            ObjectOpenHashSet<java.util.UUID>()
                        val ignoreListStr: String = rs.getString("ignore_list")

                        if (ignoreListStr != null && !ignoreListStr.isEmpty()) {
                            for (id in ignoreListStr.split(",".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                                ignoreList.add(java.util.UUID.fromString(id))
                            }
                        }

                        return ChatUser.builder()
                            .uuid(java.util.UUID.fromString(rs.getString("uuid")))
                            .toggledPM(rs.getBoolean("pm_enabled"))
                            .ignoreList(ignoreList)
                            .build()
                    }
                }
            }
        } catch (e: java.sql.SQLException) {
            logger.error("Failed to load user", e)
        }

        return this.createUser(uuid)
    }

    fun saveUser(user: ChatUser) {
        val sql =
            "INSERT INTO surf_chat_user (uuid, pm_enabled, ignore_list) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE pm_enabled = ?, ignore_list = ?"

        try {
            dataSource.getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, user.getUuid().toString())
                    pstmt.setBoolean(2, user.isToggledPM())

                    val ignoreListStr: String = java.lang.String.join(
                        ",",
                        *user.getIgnoreList().stream()
                            .map<String> { obj: java.util.UUID -> obj.toString() }
                            .toArray<String> { _Dummy_.__Array__() })

                    pstmt.setString(3, ignoreListStr)
                    pstmt.setBoolean(4, user.isToggledPM())
                    pstmt.setString(5, ignoreListStr)
                    pstmt.executeUpdate()
                }
            }
        } catch (e: java.sql.SQLException) {
            logger.error("Failed to save user", e)
        }
    }

    fun createUser(uuid: java.util.UUID?): ChatUser {
        return ChatUser.builder()
            .uuid(uuid)
            .toggledPM(false)
            .ignoreList(ObjectOpenHashSet<java.util.UUID>())
            .build()
    }

    companion object {
        @Getter
        private val instance = DatabaseService()
    }
}