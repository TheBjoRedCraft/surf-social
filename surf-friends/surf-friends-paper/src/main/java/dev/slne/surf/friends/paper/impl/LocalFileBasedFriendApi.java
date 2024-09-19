package dev.slne.surf.friends.paper.impl;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public class LocalFileBasedFriendApi extends FriendApi {

    private final Object2ObjectMap<UUID, ObjectList<UUID>> friends = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, ObjectList<UUID>> friendRequests = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, Boolean> friendRequestSettings = new Object2ObjectOpenHashMap<>();

    private final FileConfiguration config = FriendsPaperPlugin.instance().getConfig();

    @Override
    public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(friends.get(player) == null){
                friends.put(player, new ObjectArrayList<>());
            }


            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));

            if(beforeAction.contains(target)){
                this.sendIfOnline(player, String.format("Du bist bereits mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            beforeAction.add(target);
            friends.put(player, beforeAction);



            this.sendIfOnline(player, String.format("Du bist nun mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
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
                this.sendIfOnline(player, String.format("Du bist nicht mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, String.format("Du bist nicht mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            beforeAction.remove(target);


            this.friends.put(player, beforeAction);

            this.sendIfOnline(player, String.format("Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
            return true;
        });
    }

    @Override
    public CompletableFuture<List<UUID>> getFriends(UUID player) {
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
                this.sendIfOnline(player, String.format("Du hast bereits eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }


            beforeAction.add(player);
            this.friendRequests.put(target, beforeAction);

            this.sendIfOnline(player, String.format("Du hast eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", Bukkit.getOfflinePlayer(target).getName()));
            this.sendIfOnline(target, String.format("Du hast eine Freundschaftsanfrage von <gold>%s</gold> erhalten.", Bukkit.getOfflinePlayer(player).getName()));
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
            if(!friendRequests.containsKey(player)){
                friendRequests.put(player, new ObjectArrayList<>());
            }

            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(player));


            if(!beforeAction.contains(target)){
                this.sendIfOnline(player, String.format("Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", Bukkit.getOfflinePlayer(target).getName()));
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
                this.sendIfOnline(player, String.format("Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            beforeAction.remove(target);


            this.friendRequests.put(player, beforeAction);

            this.sendIfOnline(player, String.format("Du hast die Freundschaftsanfrage von <gold>%s</gold> abgelehnt.", Bukkit.getOfflinePlayer(target).getName()));
            this.sendIfOnline(target, String.format("<gold>%s</gold> hat deine Freundschaftsanfrage abgelehnt.", Bukkit.getOfflinePlayer(player).getName()));
            return true;
        });
    }

    @Override
    public CompletableFuture<String> getServerFromPlayer(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            Player target = Bukkit.getPlayer(player);

            if(target == null){
                return "Offline";
            }else {
                return "Nicht angegeben";
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> init() {
        return CompletableFuture.supplyAsync(() -> {
            if (config.getConfigurationSection("storage") == null) {
                return false;
            }

            for (String entry : config.getConfigurationSection("storage").getKeys(false)) {
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
            }

            return true;
        });
    }


    @Override
    public CompletableFuture<Boolean> exit() {
        return CompletableFuture.supplyAsync(() -> {
            for (UUID uuid : friends.keySet()) {
                ObjectList<String> friendUUIDStrings = new ObjectArrayList<>();

                friends.get(uuid).forEach(friendUuid -> friendUUIDStrings.add(friendUuid.toString()));
                config.set("storage." + uuid + ".friends", friendUUIDStrings);
            }

            for (UUID uuid : friendRequests.keySet()) {
                ObjectList<String> requestUUIDStrings = new ObjectArrayList<>();

                friendRequests.get(uuid).forEach(requestUuid -> requestUUIDStrings.add(requestUuid.toString()));
                config.set("storage." + uuid + ".requests", requestUUIDStrings);
            }

            for (UUID uuid : friendRequestSettings.keySet()) {
                config.set("storage." + uuid + ".setting", friendRequestSettings.get(uuid));
            }

            FriendsPaperPlugin.instance().saveConfig();
            return true;
        });
    }


    @Override
    public CompletableFuture<Boolean> toggle(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            if(friendRequestSettings.containsKey(player)){
                if(friendRequestSettings.get(player)){
                    friendRequestSettings.put(player, false);
                }else{
                    friendRequestSettings.put(player, true);
                }
            }else{
                friendRequestSettings.put(player, false);
            }

            return true;
        });
    }


    private void sendIfOnline(UUID player, String message){
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);

        if(!p.isOnline()){
            return;
        }

        p.getPlayer().sendMessage(FriendsPaperPlugin.prefix().append(MiniMessage.miniMessage().deserialize(message)));
    }
}
