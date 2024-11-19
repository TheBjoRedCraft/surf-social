package dev.slne.surf.friends.menu.sub.friend;

import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.menu.FriendMenu;
import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
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
    mid.addItem(build(new ItemBuilder(Material.PLAYER_HEAD)
        .setName(Component.text(name, PluginColor.BLUE_LIGHT))
        .setSkullOwner(offlinePlayer.getName())));

    remove.setText("-", (c, stack) -> {
      ItemBuilder builder = new ItemBuilder(stack);

      builder.setName(Component.text("Entfernen"));
      builder.addLoreLine(Component.text("Freund/in entfernen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("Hier kannst du diesen Spieler", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("aus deiner Freundesliste entfernen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));

      return this.build(builder);
    });
      if (offlinePlayer.isOnline()) {
        right.addItem(build(new ItemBuilder(Material.ENDER_PEARL)
            .setName(Component.text("Nachspringen").color(PluginColor.BLUE_LIGHT))
            .addLoreLine(Component.text("Nachspringen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Hier kannst du diesem", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Spieler nachjoinen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Er ist aktuell auf ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(FriendManager.instance().getServer(offlinePlayer.getUniqueId()), PluginColor.GOLD).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(" online.", PluginColor.LIGHT_GRAY)).decoration(TextDecoration.ITALIC, false))
            .addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)
            .setSkullOwner(name)));

      }else{
        right.addItem(build(new ItemBuilder(Material.ENDER_PEARL)
            .setName(Component.text("Nachspringen").color(PluginColor.BLUE_LIGHT))
            .addLoreLine(Component.text("Nachspringen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Dieser Spieler ist aktuell offline.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Du kannst ihm nicht nachspringen!", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Er war zuletzt am ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(dateFormat.format(date), PluginColor.GOLD).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(" online.", PluginColor.LIGHT_GRAY)).decoration(TextDecoration.ITALIC, false))
            .addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)
            .setSkullOwner(name)));
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
