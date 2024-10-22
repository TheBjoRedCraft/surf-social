package dev.slne.surf.friends.velocity.command.argument;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.slne.surf.friends.velocity.VelocityInstance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerArgument extends StringArgument {
    private PlayerArgument(String nodeName) {
        super(nodeName);
 
        replaceSuggestions(ArgumentSuggestions.stringCollection(info -> VelocityInstance.instance().proxy().getAllPlayers().stream().map(Player::getUsername).toList()));
    }
 
    @Contract("_ -> new")
    public static @NotNull PlayerArgument player(String nodeName) {
        return new PlayerArgument(nodeName);
    }
 
    public static Player getPlayer(String nodeName, @NotNull CommandArguments args) {
        return VelocityInstance.instance().proxy().getPlayer(args.<String>getUnchecked(nodeName)).orElse(null);
    }
}