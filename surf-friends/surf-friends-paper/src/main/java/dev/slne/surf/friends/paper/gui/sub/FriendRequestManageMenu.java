package dev.slne.surf.friends.paper.gui.sub;

import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.paper.gui.FriendMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FriendRequestManageMenu extends FriendMenu {

  public FriendRequestManageMenu(String name) {
    super(5, "Freundschaftsanfrage von " + name);

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    OutlinePane midPane = new OutlinePane(4, 2, 1, 1);

    Label accept = new Label(1, 2, 1, 1, Priority.NORMAL, Font.OAK_PLANKS);
    Label deny = new Label(7, 2, 1, 1, Priority.NORMAL, Font.OAK_PLANKS);

    ItemStack target = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(name).setName(Component.text(name).color(PluginColor.LIGHT_BLUE)).build();

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    accept.setText("✔");
    deny.setText("X");

    midPane.addItem(new GuiItem(target));



    addPane(header);
    addPane(footer);
    addPane(accept);
    addPane(midPane);
    addPane(deny);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
