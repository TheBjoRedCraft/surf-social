package dev.slne.surf.friends.paper.impl;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.api.event.*;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalFileBasedFriendApi extends FriendApi {

    private final Object2ObjectMap<UUID, ObjectList<UUID>> friends = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, ObjectList<UUID>> friendRequests = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, Boolean> friendRequestSettings = new Object2ObjectOpenHashMap<>();

    private final FileConfiguration config = FriendsPaperPlugin.instance().getConfig();

    @Override
    public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));

            if(beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.add(target);


            this.friends.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendAddEvent(player, target));
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));


            if(!beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.remove(target);


            this.friends.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendRemoveEvent(player, target));
            return true;
        });
    }

    @Override
    public CompletableFuture<List<UUID>> getFriends(UUID player) {
        return CompletableFuture.supplyAsync(() -> friends.get(player));
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
            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.add(target);


            this.friendRequests.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendRequestSendEvent(player, target));
            return true;
        });
    }

    @Override
    public CompletableFuture<List<UUID>> getFriendRequests(UUID player) {
        return CompletableFuture.supplyAsync(() -> friendRequests.get(player));
    }

    @Override
    public CompletableFuture<Boolean> acceptFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.remove(target);


            this.friendRequests.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendRequestAcceptEvent(player, target));
            this.addFriend(player, target);

            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> denyFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.remove(target);


            this.friendRequests.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendRequestDenyEvent(player, target));
            return true;
        });
    }

    @Override
    public CompletableFuture<String> getServerFromPlayer(UUID player) {
        return CompletableFuture.supplyAsync(() -> "Nicht angegeben.");
    }

    @Override
    public CompletableFuture<Boolean> init(){
        return CompletableFuture.supplyAsync(() -> {

            if(config.getConfigurationSection("storage") == null){
                return false;
            }

            for(String entry : config.getConfigurationSection("storage").getKeys(false)){
                Boolean setting = config.getBoolean("storage." + entry + ".setting");
                List<String> friends = config.getStringList("storage." + entry + ".friends");
                List<String> friendRequests = config.getStringList("storage." + entry + ".requests");

                ObjectList<UUID> friendUUIDs = new ObjectArrayList<>();
                ObjectList<UUID> friendRequestUUIDs = new ObjectArrayList<>();

                UUID uuid = UUID.fromString(entry);

                friends.forEach(id -> friendUUIDs.add(UUID.fromString(id)));
                friendRequests.forEach(id -> friendRequestUUIDs.add(UUID.fromString(id)));

                this.friends.put(uuid, friendUUIDs);
                this.friendRequests.put(uuid, friendRequestUUIDs);
                this.friendRequestSettings.put(uuid, setting);

                return true;
            }

            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> exit(){
        return CompletableFuture.supplyAsync(() -> {
            for(UUID uuid : friends.keySet()){
                config.set("storage." + uuid + "friends", friends.get(uuid));
            }
            for(UUID uuid : friendRequests.keySet()){
                config.set("storage." + uuid + "requests", friendRequests.get(uuid));
            }

            for(UUID uuid : friendRequestSettings.keySet()){
                config.set("storage." + uuid + "setting", friendRequestSettings.get(uuid));
            }

            return true;
        });
    }
}
