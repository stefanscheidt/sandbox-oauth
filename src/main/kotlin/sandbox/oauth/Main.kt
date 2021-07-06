package sandbox.oauth

import mu.KotlinLogging
import org.http4k.server.SunHttp
import sandbox.oauth.ConfigKey.CLIENT_ID
import sandbox.oauth.ConfigKey.CLIENT_SECRET
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main() {
    if (System.getenv(CLIENT_ID.name) == null || System.getenv(CLIENT_SECRET.name) == null) {
        println("env vars CLIENT_ID and CLIENT_SECRET must be set")
        exitProcess(1)
    }

    val config = Config(
        CLIENT_ID to System.getenv(CLIENT_ID.name),
        CLIENT_SECRET to System.getenv(CLIENT_SECRET.name)
    )
    val server = App(config).asServer(SunHttp(8080)).start()
    logger.info { "Server starter on port ${server.port()}" }
}
