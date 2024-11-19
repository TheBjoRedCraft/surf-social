package dev.slne.surf.friends.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.slne.surf.friends.FriendData;
import dev.slne.surf.friends.SurfFriendsPlugin;

import dev.slne.surf.friends.config.PluginConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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

      try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, player.toString());

        try (ResultSet resultSet = statement.executeQuery()) {
          if (resultSet.next()) {
            String friends = resultSet.getString("friends");
            String friendRequests = resultSet.getString("friendrequests");
            boolean allowRequests = resultSet.getBoolean("allowrequests");

            ObjectList<UUID> friendsList = friends.isEmpty()
                ? new ObjectArrayList<>()
                : new ObjectArrayList<>(Arrays.stream(friends.split(","))
                    .map(UUID::fromString)
                    .collect(Collectors.toList()));

            ObjectList<UUID> friendRequestsList = friendRequests.isEmpty()
                ? new ObjectArrayList<>()
                : new ObjectArrayList<>(Arrays.stream(friendRequests.split(","))
                    .map(UUID::fromString)
                    .collect(Collectors.toList()));

            return FriendData.builder()
                .player(player)
                .friends(friendsList)
                .friendRequests(friendRequestsList)
                .allowRequests(allowRequests)
                .build();
          }
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
          allowrequests = VALUES(allowrequests);
          """;

      String friends = friendData.getFriends().stream()
          .map(UUID::toString)
          .collect(Collectors.joining(","));
      String friendRequests = friendData.getFriendRequests().stream()
          .map(UUID::toString)
          .collect(Collectors.joining(","));

      try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, friendData.getPlayer().toString());
        statement.setString(2, friends);
        statement.setString(3, friendRequests);
        statement.setBoolean(4, friendData.getAllowRequests());

        statement.executeUpdate();

      } catch (SQLException e) {
        Bukkit.getConsoleSender().sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text(e.getMessage())));
      }
    });
  }

  public static void closeConnection() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
