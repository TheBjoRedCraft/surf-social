package dev.slne.surf.friends.menu.sub.settings;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.ToggleButton;

import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.menu.FriendMainMenu;
import dev.slne.surf.friends.menu.FriendMenu;

import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import java.util.UUID;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

import org.bukkit.Material;

public class FriendSettingsMenu extends FriendMenu {

  public FriendSettingsMenu(UUID player) {
    super(5, "Freundeseinstellungen");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    StaticPane navigation = new StaticPane(0, 4, 9, 1, Priority.HIGH);
    ToggleButton mid = new ToggleButton(4, 2, 1, 1);

    Sound sound = Sound.sound(Key.key("minecraft:block.note_block.bit"), Source.MASTER, 1, 0);

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    mid.setEnabledItem(build(new ItemBuilder(Material.WRITABLE_BOOK)
        .addLoreLine(Component.text("Aktuell ist diese Einstellung aktiviert.").decoration(TextDecoration.ITALIC, State.FALSE))
        .setName(Component.text("Freundesanfragen").color(PluginColor.BLUE_LIGHT)), event -> FriendManager.instance().toggle(player)));

    mid.setDisabledItem(build(new ItemBuilder(Material.WRITABLE_BOOK)
        .addLoreLine(Component.text("Aktuell ist diese Einstellung deaktiviert.").decoration(TextDecoration.ITALIC, State.FALSE))
        .setName(Component.text("Freundesanfragen").color(PluginColor.BLUE_LIGHT)), event -> FriendManager.instance().toggle(player)));

    navigation.addItem(build(new ItemBuilder(Material.BARRIER).setName(Component.text("Zurück").color(PluginColor.RED)), event -> new FriendMainMenu().show(event.getWhoClicked())), 4, 0);

    mid.setOnClick(event -> event.getWhoClicked().playSound(sound));

    addPane(header);
    addPane(footer);
    addPane(navigation);
    addPane(mid);
  }
}
