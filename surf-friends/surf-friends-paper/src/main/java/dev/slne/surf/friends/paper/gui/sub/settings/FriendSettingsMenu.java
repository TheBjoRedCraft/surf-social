package dev.slne.surf.friends.paper.gui.sub.settings;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import com.github.stefvanschie.inventoryframework.pane.component.ToggleButton;
import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.ItemBuilder;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.paper.PaperInstance;
import dev.slne.surf.friends.paper.gui.FriendMainMenu;
import dev.slne.surf.friends.paper.gui.FriendMenu;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Material;

public class FriendSettingsMenu extends FriendMenu {
  FriendApi api = PaperInstance.instance().api();

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
        .setName(Component.text("Freundesanfragen").color(PluginColor.LIGHT_BLUE)), event -> api.toggle(player)));

    mid.setDisabledItem(build(new ItemBuilder(Material.WRITABLE_BOOK)
        .addLoreLine(Component.text("Aktuell ist diese Einstellung deaktiviert.").decoration(TextDecoration.ITALIC, State.FALSE))
        .setName(Component.text("Freundesanfragen").color(PluginColor.LIGHT_BLUE)), event -> api.toggle(player)));

    navigation.addItem(build(new ItemBuilder(Material.BARRIER)
        .setName(Component.text("Zurück")
            .color(PluginColor.RED)), event ->
        new FriendMainMenu().show(event.getWhoClicked())), 4, 0);

    mid.setOnClick(event -> {
      event.getWhoClicked().playSound(sound);
    });

    addPane(header);
    addPane(footer);
    addPane(navigation);
    addPane(mid);
  }
}
