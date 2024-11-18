package dev.slne.surf.friends.menu;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import dev.slne.surf.friends.menu.sub.friend.FriendFriendsMenu;
import dev.slne.surf.friends.menu.sub.request.FriendRequestsMenu;
import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FriendMainMenu extends FriendMenu {

  public FriendMainMenu() {
    super(5, "Freunde");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);

    OutlinePane flPane = new OutlinePane(2, 2, 1, 1);
    OutlinePane frPane = new OutlinePane(6, 2, 1, 1);

    ItemStack friendList = new ItemBuilder(Material.ENDER_PEARL)
        .setName(Component.text("Freunde").color(PluginColor.BLUE_LIGHT))
        .addLoreLine(Component.text("Freundesliste", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text("Hier findest du eine Auflistung aller Freunde von dir.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text("Du kannst diesen hier nachjoinen oder sie entfernen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .build();
    ItemStack friendRequests = new ItemBuilder(Material.PAPER)
        .setName(Component.text("Freundschaftsanfragen").color(PluginColor.BLUE_LIGHT))
        .addLoreLine(Component.text("Freundschaftsanfragen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text("Hier findest du eine Auflistung aller offenen Freundschaftsanfragen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .addLoreLine(Component.text("Du kannst diese direkt hier annehmen oder akzeptieren.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
        .build();

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    flPane.addItem(build(friendList, event -> {
      new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    frPane.addItem(build(friendRequests, event -> {
      new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));


    addPane(header);
    addPane(footer);
    addPane(flPane);
    addPane(frPane);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
