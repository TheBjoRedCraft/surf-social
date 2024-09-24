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
import dev.slne.surf.friends.velocity.command.FriendCommand;
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendAddCommand;
import java.nio.file.Path;
import java.util.logging.Logger;

import lombok.Getter;
import net.kyori.adventure.util.Services;

@Plugin(
    id = "surf-friends",
    name = "SurfFriends",
    authors = "TheBjoRedCraft and SLNE Development",
    version = "1.21-2.1.0-SNAPSHOT"
)
@Getter
public class VelocityInstance {
  @Getter
  private static VelocityInstance instance;
  private final Logger logger;
  private final ProxyServer proxy;
  private final Path dataDirectory;

  private final FriendApi api = Services.serviceWithFallback(FriendApi.class).orElseThrow(() -> new Error("FriendApi not available"));
  private final MinecraftChannelIdentifier channel = MinecraftChannelIdentifier.create("surf-friends", "main");

  @Inject
  public VelocityInstance(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = dataDirectory;
    instance = this;

    CommandAPI.onLoad(new CommandAPIVelocityConfig(proxy, this));
    proxy.getChannelRegistrar().register(channel);

    new FriendCommand("friend").register();
    new FriendCommand("friends").register();

    new FriendAddCommand("fa").register();
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    CommandAPI.onEnable();

    api.init();
  }

  @Subscribe
  public void onShutdown(ProxyShutdownEvent event){
    CommandAPI.onDisable();

    api.exit();
    //Shutdown
  }

  public void openMenu(Player player, String menu) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF(menu);
    player.getCurrentServer().ifPresent(serverConnection -> {
      serverConnection.sendPluginMessage(channel, out.toByteArray());
    });
  }
}
