package dev.slne.surf.friends.paper;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.paper.command.FriendCommand;
import dev.slne.surf.friends.paper.impl.LocalFileBasedFriendApi;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class FriendsPaperPlugin extends JavaPlugin {
    private final FriendApi api = new LocalFileBasedFriendApi();


    @Override
    public void onLoad() {
        //Load
    }

    @Override
    public void onEnable() {
        registerCommands();

        saveDefaultConfig();

        api.init();
    }

    @Override
    public void onDisable() {
        api.exit();
    }

    private void registerCommands(){
        new FriendCommand("friend").register();
    }

    public static FriendsPaperPlugin instance(){
        return getPlugin(FriendsPaperPlugin.class);
    }
}
