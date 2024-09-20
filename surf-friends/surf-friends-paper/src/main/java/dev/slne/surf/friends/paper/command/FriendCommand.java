package dev.slne.surf.friends.paper.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.paper.command.subcommand.friend.*;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;

public class FriendCommand extends CommandAPICommand {
    public FriendCommand(String name) {
        super(name);
        withPermission("surf-social.friends.friend.command");

        withSubcommand(new FriendAcceptCommand("accept"));
        withSubcommand(new FriendAddCommand("add"));
        withSubcommand(new FriendAddCommand("request"));
        withSubcommand(new FriendDenyCommand("deny"));
        withSubcommand(new FriendListCommand("list"));
        withSubcommand(new FriendRemoveCommand("remove"));
        withSubcommand(new FriendToggleCommand("toggle"));
        withSubcommand(new FriendSaveCommand("save"));

        executesPlayer((player, info) -> {
            new FriendMainMenu().show(player);
        });
    }
}