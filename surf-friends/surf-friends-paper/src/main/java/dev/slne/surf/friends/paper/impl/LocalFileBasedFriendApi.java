package dev.slne.surf.friends.paper.impl;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.api.event.*;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalFileBasedFriendApi extends FriendApi {

    private final Object2ObjectMap<UUID, ObjectList<UUID>> friends = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, ObjectList<UUID>> friendRequests = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, Boolean> friendRequestSettings = new Object2ObjectOpenHashMap<>();

    private final FriendLogger logger = FriendsPaperPlugin.logger();

    private final FileConfiguration config = FriendsPaperPlugin.instance().getConfig();

    @Override
    public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            if(friends.get(player) == null){
                friends.put(player, new ObjectArrayList<>());
            }


            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friends.get(player));

            if(beforeAction.contains(target)){
                return false;
            }

            if(player.equals(target)){
                return false;
            }

            beforeAction.add(target);
            friends.put(player, beforeAction);

            Bukkit.getPluginManager().callEvent(new FriendAddEvent(player, target));



            this.sendIfOnline(player, String.format("Du bist nun mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
            this.sendIfOnline(target, String.format("Du bist nun mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(player).getName()));
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

            this.sendIfOnline(player, String.format("Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(target).getName()));
            this.sendIfOnline(target, String.format("Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", Bukkit.getOfflinePlayer(player).getName()));
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
            if(!friendRequests.containsKey(player)){
                friendRequests.put(player, new ObjectArrayList<>());
            }

            ObjectList<UUID> beforeAction = new ObjectArrayList<>(friendRequests.get(target));
//            FriendRequestSendEvent event = new FriendRequestSendEvent(player, target, false);
//
//            Bukkit.getPluginManager().callEvent(event);
//
//
//            if(event.isCancelled()){
//                return false;
//            }

            if(beforeAction.contains(player)){
                this.sendIfOnline(player, String.format("Du hast bereits eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", Bukkit.getOfflinePlayer(target).getName()));
                return false;
            }

            if(player.equals(target)){
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

            this.sendIfOnline(player, String.format("Du hast die Freundschaftsanfrage von <gold>%s</gold> abgelehnt.", Bukkit.getOfflinePlayer(target).getName()));
            this.sendIfOnline(target, String.format("<gold>%s</gold> hat deine Freundschaftsanfrage abgelehnt.", Bukkit.getOfflinePlayer(player).getName()));
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
                config.set("storage." + uuid + ".friends", friends.get(uuid).toString());
            }
            for(UUID uuid : friendRequests.keySet()){
                config.set("storage." + uuid + ".requests", friendRequests.get(uuid).toString());
            }

            for(UUID uuid : friendRequestSettings.keySet()){
                config.set("storage." + uuid + ".setting", friendRequestSettings.get(uuid).toString());
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

                    Bukkit.getPluginManager().callEvent(new FriendToggleEvent(player, true, false));
                }else{
                    friendRequestSettings.put(player, true);

                    Bukkit.getPluginManager().callEvent(new FriendToggleEvent(player, false, true));
                }
            }else{
                friendRequestSettings.put(player, false);

                Bukkit.getPluginManager().callEvent(new FriendToggleEvent(player, true, false));
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
