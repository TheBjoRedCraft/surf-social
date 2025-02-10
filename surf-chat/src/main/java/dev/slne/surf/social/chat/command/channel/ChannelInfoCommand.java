package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import net.kyori.adventure.text.Component;

public class ChannelInfoCommand extends CommandAPICommand {
  public ChannelInfoCommand(String commandName) {
    super(commandName);

    withOptionalArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getOrDefaultUnchecked("channel", Channel.getChannel(player));

      if(channel == null) {
        return;
      }

      player.sendMessage(createInfoMessage(channel));
    });
  }

  private Component createInfoMessage(Channel channel) {
    return Component.text("Kanalinformation: ").append(Component.text(channel.getName())).append(Component.newline())
        .append(Component.text("Beschreibung: ").append(Component.text(channel.getDescription())).append(Component.newline()))
        .append(Component.text("Mitglieder: ").append(Component.text(channel.getMembers().size())).append(Component.newline()))
        .append(Component.text("Einladungen: ").append(Component.text(channel.getInvites().size())).append(Component.newline()))
        .append(Component.text("Typ: ").append(Component.text(channel.isClosed() ? "Geschlossen" : "Offen")).append(Component.newline()))
        .append(Component.text("Besitzer: ").append(Component.text(channel.getOwner().getName())));
  }
}
