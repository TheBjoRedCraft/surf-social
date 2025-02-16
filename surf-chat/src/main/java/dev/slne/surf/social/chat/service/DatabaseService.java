package dev.slne.surf.social.chat.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.ChatUser;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseService {
  @Getter
  private static final DatabaseService instance = new DatabaseService();
  private final FileConfiguration config = SurfChat.getInstance().getConfig();
  private final ComponentLogger logger = ComponentLogger.logger(DatabaseService.class);

  private HikariDataSource dataSource;

  public void connect() {
    HikariConfig config = new HikariConfig();

    config.setJdbcUrl("jdbc:mysql://" + this.config.getString("database.host") + ":" + this.config.getInt("database.port") + "/" + this.config.getString("database.database"));
    config.setUsername(this.config.getString("database.username"));
    config.setPassword(this.config.getString("database.password"));
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    dataSource = new HikariDataSource(config);

    this.createTable();
  }

  public void disconnect() {
    if (dataSource != null) {
      dataSource.close();
    }
  }

  private void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS surf_chat_user (" +
        "uuid VARCHAR(36) PRIMARY KEY," +
        "pm_enabled BOOLEAN NOT NULL," +
        "ignore_list TEXT" +
        ");";

    try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      this.logger.error("Failed to create table", e);
    }
  }

  public ChatUser loadUser(UUID uuid) {
    String sql = "SELECT uuid, pm_enabled, ignore_list FROM surf_chat_user WHERE uuid = ?";

    try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, uuid.toString());

      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        ObjectSet<UUID> ignoreList = new ObjectOpenHashSet<>();
        String ignoreListStr = rs.getString("ignore_list");

        if (ignoreListStr != null && !ignoreListStr.isEmpty()) {
          for (String id : ignoreListStr.split(",")) {
            ignoreList.add(UUID.fromString(id));
          }
        }

        return ChatUser.builder()
            .uuid(UUID.fromString(rs.getString("uuid")))
            .toggledPM(rs.getBoolean("pm_enabled"))
            .ignoreList(ignoreList)
            .build();
      }
    } catch (SQLException e) {
      this.logger.error("Failed to load user", e);
    }

    return this.createUser(uuid);
  }

  public void saveUser(ChatUser user) {
    String sql = "INSERT INTO surf_chat_user (uuid, pm_enabled, ignore_list) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE pm_enabled = ?, ignore_list = ?";

    try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, user.getUuid().toString());
      pstmt.setBoolean(2, user.isToggledPM());

      String ignoreListStr = String.join(",", user.getIgnoreList().stream().map(UUID::toString).toArray(String[]::new));

      pstmt.setString(3, ignoreListStr);
      pstmt.setBoolean(4, user.isToggledPM());
      pstmt.setString(5, ignoreListStr);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      this.logger.error("Failed to save user", e);
    }
  }

  public ChatUser createUser(UUID uuid) {
    return ChatUser.builder()
        .uuid(uuid)
        .toggledPM(false)
        .ignoreList(new ObjectOpenHashSet<>())
        .build();
  }
}