package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.velocity.VelocityInstance;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public class FriendAcceptCommand extends CommandAPICommand {
    public FriendAcceptCommand(String name) {
        super(name);

        withArguments(new StringArgument("player"));

        executesPlayer((player, info)-> {
            Optional<Player> optionalPlayer = VelocityInstance.instance().proxy().getPlayer((String) info.getUnchecked("player"));

            if(optionalPlayer.isEmpty()){
                player.sendMessage(FriendCore.prefix().append(Component.text("Der Spieler wurde nicht gefunden.").color(PluginColor.RED)));
                return;
            }



            UUID target = optionalPlayer.get().getUniqueId();
            VelocityInstance.instance().friendApi().acceptFriendRequest(player.getUniqueId(), target);
        });
    }
}
