package dev.slne.surf.friends.paper.mysqlbased.api;

import com.google.auto.service.AutoService;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.paper.PaperInstance;
import dev.slne.surf.friends.paper.mysqlbased.api.util.ByteUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
@AutoService(FriendApi.class)
public class FriendApiFallback implements FriendApi {
  private Connection connection;
  private static final String DB_URL = PaperInstance.instance().getConfig().getString("database.url", "");
  private static final String USER = PaperInstance.instance().getConfig().getString("database.user", "");
  private static final String PASSWORD = PaperInstance.instance().getConfig().getString("database.password", "");

  @Getter
  private static final Object2ObjectMap<UUID, FriendData> data = new Object2ObjectOpenHashMap<>();

  @Getter
  private static FriendApiFallback instance;

  public FriendApiFallback() {
    try {
      connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
      
      this.createTables();
    } catch (SQLException e) {
      PaperInstance.instance().logger().error(e.getMessage());
    }

    instance = this;
  }

  private void createTables() {
    String statement = "CREATE TABLE IF NOT EXISTS friends (uuid BINARY(16) PRIMARY KEY, friend_list BLOB, friend_requests BLOB, allow_requests BOOLEAN)";

    try (PreparedStatement stmt = connection.prepareStatement(statement)) {
      stmt.execute();
    } catch (SQLException e) {
      PaperInstance.instance().logger().error(e.getMessage());
    }
  }

  @Override
  public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(player);

      if (friendData.getFriendList().contains(target)) {
        this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
        return false;
      }

      if (player.equals(target)) {
        this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
        return false;
      }

      friendData.getFriendList().add(target);

