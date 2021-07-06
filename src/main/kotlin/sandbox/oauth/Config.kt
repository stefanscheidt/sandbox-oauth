package sandbox.oauth


enum class ConfigKey(val default: String) {
    CLIENT_ID("not-set"),
    CLIENT_SECRET("not-set"),
    OAUTH_URI("https://github.com/login/oauth"),
    OPEN_BROWSER("open")
}

class Config(vararg entries: Pair<ConfigKey, String>) {
    private val config: Map<ConfigKey, String> = entries.toMap()

    operator fun get(key: ConfigKey): String =
        config[key] ?: key.default
}
