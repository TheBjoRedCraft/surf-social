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


class ChatFilterService {
    private val blockedWords: ObjectSet<String> = ObjectArraySet()
    private val allowedDomains: ObjectSet<String> = ObjectArraySet()
    private val blockedPatterns: ObjectSet<java.util.regex.Pattern> =
        ObjectArraySet<java.util.regex.Pattern>()

    private val logger: ComponentLogger = ComponentLogger.logger(
        this.javaClass
    )
    private val rateLimit: java.util.concurrent.ConcurrentHashMap<java.util.UUID, Long> =
        java.util.concurrent.ConcurrentHashMap<java.util.UUID, Long>()
    private val messageCount: java.util.concurrent.ConcurrentHashMap<java.util.UUID, Int> =
        java.util.concurrent.ConcurrentHashMap<java.util.UUID, Int>()

    fun loadBlockedWords() {
        val file: java.io.File =
            java.io.File(SurfChat.Companion.getInstance().getDataFolder(), "blocked.txt")
        val start: Long = java.lang.System.currentTimeMillis()

        if (!file.exists()) {
            file.getParentFile().mkdirs()
            SurfChat.Companion.getInstance().saveResource("blocked.txt", false)
        }

        blockedWords.clear()
        blockedPatterns.clear()

        try {
            java.io.BufferedReader(java.io.FileReader(file)).use { reader ->
                val lines: List<String> = reader.lines()
                    .map<String> { obj: String -> obj.trim { it <= ' ' } }
                    .filter { word: String -> !word.isEmpty() }
                    .toList()
                blockedWords.addAll(lines)
                blockedPatterns.addAll(
                    lines.parallelStream()
                        .map<String> { word: String -> this.getRegex(word) }
                        .map<java.util.regex.Pattern> { regex: String? ->
                            java.util.regex.Pattern.compile(
                                regex,
                                java.util.regex.Pattern.CASE_INSENSITIVE
                            )
                        }
                        .toList()
                )
            }
        } catch (e: java.io.IOException) {
            logger.error("Failed to read blocked.txt file", e)
        }

        val duration: Long = java.lang.System.currentTimeMillis() - start
        logger.info(
            Component.text("Loaded ", NamedTextColor.GREEN)
                .append(Component.text(blockedWords.size, NamedTextColor.GOLD))
                .append(
                    Component.text(
                        " blocked words and their regexes in ",
                        NamedTextColor.GREEN
                    )
                )
                .append(Component.text(duration, NamedTextColor.GOLD))
                .append(Component.text("ms", NamedTextColor.GREEN))
        )
    }


    private fun getRegex(word: String): String {
        val replacements: Object2ObjectMap<Char, String> = Object2ObjectOpenHashMap()
        replacements.put('a', "[a@4]")
        replacements.put('b', "[b8]")
        replacements.put('c', "c")
        replacements.put('d', "d")
        replacements.put('e', "[e3]")
        replacements.put('f', "f")
        replacements.put('g', "[g9]")
        replacements.put('h', "h")
        replacements.put('i', "[i1!]")
        replacements.put('j', "j")
        replacements.put('k', "k")
        replacements.put('l', "[l1]")
        replacements.put('m', "m")
        replacements.put('n', "n")
        replacements.put('o', "[o0]")
        replacements.put('p', "p")
        replacements.put('q', "q")
        replacements.put('r', "r")
        replacements.put('s', "[s5]")
        replacements.put('t', "[t7]")
        replacements.put('u', "u")
        replacements.put('v', "v")
        replacements.put('w', "w")
        replacements.put('x', "x")
        replacements.put('y', "y")
        replacements.put('z', "[z2]")

        val regexBuilder: java.lang.StringBuilder = java.lang.StringBuilder()
        for (c in word.toCharArray()) {
            regexBuilder.append(replacements.getOrDefault(c, c.toString() + ""))
        }

        return regexBuilder.toString()
    }

    fun containsBlocked(message: Component): Boolean {
        return blockedPatterns.stream().anyMatch { pattern: java.util.regex.Pattern ->
            pattern.matcher(
                PlainTextComponentSerializer.plainText().serialize(message)
            ).find()
        }
    }

    fun containsLink(message: Component): Boolean {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)
        val urlPattern = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
        val pattern: java.util.regex.Pattern =
            java.util.regex.Pattern.compile(urlPattern, java.util.regex.Pattern.CASE_INSENSITIVE)
        val matcher: java.util.regex.Matcher = pattern.matcher(plainMessage)

        while (matcher.find()) {
            val domain: String = matcher.group(3)
            if (allowedDomains.stream().noneMatch { suffix: String? -> domain.endsWith(suffix) }) {
                return true
            }
        }
        return false
    }

    fun isValidInput(input: String): Boolean {
        return VALID_CHARACTERS_PATTERN.matcher(input).matches()
    }

    fun isSpamming(uuid: java.util.UUID): Boolean {
        val currentTime: Long = java.lang.System.currentTimeMillis()

        rateLimit.putIfAbsent(uuid, currentTime)
        messageCount.putIfAbsent(uuid, 0)

        val lastMessageTime: Long = rateLimit.get(uuid)
        val count: Int = messageCount.get(uuid)

        if (currentTime - lastMessageTime < TIME_FRAME) {
            if (count >= MESSAGE_LIMIT) {
                return true
            } else {
                messageCount.put(uuid, count + 1)
                return false
            }
        } else {
            rateLimit.put(uuid, currentTime)
            messageCount.put(uuid, 1)
            return false
        }
    }

    companion object {
        @Getter
        private val instance = ChatFilterService()
        private val VALID_CHARACTERS_PATTERN: java.util.regex.Pattern =
            java.util.regex.Pattern.compile("^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~-äöü@ ]*$")
        private val TIME_FRAME: Long = java.util.concurrent.TimeUnit.SECONDS.toMillis(10)
        private const val MESSAGE_LIMIT = 5
    }
}