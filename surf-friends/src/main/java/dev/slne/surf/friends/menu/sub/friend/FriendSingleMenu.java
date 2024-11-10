package dev.slne.surf.friends.menu.sub.friend;

import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.menu.FriendMenu;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;


public class FriendSingleMenu extends FriendMenu {
  public FriendSingleMenu(String name) {
    super(5, name);

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    StaticPane navigation = new StaticPane(0, 4, 9, 1, Priority.HIGH);

    OutlinePane mid = new OutlinePane(4, 2, 1, 1);
    OutlinePane right = new OutlinePane(7, 2, 1, 1);

    Label remove = new Label(1, 2, 1, 1, Font.OAK_PLANKS);

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
    Date date = new Date(offlinePlayer.getLastSeen());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    navigation.addItem(build(new ItemBuilder(Material.BARRIER).setName(Component.text("Zurück").color(PluginColor.RED)), event -> new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked())), 4, 0);
    mid.addItem(build(new ItemBuilder(Material.PLAYER_HEAD).setName(name).setSkullOwner(offlinePlayer)));

    remove.setText("-", (c, stack) -> {
      ItemMeta meta = stack.getItemMeta();

      meta.displayName(Component.text("Entfernen").color(PluginColor.RED));
      stack.setItemMeta(meta);

      return new GuiItem(stack);
    });
      if (offlinePlayer.isOnline()) {
        right.addItem(build(new ItemBuilder(Material.ENDER_PEARL)
            .setName(Component.text("Nachspringen").color(PluginColor.LIGHT_BLUE))
            .addLoreLine(Component.text("Server: N/A").decoration(TextDecoration.ITALIC, State.FALSE))
            .setSkullOwner(Bukkit.getOfflinePlayer(name))));

      }else{
        right.addItem(build(new ItemBuilder(Material.ENDER_PEARL)
            .setName(Component.text("Der Spieler ist offline.").color(PluginColor.LIGHT_BLUE))
            .addLoreLine(Component.text("Zuletzt gesehen: " + dateFormat.format(date))
                .decoration(TextDecoration.ITALIC, State.FALSE))
            .setSkullOwner(Bukkit.getOfflinePlayer(name))));
      }

    remove.setOnClick(event -> {
      new FriendRemoveConfirmMenu(name).show(event.getWhoClicked());
    });

    addPane(header);
    addPane(footer);
    addPane(navigation);
    addPane(remove);

    addPane(right);
    addPane(mid);

    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
