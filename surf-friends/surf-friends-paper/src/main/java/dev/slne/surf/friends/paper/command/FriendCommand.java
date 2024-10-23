package dev.slne.surf.friends.paper.command;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.paper.command.subcommand.FriendAcceptCommand;
import dev.slne.surf.friends.paper.command.subcommand.FriendAddCommand;
import dev.slne.surf.friends.paper.command.subcommand.FriendDenyCommand;
import dev.slne.surf.friends.paper.command.subcommand.FriendJumpCommand;
import dev.slne.surf.friends.paper.command.subcommand.FriendRemoveCommand;
import dev.slne.surf.friends.paper.command.subcommand.FriendToggleCommand;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;

public class FriendCommand extends CommandAPICommand {
    public FriendCommand(String name) {
        super(name);
        withPermission("surf-social.friends.friend.command");

        withAliases("friends");

        withSubcommand(new FriendAcceptCommand("accept"));
        withSubcommand(new FriendAddCommand("add"));
        withSubcommand(new FriendDenyCommand("deny"));
        withSubcommand(new FriendRemoveCommand("remove"));
        withSubcommand(new FriendToggleCommand("toggle"));
        withSubcommand(new FriendJumpCommand("jump"));

        executesPlayer((player, info) -> {
            new FriendMainMenu().show(player);
        });
    }
}