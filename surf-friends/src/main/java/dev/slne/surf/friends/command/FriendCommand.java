package dev.slne.surf.friends.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.command.subcommand.FriendAcceptCommand;
import dev.slne.surf.friends.command.subcommand.FriendAddCommand;
import dev.slne.surf.friends.command.subcommand.FriendDenyCommand;
import dev.slne.surf.friends.command.subcommand.FriendJumpCommand;
import dev.slne.surf.friends.command.subcommand.FriendRemoveCommand;
import dev.slne.surf.friends.command.subcommand.FriendToggleCommand;

public class FriendCommand extends CommandAPICommand {

  public FriendCommand(String commandName) {
    super(commandName);

    withPermission("surf.friends.command.friend");

    withSubcommand(new FriendAcceptCommand("accept"));
    withSubcommand(new FriendAddCommand("add"));
    withSubcommand(new FriendDenyCommand("deny"));
    withSubcommand(new FriendRemoveCommand("remove"));
    withSubcommand(new FriendToggleCommand("toggle"));
    withSubcommand(new FriendJumpCommand("jump"));

  }
}
