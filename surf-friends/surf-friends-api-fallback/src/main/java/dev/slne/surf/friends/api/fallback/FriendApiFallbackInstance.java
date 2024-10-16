package dev.slne.surf.friends.api.fallback;

import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.api.fallback.communication.CommunicationListener;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import java.nio.file.Path;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Services;
import org.slf4j.Logger;

@Plugin(
    id = "surf-friends-api-fallback",
    name = "SurfFriendsApiFallback",
    authors = {"SLNE Development", "TheBjoRedCraft"},
    version = "1.21-1.0.0-SNAPSHOT"
)

@Getter
@Accessors(fluent = true)
public class FriendApiFallbackInstance {
  @Getter
  private static FriendApiFallbackInstance instance;
  private final FriendApi friendApi;
  private final Logger logger;
  private final ProxyServer proxy;
  private final Path dataDirectory;

  @Inject
  public FriendApiFallbackInstance(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
    instance = this;

    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = dataDirectory;
    this.friendApi = Services.serviceWithFallback(FriendApi.class).orElse(null);
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    friendApi.init();

    proxy.getChannelRegistrar().register(CommunicationListener.COMMUNICATION_FRIENDS);
    proxy.getChannelRegistrar().register(CommunicationListener.COMMUNICATION_REQUESTS);
    proxy.getChannelRegistrar().register(CommunicationListener.COMMUNICATION_SERVER);

    info("Successfully enabled.");
  }

  @Subscribe
  public void onShutdown(ProxyShutdownEvent event){
    friendApi.exit();

    info("Successfully disabled.");
  }

  public static void error(String message){
    FriendApiFallbackInstance.instance().proxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.RED)));
  }

  public static void warn(String message){
    FriendApiFallbackInstance.instance().proxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.YELLOW)));
  }

  public static void info(String message){
    FriendApiFallbackInstance.instance().proxy().getConsoleCommandSource().sendMessage(FriendCore.prefix().append(Component.text(message).color(PluginColor.LIGHT_GREEN)));
  }
}
