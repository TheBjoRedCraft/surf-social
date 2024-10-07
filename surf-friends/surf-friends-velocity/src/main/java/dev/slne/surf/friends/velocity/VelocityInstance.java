package dev.slne.surf.friends.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIVelocityConfig;
import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.velocity.command.FriendCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendAddCommand;
import java.nio.file.Path;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

@Plugin(
    id = "surf-friends",
    name = "SurfFriends",
    authors = {"TheBjoRedCraft", "SLNE Development"},
    version = "1.21-2.1.0-SNAPSHOT"
)
@Getter
public class VelocityInstance {
  @Getter
  private static VelocityInstance instance;
  private final Logger logger;
  private final ProxyServer proxy;
  private final Path dataDirectory;

  private final FriendApi api = VelocityFriendApiProvider.get();

  @Inject
  public VelocityInstance(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = dataDirectory;

    instance = this;

    CommandAPI.onLoad(new CommandAPIVelocityConfig(proxy, this));
    proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.create("surf-friends", "main"));

    new FriendCommand("friend").register();
    new FriendCommand("friends").register();

    new FriendAddCommand("fa").register();

    info("Loading SurfSocial/SurfFriends:Velocity");
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    api.init();

    CommandAPI.onEnable();

    info("Successfully enabled.");
  }

  @Subscribe
  public void onShutdown(ProxyShutdownEvent event){
    api.exit();

    CommandAPI.onDisable();

    info("Successfully disabled.");
  }

  public void openMenu(Player player, String menu) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF(menu);
    player.getCurrentServer().ifPresent(serverConnection -> {
      serverConnection.sendPluginMessage(MinecraftChannelIdentifier.create("surf-friends", "main"), out.toByteArray());
    });
  }


  public static void error(String message){
    VelocityInstance.getInstance().getProxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.RED)));
  }

  public static void warn(String message){
    VelocityInstance.getInstance().getProxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.YELLOW)));
  }

  public static void info(String message){
    VelocityInstance.getInstance().getProxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.LIGHT_GREEN)));
  }
}
