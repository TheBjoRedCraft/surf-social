package dev.slne.surf.social.friend.service

import com.electronwill.nightconfig.core.file.FileConfig
import java.nio.file.Path

class ConfigService(dataDirectory: Path) {
    private val configPath: Path = dataDirectory.resolve("config.toml")
    private var config: FileConfig? = null

    fun loadConfig() {
        config = FileConfig.of(configPath)

        val config = this.config ?: return

        config.load()
    }

    fun saveConfig() {
        if (config != null) {
            config!!.save()
        }
    }

    fun createDefaultConfig() {

        configPath.toFile().parentFile.mkdirs()
        if (!configPath.toFile().exists()) {
            val config = FileConfig.of(configPath)

            config.add("database.url", "jdbc:mysql://localhost:3306/database")
            config.add("database.username", "username")
            config.add("database.password", "password")
            config.save()
        }
    }

    fun getValue(key: String?): String? {
        if (config != null) {
            return config!!.get(key)
        }
        return null
    }

    fun setValue(key: String?, value: String?) {
        if (config != null) {
            config!!.set<Any>(key, value)
        }
    }
}