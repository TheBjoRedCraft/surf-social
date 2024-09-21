package dev.slne.surf.friends.paper.gui;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.paper.gui.sub.friend.FriendFriendsMenu;
import dev.slne.surf.friends.paper.gui.sub.request.FriendRequestsMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FriendMainMenu extends FriendMenu {

  public FriendMainMenu() {
    super(5, "Freunde");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);

    OutlinePane settingPane = new OutlinePane(1, 2, 1, 1);
    OutlinePane flPane = new OutlinePane(4, 2, 1, 1);
    OutlinePane frPane = new OutlinePane(7, 2, 1, 1);

    ItemStack settings = new ItemBuilder(Material.DIAMOND_PICKAXE).setName(Component.text("Einstellungen").color(PluginColor.LIGHT_BLUE)).build();
    ItemStack friendList = new ItemBuilder(Material.ENDER_PEARL).setName(Component.text("Freunde").color(PluginColor.LIGHT_BLUE)).build();
    ItemStack friendRequests = new ItemBuilder(Material.PAPER).setName(Component.text("Freundschaftsanfragen").color(PluginColor.LIGHT_BLUE)).build();

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    settingPane.addItem(build(settings, event -> {

    }));

    flPane.addItem(build(friendList, event -> {
      new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    frPane.addItem(build(friendRequests, event -> {
      new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));


    addPane(header);
    addPane(footer);
    addPane(settingPane);
    addPane(flPane);
    addPane(frPane);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
