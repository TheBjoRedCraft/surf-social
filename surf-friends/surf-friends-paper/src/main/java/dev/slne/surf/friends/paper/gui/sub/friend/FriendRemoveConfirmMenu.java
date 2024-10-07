package dev.slne.surf.friends.paper.gui.sub.friend;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.paper.PaperInstance;
import dev.slne.surf.friends.paper.gui.FriendMenu;
import dev.slne.surf.friends.velocity.VelocityFriendApiProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class FriendRemoveConfirmMenu extends FriendMenu {

  public FriendRemoveConfirmMenu(String name) {
    super(5, "Bitte bestätige.");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    StaticPane navigation = new StaticPane(0, 4, 9, 1, Priority.HIGH);
    OutlinePane left = new OutlinePane(1, 2, 1, 1);
    OutlinePane mid = new OutlinePane(4, 2, 1, 1);

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    mid.addItem(build(new ItemBuilder(Material.PLAYER_HEAD).setName(Component.text("Möchtest du " + name + " wirklich enfernen?")).setSkullOwner(offlinePlayer)));

    left.addItem(build(new ItemBuilder(Material.GREEN_WOOL).setName(Component.text("Bestätigen").color(PluginColor.LIGHT_GREEN)), event -> {
      VelocityFriendApiProvider.get().removeFriend(event.getWhoClicked().getUniqueId(), offlinePlayer.getUniqueId());
      VelocityFriendApiProvider.get().removeFriend(offlinePlayer.getUniqueId(), event.getWhoClicked().getUniqueId());

      new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    navigation.addItem(build(new ItemBuilder(Material.BARRIER)
        .setName(Component.text("Zurück")
            .color(PluginColor.RED)), event ->
        new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked())), 4, 0);


    addPane(header);
    addPane(footer);
    addPane(mid);
    addPane(left);
  }
}
