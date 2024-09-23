package dev.slne.surf.friends.velocity.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.velocity.VelocityInstance;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class FriendListCommand extends CommandAPICommand {
    private final FriendApi api = VelocityInstance.getInstance().getApi();


    public FriendListCommand(String name) {
        super(name);

        executesPlayer((player, info) -> {
            try {
                StringBuilder message = new StringBuilder(
                    "<white>Freunde: <gray>(<yellow>" + api.getFriends(player.getUniqueId()).get().size() + "<gray>) <white>");
                int current = 0;

                for (UUID uuid : api.getFriends(player.getUniqueId()).get()) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                    current++;

                    if (current == api.getFriends(player.getUniqueId()).get().size()) {
                        if (target.getName() != null) {
                            message.append("\n<white>").append(target.getName()).append(" (").append(api.getServerFromPlayer(player.getUniqueId()).get()).append(")");
                        }
                    } else {
                        if (target.getName() != null) {
                            message.append("\n<white>").append(target.getName()).append(" (").append(api.getServerFromPlayer(player.getUniqueId()).get()).append(")").append("<gray>, ");
                        }
                    }
                }

                player.sendMessage(FriendCore.prefix().append(MiniMessage.miniMessage().deserialize(message.toString())));

            }catch (InterruptedException | ExecutionException  e){
                VelocityInstance.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
            }
        });
    }
}
