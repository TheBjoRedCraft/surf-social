package dev.slne.surf.friends.menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import dev.slne.surf.friends.core.util.ItemBuilder;
import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FriendMenu extends ChestGui {

  public FriendMenu(int rows, @NotNull String title) {
    super(rows, title);
  }


  public GuiItem build(ItemBuilder builder, Consumer<InventoryClickEvent> action){
    return new GuiItem(builder.build(), action);
  }

  public GuiItem build(ItemStack stack, Consumer<InventoryClickEvent> action){
    return new GuiItem(stack, action);
  }

  public GuiItem build(ItemBuilder builder){
    return new GuiItem(builder.build());
  }
}
