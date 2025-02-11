package dev.slne.surf.social.chat.service;

  import dev.slne.surf.social.chat.SurfChat;
  import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
  import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
  import it.unimi.dsi.fastutil.objects.ObjectArraySet;
  import it.unimi.dsi.fastutil.objects.ObjectSet;
  import lombok.Getter;
  import lombok.extern.slf4j.Slf4j;
  import net.kyori.adventure.text.Component;
  import net.kyori.adventure.text.format.NamedTextColor;
  import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
  import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
  import org.bukkit.configuration.file.FileConfiguration;
  import org.bukkit.configuration.file.YamlConfiguration;

  import java.io.File;
  import java.io.IOException;
  import java.util.UUID;
  import java.util.concurrent.ConcurrentHashMap;
  import java.util.concurrent.TimeUnit;
  import java.util.regex.Pattern;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Slf4j
  @Getter
  public class ChatFilterService {
    @Getter
    private static final ChatFilterService instance = new ChatFilterService();
    private final ObjectSet<String> blockedWords = new ObjectArraySet<>();
    private final ObjectSet<String> allowedDomains = new ObjectArraySet<>();
    private final ObjectSet<Pattern> blockedPatterns = new ObjectArraySet<>();
    private final ComponentLogger logger = ComponentLogger.logger(this.getClass());
    private final ConcurrentHashMap<UUID, Long> rateLimit = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> messageCount = new ConcurrentHashMap<>();
    private static final long TIME_FRAME = TimeUnit.SECONDS.toMillis(10);
    private static final int MESSAGE_LIMIT = 3;

    public void loadBlockedWords() {
      File file = new File(SurfChat.getInstance().getDataFolder(), "blocked.yml");
      long start = System.currentTimeMillis();

      if (!file.exists()) {
        try {
          file.getParentFile().mkdirs();
          file.createNewFile();
        } catch (IOException e) {
          logger.error("Failed to create blocked.yml file", e);
        }
      }

      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      ObjectSet<String> words = new ObjectArraySet<>(config.getStringList("blocked-words"));

      this.blockedWords.clear();
      this.blockedWords.addAll(words);

      this.blockedPatterns.clear();
      words.forEach(word -> {
        Object2ObjectMap<Character, String> replacements = new Object2ObjectOpenHashMap<>();

        replacements.put('a', "[a@4]?");
        replacements.put('b', "[b8]?");
        replacements.put('c', "c?");
        replacements.put('d', "d?");
        replacements.put('e', "[e3]?");
        replacements.put('f', "f?");
        replacements.put('g', "[g9]?");
        replacements.put('h', "h?");
        replacements.put('i', "[i1!]?");
        replacements.put('j', "j?");
        replacements.put('k', "k?");
        replacements.put('l', "[l1]?");
        replacements.put('m', "m?");
        replacements.put('n', "n?");
        replacements.put('o', "[o0]?");
        replacements.put('p', "p?");
        replacements.put('q', "q?");
        replacements.put('r', "r?");
        replacements.put('s', "[s5]?");
        replacements.put('t', "[t7]?");
        replacements.put('u', "u?");
        replacements.put('v', "v?");
        replacements.put('w', "w?");
        replacements.put('x', "x?");
        replacements.put('y', "y?");
        replacements.put('z', "[z2]?");

        StringBuilder regexBuilder = new StringBuilder();
        for (char c : word.toCharArray()) {
          regexBuilder.append(replacements.getOrDefault(c, c + "?"));
        }

        String regex = regexBuilder.toString();
        blockedPatterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
      });

      logger.info(Component.text("Loaded ", NamedTextColor.GREEN)
          .append(Component.text(blockedWords.size(), NamedTextColor.GOLD))
          .append(Component.text(" blocked words in ", NamedTextColor.GREEN))
          .append(Component.text(System.currentTimeMillis() - start, NamedTextColor.GOLD))
          .append(Component.text("ms", NamedTextColor.GREEN)));
    }

    public boolean containsBlocked(Component message) {
      return blockedPatterns.stream().anyMatch(pattern -> pattern.matcher(PlainTextComponentSerializer.plainText().serialize(message)).find());
    }

    public boolean containsLink(Component message) {
      String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
      String urlPattern = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
      Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
      var matcher = pattern.matcher(plainMessage);

      while (matcher.find()) {
        String domain = matcher.group(3);
        if (allowedDomains.stream().noneMatch(domain::endsWith)) {
          return true;
        }
      }
      return false;
    }

    public boolean isSpamming(UUID uuid) {
      long currentTime = System.currentTimeMillis();

      rateLimit.putIfAbsent(uuid, currentTime);
      messageCount.putIfAbsent(uuid, 0);

      long lastMessageTime = rateLimit.get(uuid);
      int count = messageCount.get(uuid);

      if (currentTime - lastMessageTime < TIME_FRAME) {
        if (count >= MESSAGE_LIMIT) {
          return true;
        } else {
          messageCount.put(uuid, count + 1);
          return false;
        }
      } else {
        rateLimit.put(uuid, currentTime);
        messageCount.put(uuid, 1);
        return false;
      }
    }
  }