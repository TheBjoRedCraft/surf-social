package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern


object ChatFilterService {
    private val blockedWords: ObjectSet<String> = ObjectArraySet()
    private val allowedDomains: ObjectSet<String> = ObjectArraySet()
    private val blockedPatterns: ObjectSet<Pattern> = ObjectArraySet()

    private val logger: ComponentLogger = ComponentLogger.logger(this.javaClass)
    private val rateLimit: ConcurrentHashMap<UUID, Long> = ConcurrentHashMap<UUID, Long>()
    private val messageCount: ConcurrentHashMap<UUID, Int> = ConcurrentHashMap<UUID, Int>()

    private val VALID_CHARACTERS_PATTERN: Pattern = Pattern.compile("^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~-äöü@ ]*$")
    private val TIME_FRAME: Long = java.util.concurrent.TimeUnit.SECONDS.toMillis(10)
    private const val MESSAGE_LIMIT = 5

    fun loadBlockedWords() {
        val file = File(SurfChat.instance.dataFolder, "blocked.txt")
        val start: Long = System.currentTimeMillis()

        if (!file.exists()) {
            file.getParentFile().mkdirs()
            SurfChat.instance.saveResource("blocked.txt", false)
        }

        blockedWords.clear()
        blockedPatterns.clear()

        try {
            BufferedReader(FileReader(file)).use { reader ->
                val lines: List<String> = reader.lines()
                    .map { obj: String -> obj.trim { it <= ' ' } }
                    .filter { word: String -> word.isNotEmpty() }
                    .toList()

                blockedWords.addAll(lines)
                blockedPatterns.addAll(
                    lines.parallelStream()
                        .map { word: String -> this.getRegex(word) }
                        .map { regex: String -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE) }
                        .toList()
                )
            }
        } catch (e: java.io.IOException) {
            logger.error("Failed to read blocked.txt file", e)
        }

        val duration: Long = System.currentTimeMillis() - start
        logger.info(Component.text("Loaded ", NamedTextColor.GREEN)
                .append(Component.text(blockedWords.size, NamedTextColor.GOLD))
                .append(Component.text(" blocked words and their regexes in ", NamedTextColor.GREEN))
                .append(Component.text(duration, NamedTextColor.GOLD))
                .append(Component.text("ms", NamedTextColor.GREEN))
        )
    }


    private fun getRegex(word: String): String {
        val replacements: Object2ObjectMap<Char, String> = Object2ObjectOpenHashMap()
        replacements['a'] = "[a@4]"
        replacements['b'] = "[b8]"
        replacements['c'] = "c"
        replacements['d'] = "d"
        replacements['e'] = "[e3]"
        replacements['f'] = "f"
        replacements['g'] = "[g9]"
        replacements['h'] = "h"
        replacements['i'] = "[i1!]"
        replacements['j'] = "j"
        replacements['k'] = "k"
        replacements['l'] = "[l1]"
        replacements['m'] = "m"
        replacements['n'] = "n"
        replacements['o'] = "[o0]"
        replacements['p'] = "p"
        replacements['q'] = "q"
        replacements['r'] = "r"
        replacements['s'] = "[s5]"
        replacements['t'] = "[t7]"
        replacements['u'] = "u"
        replacements['v'] = "v"
        replacements['w'] = "w"
        replacements['x'] = "x"
        replacements['y'] = "y"
        replacements['z'] = "[z2]"

        val regexBuilder: StringBuilder = StringBuilder()
        for (c in word.toCharArray()) {
            regexBuilder.append(replacements.getOrDefault(c, c.toString() + ""))
        }

        return regexBuilder.toString()
    }

    fun containsBlocked(message: Component): Boolean {
        return blockedPatterns.stream().anyMatch { pattern: Pattern ->
            pattern.matcher(PlainTextComponentSerializer.plainText().serialize(message)).find()
        }
    }

    fun containsLink(message: Component): Boolean {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)
        val urlPattern = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
        val pattern: Pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(plainMessage)

        while (matcher.find()) {
            val domain: String = matcher.group(3)
            if (allowedDomains.stream().noneMatch { suffix: String -> domain.endsWith(suffix) }) {
                return true
            }
        }
        return false
    }

    fun isValidInput(input: String): Boolean {
        return VALID_CHARACTERS_PATTERN.matcher(input).matches()
    }

    fun isSpamming(uuid: UUID): Boolean {
        val currentTime: Long = System.currentTimeMillis()

        rateLimit.putIfAbsent(uuid, currentTime)
        messageCount.putIfAbsent(uuid, 0)

        val lastMessageTime: Long = rateLimit[uuid] ?: 0
        val count: Int = messageCount[uuid] ?: 0

        if (currentTime - lastMessageTime < TIME_FRAME) {
            if (count >= MESSAGE_LIMIT) {
                return true
            } else {
                messageCount[uuid] = count + 1
                return false
            }
        } else {
            rateLimit[uuid] = currentTime
            messageCount[uuid] = 1
            return false
        }
    }
}