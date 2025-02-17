package dev.slne.surf.social.chat.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

/**
 * A class that contains all the colors used in the Surf system. This class is used to provide a
 * uniform appearance across all Surf plugins.
 */
object Colors {
    // -------------------- Surf Colors -------------------- //
    /**
     * PRIMARY color (#3b92d1). Generally not used in our system. However, an example of its use could
     * be for titles, subtitles, etc.
     */
    val PRIMARY: TextColor = TextColor.color(0x3b92d1)

    /**
     * SECONDARY color (#5b5b5b). Also seldom used in our system. Could be used for elements like
     * subtitles.
     */
    val SECONDARY: TextColor = TextColor.color(0x5b5b5b)

    /**
     * INFO color (#40d1db). Used to inform the user about a specific situation. Typically, it's not a
     * follow-up to a user action.
     */
    val INFO: TextColor = TextColor.color(0x40d1db)

    /**
     * SUCCESS color (#65ff64). Indicates a positive outcome of an action performed by the user.
     */
    val SUCCESS: TextColor = TextColor.color(0x65ff64)

    /**
     * WARNING color (#f9c353). Used as a direct warning to the user.
     */
    val WARNING: TextColor = TextColor.color(0xf9c353)

    /**
     * ERROR (or DANGER) color (#ee3d51). Represents error messages directed at the user.
     */
    val ERROR: TextColor = TextColor.color(0xee3d51)

    /**
     * VARIABLE_KEY color (#3b92d1). Mainly used as a key in listings.
     */
    val VARIABLE_KEY: TextColor = INFO

    /**
     * VARIABLE_VALUE color (#f9c353). Primarily used in listings and chat messages as a variable.
     */
    val VARIABLE_VALUE: TextColor = WARNING

    /**
     * SPACER color (GRAY). Used for various forms of spacers.
     */
    val SPACER: NamedTextColor = NamedTextColor.GRAY

    /**
     * DARK_SPACER color (DARK_GRAY). Used for dark spacers.
     */
    val DARK_SPACER: NamedTextColor = NamedTextColor.DARK_GRAY

    /**
     * PREFIX color (#3b92d1). Used for the color of every prefix to provide a uniform appearance.
     */
    val PREFIX_COLOR: TextColor = PRIMARY

    // -------------------- Default Colors -------------------- //
    /**
     * The prefix for all Surf plugins
     */
    val PREFIX: Component = Component.text(">> ", DARK_SPACER)
        .append(Component.text("SC", PREFIX_COLOR))
        .append(Component.text(" | ", DARK_SPACER))

    /**
     * Represents the color black.
     */
    val BLACK: NamedTextColor = NamedTextColor.BLACK

    /**
     * Represents the color dark blue.
     */
    val DARK_BLUE: NamedTextColor = NamedTextColor.DARK_BLUE

    /**
     * Represents the color dark green.
     */
    val DARK_GREEN: NamedTextColor = NamedTextColor.DARK_GREEN

    /**
     * Represents the named text color DARK_AQUA.
     */
    val DARK_AQUA: NamedTextColor = NamedTextColor.DARK_AQUA

    /**
     * Represents the dark red color.
     */
    val DARK_RED: NamedTextColor = NamedTextColor.DARK_RED

    /**
     * Represents the dark purple named text color.
     */
    val DARK_PURPLE: NamedTextColor = NamedTextColor.DARK_PURPLE

    /**
     * The GOLD color for naming text.
     */
    val GOLD: NamedTextColor = NamedTextColor.GOLD

    /**
     * Represents the named text color "GRAY".
     */
    val GRAY: NamedTextColor = NamedTextColor.GRAY

    /**
     * Represents the named text color "DARK_GRAY".
     */
    val DARK_GRAY: NamedTextColor = NamedTextColor.DARK_GRAY

    /**
     * Represents the named text color "BLUE".
     */
    val BLUE: NamedTextColor = NamedTextColor.BLUE

    /**
     * Represents the named text color "GREEN".
     */
    val GREEN: NamedTextColor = NamedTextColor.GREEN

    /**
     * Represents the named text color "AQUA".
     */
    val AQUA: NamedTextColor = NamedTextColor.AQUA

    /**
     * Represents the named text color "RED".
     */
    val RED: NamedTextColor = NamedTextColor.RED

    /**
     * Represents the color light purple.
     */
    val LIGHT_PURPLE: NamedTextColor = NamedTextColor.LIGHT_PURPLE

    /**
     * Represents the color yellow.
     */
    val YELLOW: NamedTextColor = NamedTextColor.YELLOW

    /**
     * Represents the color white.
     */
    val WHITE: NamedTextColor = NamedTextColor.WHITE
}
