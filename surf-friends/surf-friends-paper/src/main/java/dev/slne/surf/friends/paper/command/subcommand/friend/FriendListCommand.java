package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import dev.slne.surf.friends.paper.impl.LocalFileBasedFriendApi;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class FriendListCommand extends CommandAPICommand {
    private final LocalFileBasedFriendApi api = FriendsPaperPlugin.instance().api();


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

                player.sendMessage(FriendsPaperPlugin.prefix().append(MiniMessage.miniMessage().deserialize(message.toString())));

            }catch (InterruptedException | ExecutionException  e){
                FriendsPaperPlugin.logger().error(e.getMessage());
            }
        });
    }
}
