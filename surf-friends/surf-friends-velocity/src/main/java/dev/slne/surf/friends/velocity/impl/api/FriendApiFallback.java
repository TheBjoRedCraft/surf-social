package dev.slne.surf.friends.velocity.impl.api;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.velocity.VelocityInstance;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Services;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@AutoService(FriendApi.class)
@SuppressWarnings("unchecked")
public class FriendApiFallback implements FriendApi, Services.Fallback {

    private final File jsonFile = new File("plugins/surf-friends-velocity/friends.json");
    private final Gson gson = new Gson();

    private final Object2ObjectMap<UUID, ObjectList<UUID>> friends = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, ObjectList<UUID>> friendRequests = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, Boolean> friendRequestSettings = new Object2ObjectOpenHashMap<>();


    @Override
    public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(friends.get(player) == null){
                friends.put(player, new ObjectArrayList<>());
            }


            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));

            if(beforeAction.contains(target)){
                this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            beforeAction.add(target);
            friends.put(player, beforeAction);

            this.sendIfOnline(player, "Du bist nun mit <gold>%s</gold> befreundet.", target);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(friends.get(player) == null){
                friends.put(player, new ObjectArrayList<>());
            }

            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));


            if(!beforeAction.contains(target)){
                this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            beforeAction.remove(target);


            this.friends.put(player, beforeAction);

            this.sendIfOnline(player, "Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", target);
            return true;
        });
    }

    @Override
    public CompletableFuture<ObjectList<UUID>> getFriends(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friends.containsKey(player)){
                friends.put(player, new ObjectArrayList<>());
            }

            return friends.get(player);
        });
    }

    @Override
    public CompletableFuture<Boolean> areFriends(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friends.containsKey(player)){
                return false;
            }

            return friends.get(player).contains(target);
        });
    }

    @Override
    public CompletableFuture<Boolean> sendFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friendRequests.containsKey(target)){
                friendRequests.put(target, new ObjectArrayList<>());
            }


            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(target));

            if(beforeAction.contains(player)){

                this.sendIfOnline(player, "Du hast bereits eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);
                return false;
            }


            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }


            if(!friends.containsKey(player)){
                friends.put(player, new ObjectArrayList<>());
            }

            if(friends.get(player).contains(target)) {
                this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
                return false;
            }


            beforeAction.add(player);
            this.friendRequests.put(target, beforeAction);

            this.sendIfOnline(player, "Du hast eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);
            this.sendIfOnline(target, "Du hast eine Freundschaftsanfrage von <gold>%s</gold> erhalten.", player);

            return true;
        });
    }

    @Override
    public CompletableFuture<ObjectList<UUID>> getFriendRequests(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friendRequests.containsKey(player)){
                friendRequests.put(player, new ObjectArrayList<>());
            }

            return friendRequests.get(player);
        });
    }

    @Override
    public CompletableFuture<Boolean> acceptFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friendRequests.containsKey(player)){
                friendRequests.put(player, new ObjectArrayList<>());
            }

            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){

                this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            beforeAction.remove(target);


            this.friendRequests.put(player, beforeAction);

            this.addFriend(player, target);
            this.addFriend(target, player);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> denyFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(!friendRequests.containsKey(player)){
                friendRequests.put(player, new ObjectArrayList<>());
            }

            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){
                this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            beforeAction.remove(target);


            this.friendRequests.put(player, beforeAction);

            this.sendIfOnline(player, "Du hast die Freundschaftsanfrage von <gold>%s</gold> abgelehnt.", target);
            this.sendIfOnline(target, "<gold>%s</gold> hat deine Freundschaftsanfrage abgelehnt.", player);
            return true;
        });
    }

    @Override
    public CompletableFuture<String> getServerFromPlayer(UUID player) {
        return CompletableFuture.supplyAsync(() -> "Nicht angegeben");
    }

    @Override
    public CompletableFuture<Boolean> init() {
        return CompletableFuture.supplyAsync(() -> {
            if (!jsonFile.exists()) {
                return false;
            }

            try (FileReader reader = new FileReader(jsonFile)) {

                Type dataType = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
                Object2ObjectMap<String, Object2ObjectMap<String, Object>> data = gson.fromJson(reader, dataType);

                if (data == null) {
                    return false;
                }

                for (Entry<String, Object2ObjectMap<String, Object>> entry : data.entrySet()) {
                    UUID uuid = UUID.fromString(entry.getKey());

                    Object2ObjectMap<String, Object> userData = entry.getValue();
                    ObjectList<String> friendsList = (ObjectList<String>) userData.get("friends");
                    ObjectList<String> requestsList = (ObjectList<String>) userData.get("requests");
                    ObjectList<UUID> friendUUIDs = new ObjectArrayList<>();
                    ObjectList<UUID> requestUUIDs = new ObjectArrayList<>();

                    if (friendsList != null) {
                        friendsList.forEach(friend -> friendUUIDs.add(UUID.fromString(friend)));
                    }

                    friends.put(uuid, friendUUIDs);

                    if (requestsList != null) {
                        requestsList.forEach(request -> requestUUIDs.add(UUID.fromString(request)));
                    }
                    friendRequests.put(uuid, requestUUIDs);

                    Boolean setting = (Boolean) userData.get("setting");

                    friendRequestSettings.put(uuid, setting != null ? setting : true);
                }

                VelocityInstance.getInstance().getLogger().log(Level.FINEST, "Successfully loaded data.");
                return true;
            } catch (IOException e) {
                VelocityInstance.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exit() {
        return CompletableFuture.supplyAsync(() -> {
            Object2ObjectMap<String, Object2ObjectMap<String, Object>> toSave = new Object2ObjectOpenHashMap<>();

            if (!jsonFile.exists()) {
              try {
                  jsonFile.getParentFile().mkdirs();
                  jsonFile.createNewFile(); 
              } catch (IOException e) {
                  VelocityInstance.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
              }
            }

            for (UUID uuid : friends.keySet()) {
                Object2ObjectMap<String, Object> userData = new Object2ObjectOpenHashMap<>();
                ObjectList<UUID> friendsList = friends.get(uuid);
                ObjectList<UUID> requestsList = friendRequests.get(uuid);

                userData.put("friends", friendsList.stream().map(UUID::toString).collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll));
                userData.put("requests", requestsList != null ? requestsList.stream().map(UUID::toString).collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll) : new ObjectArrayList<>());
                userData.put("setting", friendRequestSettings.get(uuid));

                toSave.put(uuid.toString(), userData);
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(toSave, writer);

                VelocityInstance.getInstance().getLogger().log(Level.FINEST, "Successfully saved data.");

                return true;
            } catch (IOException e) {
                VelocityInstance.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
                return false;
            }
        });
    }


    @Override
    public CompletableFuture<Boolean> toggle(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            if(friendRequestSettings.containsKey(player)){
                if(friendRequestSettings.get(player)){
                    friendRequestSettings.put(player, false);

                    this.sendIfOnline(player, "Du hast nun Freundschaftsanfragen deaktiviert.");
                }else{
                    friendRequestSettings.put(player, true);

                    this.sendIfOnline(player, "Du hast nun Freundschaftsanfragen aktiviert.");
                }
            }else{
                friendRequestSettings.put(player, false);

                this.sendIfOnline(player, "Du hast nun Freundschaftsanfragen deaktiviert.");
            }

            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> send(UUID player, String server) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO: get server and implement method

            return true;
        });
    }

    private void sendIfOnline(UUID player, String message){
        Optional<Player> optionalPlayer = VelocityInstance.getInstance().getProxy().getPlayer(player);

        if(optionalPlayer.isEmpty()){
            return;
        }

        optionalPlayer.get().sendMessage(FriendCore.prefix().append(MiniMessage.miniMessage().deserialize(message)));
    }


    private void sendIfOnline(UUID player, String message, UUID target){
        Optional<Player> optionalPlayer = VelocityInstance.getInstance().getProxy().getPlayer(player);
        Optional<Player> optionalTarget = VelocityInstance.getInstance().getProxy().getPlayer(target);

        if(optionalPlayer.isEmpty()){
            return;
        }

        if(optionalTarget.isEmpty()){
            return;
        }

        optionalPlayer.get().sendMessage(FriendCore.prefix().append(MiniMessage.miniMessage().deserialize(message.replace("%s", optionalTarget.get().getUsername()))));
    }
}
