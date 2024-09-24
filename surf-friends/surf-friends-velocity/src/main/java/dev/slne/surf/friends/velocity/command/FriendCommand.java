package dev.slne.surf.friends.velocity.command;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.velocity.VelocityInstance;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendAcceptCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendAddCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendDenyCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendRemoveCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendSaveCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendToggleCommand;

public class FriendCommand extends CommandAPICommand {
    public FriendCommand(String name) {
        super(name);
        withPermission("surf-social.friends.friend.command");

        withSubcommand(new FriendAcceptCommand("accept"));
        withSubcommand(new FriendAddCommand("add"));
        withSubcommand(new FriendAddCommand("request"));
        withSubcommand(new FriendDenyCommand("deny"));
        withSubcommand(new FriendRemoveCommand("remove"));
        withSubcommand(new FriendToggleCommand("toggle"));
        withSubcommand(new FriendSaveCommand("save"));

        executesPlayer((player, info) -> {
            VelocityInstance.getInstance().openMenu(player, "friends:main");
        });
    }
}