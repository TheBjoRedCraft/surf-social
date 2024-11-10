package dev.slne.surf.friends.menu.sub.friend;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.menu.FriendMainMenu;
import dev.slne.surf.friends.menu.FriendMenu;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class FriendFriendsMenu extends FriendMenu {
  public FriendFriendsMenu(UUID player) {
    super(5, "Deine Freunde");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    PaginatedPane pages = new PaginatedPane(1, 1, 9, 3, Priority.HIGH);
    StaticPane navigation = new StaticPane(0, 4, 9, 1, Priority.HIGH);

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    pages.populateWithItemStacks(this.getFriendItems(player));

    pages.setOnClick(event -> {
      if(event.getCurrentItem() == null){
        return;
      }

      ItemMeta meta = event.getCurrentItem().getItemMeta();

      new FriendSingleMenu(meta.getDisplayName()).show(event.getWhoClicked());
    });

    navigation.addItem(build(new ItemBuilder(Material.RED_WOOL).setName(Component.text("Vorherige Seite").color(PluginColor.RED)), event -> {
      if (pages.getPage() > 0) {
        pages.setPage(pages.getPage() - 1);

        update();
      }
    }), 0, 0);

    navigation.addItem(build(new ItemBuilder(Material.GREEN_WOOL).setName(Component.text("Nächste Seite").color(PluginColor.LIGHT_GREEN)), event -> {
      if (pages.getPage() < pages.getPages() - 1) {
        pages.setPage(pages.getPage() + 1);
        update();
      }
    }), 8, 0);

    navigation.addItem(build(new ItemBuilder(Material.BARRIER).setName(Component.text("Zurück").color(PluginColor.RED)), event -> new FriendMainMenu().show(event.getWhoClicked())), 4, 0);


    addPane(header);
    addPane(footer);
    addPane(navigation);
    addPane(pages);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }


  private ObjectList<ItemStack> getFriendItems(UUID player){
    ObjectList<ItemStack> stacks = new ObjectArrayList<>();

    FriendManager.instance().getFriends(player).thenAccept(friends -> {
      for (UUID friend : friends) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(friend);

        stacks.add(new ItemBuilder(Material.PLAYER_HEAD).setName(offlinePlayer.getName()).setSkullOwner(offlinePlayer.getName()).build());
      }
    });

    return stacks;
  }
}