      this.saveDataLocal(player, friendData);
      this.sendIfOnline(player, "Du bist nun mit <gold>%s</gold> befreundet.", target);
      return true;
    });
  }

  @Override
  public CompletableFuture<Boolean> removeFriend(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(player);

      if (!friendData.getFriendList().contains(target)) {
        this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
        return false;
      }

      if (player.equals(target)) {
        this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
        return false;
      }

      friendData.getFriendList().remove(target);

      this.saveDataLocal(player, friendData);
      this.sendIfOnline(player, "Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", target);
      return true;
    });
  }

  @Override
  public ObjectList<UUID> getFriends(UUID player) {
    return new ObjectArrayList<>(this.getData(player).getFriendList());
  }

  @Override
  public CompletableFuture<Boolean> areFriends(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> this.getData(player).getFriendList().contains(target));
  }

  @Override
  public CompletableFuture<Boolean> sendFriendRequest(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(target);

      if (friendData.getFriendRequests().contains(player)) {
        this.sendIfOnline(player, "Du hast bereits eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);
        return false;
      }

      if (player.equals(target)) {
        this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
        return false;
      }

      if (friendData.getFriendList().contains(player)) {
        this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
        return false;
      }

      friendData.getFriendRequests().add(player);

      this.saveDataLocal(target, friendData);
      this.sendIfOnline(player, "Du hast eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);

      if (friendData.getAllowRequests()) {
        this.sendIfOnline(target, "Du hast eine Freundschaftsanfrage von <gold>%s</gold> erhalten.", player);
      }

      return true;
    });
  }

  @Override
  public ObjectList<UUID> getFriendRequests(UUID player) {
    return new ObjectArrayList<>(this.getData(player).getFriendRequests());
  }

  @Override
  public CompletableFuture<Boolean> acceptFriendRequest(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(player);

      if (!friendData.getFriendRequests().contains(target)) {
        this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
        return false;
      }

      if (player.equals(target)) {
        this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
        return false;
      }

      friendData.getFriendRequests().remove(target);

      this.saveDataLocal(player, friendData);

      this.addFriend(player, target);
      this.addFriend(target, player);
      return true;
    });
  }

  @Override
  public CompletableFuture<Boolean> denyFriendRequest(UUID player, UUID target) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(player);

      if (!friendData.getFriendRequests().contains(target)) {
        this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
        return false;
      }

      if (player.equals(target)) {
        this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
        return false;
      }

      friendData.getFriendRequests().remove(target);

      this.saveDataLocal(player, friendData);
      this.sendIfOnline(player, "Du hast die Freundschaftsanfrage von <gold>%s</gold> abgelehnt.", target);
      this.sendIfOnline(target, "<gold>%s</gold> hat deine Freundschaftsanfrage abgelehnt.", player);
      return true;
    });
  }

  @Override
  public Boolean init() {
    return true;
  }

  @Override
  public Boolean exit() {
    return true;
  }

  @Override
  public CompletableFuture<Boolean> toggle(UUID player) {
    return CompletableFuture.supplyAsync(() -> {
      FriendData friendData = this.getData(player);

      friendData.allowRequests(!friendData.getAllowRequests());

      this.saveDataLocal(player, friendData);
      this.sendIfOnline(player, friendData.getAllowRequests() ? "Du hast nun Freundschaftsanfragen aktiviert." : "Du hast nun Freundschaftsanfragen deaktiviert.");

      return true;
    });
  }

  private FriendData getData(UUID uuid) {
    if (data.get(uuid) == null) {
      FriendData friendData = loadData(uuid);

      if (friendData == null) {
        friendData = new FriendData().friendList(new ArrayList<>()).friendRequests(new ArrayList<>()).allowRequests(true);
      }

      data.put(uuid, friendData);
    }

    return data.get(uuid);
  }

  public void loadFriendData(UUID uuid) {
    FriendData friendData = this.loadData(uuid);
    if (friendData == null) {
      friendData = new FriendData().friendList(new ArrayList<>()).friendRequests(new ArrayList<>()).allowRequests(true);
    }
    data.put(uuid, friendData);
  }

  private FriendData loadData(UUID uuid) {
    String statement = "SELECT * FROM friends WHERE uuid = ?";

    try (PreparedStatement stmt = connection.prepareStatement(statement)) {
      stmt.setBytes(1, ByteUtil.toBytes(uuid));

      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        List<UUID> friendList = ByteUtil.toList(rs.getBytes("friend_list"));
        List<UUID> friendRequests = ByteUtil.toList(rs.getBytes("friend_requests"));

        boolean allowRequests = rs.getBoolean("allow_requests");
        return new FriendData().friendList(friendList).friendRequests(friendRequests).allowRequests(allowRequests);
      }
    } catch (SQLException e) {
      PaperInstance.instance().logger().error(e.getMessage());
    }
    return null;
  }

  public void saveData(UUID uuid, FriendData friendData) {
    String statement = "REPLACE INTO friends (uuid, friend_list, friend_requests, allow_requests) VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(statement)) {
      stmt.setBytes(1, ByteUtil.toBytes(uuid));
      stmt.setBytes(2, ByteUtil.fromList(friendData.getFriendList()));
      stmt.setBytes(3, ByteUtil.fromList(friendData.getFriendRequests()));
      stmt.setBoolean(4, friendData.getAllowRequests());
      stmt.executeUpdate();

    } catch (SQLException e) {
      PaperInstance.instance().logger().error(e.getMessage());
    }
  }

  public void saveDataLocal(UUID uuid, FriendData friendData){
    data.put(uuid, friendData);
  }

  private void sendIfOnline(UUID uuid, String message) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      player.sendMessage(FriendCore.prefix().append(Component.text(message)));
    }
  }

  private void sendIfOnline(UUID uuid, String message, UUID tUuid) {
    Player player = Bukkit.getPlayer(uuid);
    OfflinePlayer target = Bukkit.getOfflinePlayer(tUuid);

    if(player == null){
      return;
    }

    if(target.getName() == null){
      return;
    }

    player.sendMessage(FriendCore.prefix().append(Component.text(String.join(message, target.getName()))));
  }

  public void handleJoin(PlayerJoinEvent event){
    loadFriendData(event.getPlayer().getUniqueId());
  }

  public void handleQuit(PlayerQuitEvent event){
    saveData(event.getPlayer().getUniqueId(), this.getData(event.getPlayer().getUniqueId()));
  }
}
