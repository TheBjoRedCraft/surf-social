package dev.slne.surf.friends.paper.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import dev.slne.surf.friends.core.util.ItemBuilder;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FriendMainGui extends ChestGui {

  public FriendMainGui() {
    super(3, "Freunde");

    OutlinePane header = new OutlinePane(1, 1, 1, 7, Priority.LOW);
    OutlinePane footer = new OutlinePane(1, 1, 1, 7, Priority.LOW);

    header.addItem(build(new ItemBuilder(Material.BLACK_STAINED_GLASS).setName("")));
    footer.addItem(build(new ItemBuilder(Material.BLACK_STAINED_GLASS).setName("")));



    addPane(header);
    addPane(footer);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }



  private GuiItem build(ItemBuilder builder, Consumer<InventoryClickEvent> action){
    return new GuiItem(builder.build(), action);
  }

  private GuiItem build(ItemBuilder builder){
    return new GuiItem(builder.build());
  }
}
