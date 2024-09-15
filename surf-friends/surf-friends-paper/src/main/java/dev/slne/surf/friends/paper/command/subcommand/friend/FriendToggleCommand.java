package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.paper.FriendsPaperPlugin;

public class FriendToggleCommand extends CommandAPICommand {
    public FriendToggleCommand(String name) {
        super(name);

        executesPlayer((player, info)-> {
            FriendsPaperPlugin.instance().api().toggle(player.getUniqueId());
        });
    }
}
