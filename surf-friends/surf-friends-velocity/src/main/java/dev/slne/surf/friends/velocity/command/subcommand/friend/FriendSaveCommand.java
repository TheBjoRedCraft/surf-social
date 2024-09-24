package dev.slne.surf.friends.velocity.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.FriendCore;

import net.kyori.adventure.text.Component;

public class FriendSaveCommand extends CommandAPICommand {
    public FriendSaveCommand(String name) {
        super(name);

        withPermission("surf-social.friends.friend.command.save");

        executesPlayer((player, info) -> {
            FriendApi.get().exit();

            player.sendMessage(FriendCore.prefix().append(Component.text("Alle Daten wurden gespeichert.")));
        });
    }
}
