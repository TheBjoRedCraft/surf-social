package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;
import net.kyori.adventure.text.Component;


public class FriendToggleCommand extends CommandAPICommand {
    public FriendToggleCommand(String name) {
        super(name);

        executesPlayer((player, info)-> {
            if(FriendManager.instance().toggle(player.getUniqueId())){
                player.sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text("Du hast Freundschaftsanfragen nun aktiviert.")));
            } else {
                player.sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text("Du hast Freundschaftsanfragen nun deaktiviert.")));
            }
        });
    }
}
