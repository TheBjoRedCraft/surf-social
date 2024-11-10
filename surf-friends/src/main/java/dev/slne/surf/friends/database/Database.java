package dev.slne.surf.friends.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.slne.surf.friends.FriendData;
import dev.slne.surf.friends.SurfFriendsPlugin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.sql.*;
import java.util.UUID;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;

public class Database {

  private static final HikariDataSource dataSource;

  static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://localhost:3306/your_database_name");
    config.setUsername("your_username");
    config.setPassword("your_password");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    dataSource = new HikariDataSource(config);
  }

  public static void createTable() {
    String table = """
            CREATE TABLE IF NOT EXISTS surf-friends (
                player_uuid CHAR(36) PRIMARY KEY,
                friends TEXT,
                friend_requests TEXT,
                allow_requests BOOLEAN
            );
            """;

    try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
      statement.execute(table);
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
    }
  }

  public static FriendData getFriendData(UUID player) {
    String query = "SELECT * FROM surf-friends WHERE player_uuid = ?";

    try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

      ps.setString(1, player.toString());
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        ObjectList<UUID> friends = parseUUIDList(rs.getString("friends"));
        ObjectList<UUID> friendRequests = parseUUIDList(rs.getString("friend_requests"));
        Boolean allowRequests = rs.getBoolean("allow_requests");

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
  }

  public static void saveFriendData(FriendData friendData) {
    String query = """
            INSERT INTO surf-friends (player_uuid, friends, friend_requests, allow_requests)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                friends = VALUES(friends),
                friend_requests = VALUES(friend_requests),
                allow_requests = VALUES(allow_requests)
            """;

    try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

      ps.setString(1, friendData.getPlayer().toString());
      ps.setString(2, formatUUIDList(friendData.getFriends()));
      ps.setString(3, formatUUIDList(friendData.getFriendRequests()));
      ps.setBoolean(4, friendData.getAllowRequests());

      ps.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
    }
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

  public static void close() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
