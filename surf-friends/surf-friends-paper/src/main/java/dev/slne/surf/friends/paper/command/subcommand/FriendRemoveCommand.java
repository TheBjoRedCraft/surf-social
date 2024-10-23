package dev.slne.surf.friends.paper.command.subcommand;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;

import dev.slne.surf.friends.paper.PaperInstance;
import org.bukkit.OfflinePlayer;

public class FriendRemoveCommand extends CommandAPICommand {
    public FriendRemoveCommand(String name) {
        super(name);
        withArguments(new OfflinePlayerArgument("target"));

        executesPlayer((player, args)-> {
            OfflinePlayer target = args.getUnchecked("target");

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            PaperInstance.instance().friendApi().removeFriend(player.getUniqueId(), target.getUniqueId());
            PaperInstance.instance().friendApi().removeFriend(target.getUniqueId(), player.getUniqueId());
        });
    }
}
