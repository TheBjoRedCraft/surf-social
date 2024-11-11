package dev.slne.surf.friends.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.slne.surf.friends.FriendData;
import dev.slne.surf.friends.SurfFriendsPlugin;

import dev.slne.surf.friends.config.PluginConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.sql.*;
import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;

public class Database {

  private static HikariDataSource dataSource;

  public static void createConnection() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(PluginConfig.config().getString("mysql.url"));
    config.setUsername(PluginConfig.config().getString("mysql.user"));
    config.setPassword(PluginConfig.config().getString("mysql.password"));

    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setMaximumPoolSize(10);
    config.setMaxLifetime(600000);

    dataSource = new HikariDataSource(config);
    createTable();
  }

  private static void createTable() {
    String table = """
            CREATE TABLE IF NOT EXISTS surffriends (
            uuid VARCHAR(36) PRIMARY KEY,
            friends TEXT,
            friendrequests TEXT,
            allowrequests boolean
            );
            """;

    try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
      statement.execute(table);
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
    }
  }

  public static CompletableFuture<FriendData> getFriendData(UUID player) {
    return CompletableFuture.supplyAsync(() -> {
      String query = "SELECT * FROM surffriends WHERE uuid = ?";

      try (Connection connection = dataSource.getConnection();
          PreparedStatement ps = connection.prepareStatement(query)) {

        ps.setString(1, player.toString());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
          ObjectList<UUID> friends = parseUUIDList(rs.getString("friends"));
          ObjectList<UUID> friendRequests = parseUUIDList(rs.getString("friendrequests"));
          Boolean allowRequests = rs.getBoolean("allowrequests");

          return FriendData.builder()
              .player(player)
              .friends(friends)
              .friendRequests(friendRequests)
              .allowRequests(allowRequests)
              .build();
        }
      } catch (SQLException e) {
        Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
      }
      return null;
    });
  }

  public static CompletableFuture<Void> saveFriendData(FriendData friendData) {
    return CompletableFuture.runAsync(() -> {
      String query = """
            INSERT INTO surffriends (uuid, friends, friendrequests, allowrequests)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                friends = VALUES(friends),
                friendrequests = VALUES(friendrequests),
                allowrequests = VALUES(allowrequests)
            """;

      try (Connection connection = dataSource.getConnection();
          PreparedStatement ps = connection.prepareStatement(query)) {

        ps.setString(1, friendData.getPlayer().toString());
        ps.setString(2, formatUUIDList(friendData.getFriends()));
        ps.setString(3, formatUUIDList(friendData.getFriendRequests()));
        ps.setBoolean(4, friendData.getAllowRequests());

        ps.executeUpdate();
      } catch (SQLException e) {
        Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
      }
    });
  }

  private static ObjectList<UUID> parseUUIDList(String data) {
    ObjectList<UUID> list = new ObjectArrayList<>();

    if (data == null || data.isEmpty()) return list;

    String[] uuids = data.split(",");
    for (String uuid : uuids) {
      list.add(UUID.fromString(uuid.trim()));
    }
    return list;
  }

  private static String formatUUIDList(ObjectList<UUID> uuidList) {
    if (uuidList == null || uuidList.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    for (UUID uuid : uuidList) {
      sb.append(uuid.toString()).append(",");
    }
    return sb.substring(0, sb.length() - 1);
  }

  public static void closeConnection() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
