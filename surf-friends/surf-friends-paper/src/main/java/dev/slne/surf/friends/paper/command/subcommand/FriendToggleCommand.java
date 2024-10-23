package dev.slne.surf.friends.paper.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.paper.PaperInstance;


public class FriendToggleCommand extends CommandAPICommand {
    public FriendToggleCommand(String name) {
        super(name);

        executesPlayer((player, info)-> {
            PaperInstance.instance().friendApi().toggle(player.getUniqueId());
        });
    }
}
