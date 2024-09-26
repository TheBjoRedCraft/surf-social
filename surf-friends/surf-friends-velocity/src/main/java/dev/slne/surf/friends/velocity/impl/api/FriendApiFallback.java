package dev.slne.surf.friends.velocity.impl.api;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;

import com.velocitypowered.api.proxy.server.RegisteredServer;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Services;

@AutoService(FriendApi.class)
@SuppressWarnings("unchecked")
public class FriendApiFallback implements FriendApi, Services.Fallback {
    private final File jsonFile = new File("plugins/surf-friends-velocity/friends.json");
    private final Gson gson = new Gson();

    private final Object2ObjectMap<UUID, FriendData> data = new Object2ObjectOpenHashMap<>();


    @Override
    public CompletableFuture<Boolean> addFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(player);

            if(friendData.getFriendList().contains(target)){
                this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            friendData.getFriendList().add(target);

            data.put(player, friendData);

            this.sendIfOnline(player, "Du bist nun mit <gold>%s</gold> befreundet.", target);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeFriend(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(player);


            if(!friendData.getFriendList().contains(target)){
                this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du bist nicht mit <gold>%s</gold> befreundet.", target);
                return false;
            }

            friendData.getFriendList().remove(target);


            data.put(player, friendData);

            this.sendIfOnline(player, "Du bist nun nicht mehr mit <gold>%s</gold> befreundet.", target);
            return true;
        });
    }

    @Override
    public CompletableFuture<ObjectList<UUID>> getFriends(UUID player) {
        return CompletableFuture.supplyAsync(() -> this.getData(player).getFriendList());
    }

    @Override
    public CompletableFuture<Boolean> areFriends(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> this.getData(player).getFriendList().contains(target));
    }

    @Override
    public CompletableFuture<Boolean> sendFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(target);

            if(friendData.getFriendRequests().contains(player)){

                this.sendIfOnline(player, "Du hast bereits eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);
                return false;
            }


            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            if(friendData.getFriendList().contains(player)) {
                this.sendIfOnline(player, "Du bist bereits mit <gold>%s</gold> befreundet.", target);
                return false;
            }


            friendData.getFriendRequests().add(player);

            data.put(target, friendData);

            this.sendIfOnline(player, "Du hast eine Freundschaftsanfrage an <gold>%s</gold> gesendet.", target);
            this.sendIfOnline(target, "Du hast eine Freundschaftsanfrage von <gold>%s</gold> erhalten.", player);

            return true;
        });
    }

    @Override
    public CompletableFuture<ObjectList<UUID>> getFriendRequests(UUID player) {
        return CompletableFuture.supplyAsync(() -> this.getData(player).getFriendRequests());
    }

    @Override
    public CompletableFuture<Boolean> acceptFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(player);


            if(!friendData.getFriendRequests().contains(target)){
                this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            friendData.getFriendRequests().remove(target);


            data.put(player, friendData);

            this.addFriend(player, target);
            this.addFriend(target, player);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> denyFriendRequest(UUID player, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(player);


            if(!friendData.getFriendRequests().contains(target)){
                this.sendIfOnline(player, "Du hast keine offene Freundschaftsanfrage von <gold>%s</gold>.", target);
                return false;
            }

            if(player.equals(target)){
                this.sendIfOnline(player, "Du kannst nicht mit dir selbst befreundet sein.");
                return false;
            }

            friendData.getFriendRequests().remove(target);


            data.put(player, friendData);

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
    public Boolean init() {
        if (!jsonFile.exists()) {
            VelocityInstance.info("There was no data to load.");
            return true;
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            Type type = new TypeToken<Map<UUID, FriendData>>(){}.getType();
            Map<UUID, FriendData> loadedData = gson.fromJson(reader, type);

            if (loadedData != null) {
                data.putAll(loadedData);
            } else {
                VelocityInstance.info("There was an error while loading data from storage.");
            }

        } catch (IOException e) {
            VelocityInstance.error(e.getMessage());
            return false;
        }

        VelocityInstance.info("Successfully loaded data from storage.");
        return true;
    }

    @Override
    public Boolean exit() {
        if(!jsonFile.exists()){
            jsonFile.getParentFile().mkdirs();


          try {
            jsonFile.createNewFile();
          } catch (IOException e) {
              VelocityInstance.error(e.getMessage());
          }
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(data, writer);

        } catch (IOException e) {
            VelocityInstance.error(e.getMessage());
            return false;
        }

        VelocityInstance.info("Successfully saved data to storage.");
        return true;
    }


    @Override
    public CompletableFuture<Boolean> toggle(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            FriendData friendData = this.getData(player);

            if(friendData.getAllowRequests()){
                friendData.allowRequests(false);

                this.sendIfOnline(player, "Du hast nun Freundschaftsanfragen deaktiviert.");
            }else{
                friendData.allowRequests(true);

                this.sendIfOnline(player, "Du hast nun Freundschaftsanfragen aktiviert.");
            }


            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> send(UUID player, String server) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<RegisteredServer> registeredServer = VelocityInstance.getInstance().getProxy().getServer(server);
            Optional<Player> velocityPlayer = VelocityInstance.getInstance().getProxy().getPlayer(player);

            if(registeredServer.isEmpty()){
                return false;
            }

            if(velocityPlayer.isEmpty()){
                return false;
            }

            this.sendIfOnline(player, String.format("Versuche dich zu %s zu senden...", server));

            velocityPlayer.get().createConnectionRequest(registeredServer.get()).fireAndForget();

            this.sendIfOnline(player, String.format("Du wurdest erfolgreich zu %s gesendet.", server));

            return true;
        });
    }

    private FriendData getData(UUID uuid){
        if(data.get(uuid) == null){
            data.put(uuid, new FriendData().friendList(new ObjectArrayList<>()).friendRequests(new ObjectArrayList<>()).allowRequests(true));
        }

        return data.get(uuid);
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
