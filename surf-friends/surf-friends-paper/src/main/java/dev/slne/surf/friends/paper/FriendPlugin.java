package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.command.FriendCommand;
import dev.slne.surf.friends.paper.impl.LocalFileBasedFriendApi;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class FriendPlugin extends JavaPlugin {
    private final LocalFileBasedFriendApi api = new LocalFileBasedFriendApi();

    @Getter
    private static final Component prefix = Component.text(">> ").color(NamedTextColor.GRAY)
        .append(Component.text("Friends").color(NamedTextColor.AQUA))
        .append(Component.text(" |").color(NamedTextColor.DARK_GRAY))
        .append(Component.text(" ").color(NamedTextColor.WHITE));

    @Getter
    private static final FriendLogger logger = new FriendLogger();


    @Override
    public void onLoad() {
        //Load
    }

    @Override
    public void onEnable() {
        registerCommands();

        saveDefaultConfig();

        api.init();

        logger.sender = Bukkit.getConsoleSender();
    }

    @Override
    public void onDisable() {
        api.exit();
    }

    private void registerCommands(){
        new FriendCommand("friend").register();
        new FriendCommand("friends").register();
    }

    public static FriendPlugin instance(){
        return getPlugin(FriendPlugin.class);
    }
}
