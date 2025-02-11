package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
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
    return new MessageBuilder()
        .primary("Kanalinformation: ").info(channel.getName()).newLine()
        .variableKey("Beschreibung: ").variableValue(channel.getDescription()).newLine()
        .variableKey("Besitzer: ").variableValue(channel.getOwner().getName()).newLine()
        .variableKey("Status: ").variableValue(channel.isClosed() ? "Geschlossen" : "Offen").newLine()
        .variableKey("Mitglieder: ").variableValue(String.valueOf(channel.getMembers().size() + channel.getModerators().size() + 1)).newLine()
        .variableKey("Einladungen: ").variableValue(String.valueOf(channel.getInvites().size())).newLine()
        .build();
  }
}
