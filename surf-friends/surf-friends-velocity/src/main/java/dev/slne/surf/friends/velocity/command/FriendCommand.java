package dev.slne.surf.friends.velocity.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.velocity.command.subcommand.FriendAddCommand;
import dev.slne.surf.friends.velocity.command.subcommand.FriendListCommand;
import dev.slne.surf.friends.velocity.command.subcommand.FriendRemoveCommand;

public class FriendCommand extends CommandAPICommand {
    public FriendCommand(String commandName) {
        super(commandName);

        withPermission("surf-social.friends.command");


        withSubcommand(new FriendAddCommand("add"));
        withSubcommand(new FriendRemoveCommand("remove"));
        withSubcommand(new FriendListCommand("list"));
    }
}
