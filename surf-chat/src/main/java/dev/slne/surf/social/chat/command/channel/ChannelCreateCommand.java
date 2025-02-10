package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.slne.surf.social.chat.object.Channel;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class ChannelCreateCommand extends CommandAPICommand {

  public ChannelCreateCommand(String commandName) {
    super(commandName);

    withRequirement((sender) -> Channel.getChannel(sender) == null);
    withArguments(new TextArgument("name"));
    withArguments(new StringArgument("description"));
    executesPlayer((player, args) -> {
      String name = args.getUnchecked("name");
      String description = args.getUnchecked("description");
      Channel channel = Channel.builder()
          .name(name)
          .description(description)
          .members(new ObjectArraySet<>())
          .invites(new ObjectArraySet<>())
          .closed(true)
          .owner(player)
          .build();

      channel.register();
    });
  }
}
