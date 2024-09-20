package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.paper.FriendPlugin;
import net.kyori.adventure.text.Component;

public class FriendSaveCommand extends CommandAPICommand {
    public FriendSaveCommand(String name) {
        super(name);

        withPermission("surf-social.friends.friend.command.save");

        executesPlayer((player, info) -> {
            FriendPlugin.instance().api().exit();

            player.sendMessage(FriendPlugin.prefix().append(Component.text("Alle Daten wurden gespeichert. (plugins/surf-friends-paper/config.yml)")));
        });
    }
}
