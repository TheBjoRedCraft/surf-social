package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.provider.ChannelProvider;
import dev.slne.surf.social.chat.util.PageableMessageBuilder;
import dev.slne.surf.social.chat.util.PluginColor;
import net.kyori.adventure.text.Component;

public class ChannelListCommand extends CommandAPICommand {

  public ChannelListCommand(String commandName) {
    super(commandName);

    withOptionalArguments(new IntegerArgument("page"));
    executesPlayer((player, args ) -> {
      PageableMessageBuilder message = new PageableMessageBuilder();
      Integer page = args.getOrDefaultUnchecked("page", 1);

      int index = 0;

      message.setPageCommand("/channel list %page%");

      for (Channel channel : ChannelProvider.getInstance().getChannels().values()) {
        message.addLine(Component.text(index ++ + ". ", PluginColor.LIGHT_GRAY).append(Component.text(channel.getName(), PluginColor.GOLD)).append(Component.newline()).hoverEvent(this.getHoverText(channel)));
      }

      message.send(player, page);
    });
  }

  private Component getHoverText(Channel channel) {
    return Component.text(channel.getDescription())
        .append(Component.newline())
        .append(Component.text("Mitglieder: ", PluginColor.LIGHT_GRAY).append(Component.text(channel.getMembers().size(), PluginColor.GOLD)))
        .append(Component.newline())
        .append(Component.text("Moderatoren: ", PluginColor.LIGHT_GRAY).append(Component.text(channel.getModerators().size(), PluginColor.GOLD)))
        .append(Component.newline())
        .append(Component.text("Einladungen: ", PluginColor.LIGHT_GRAY).append(Component.text(channel.getInvites().size(), PluginColor.GOLD)))
        .append(Component.newline());
  }
}
