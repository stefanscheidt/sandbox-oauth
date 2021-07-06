package sandbox.oauth

import com.fasterxml.jackson.databind.JsonNode
import mu.KotlinLogging
import org.http4k.client.JavaHttpClient
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.format.Json
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.ServerConfig
import org.http4k.server.asServer
import sandbox.oauth.ConfigKey.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

private val logger = KotlinLogging.logger {}

class App(private val config: Config = Config()) {

    private val httpClient: HttpHandler =
        JavaHttpClient()

    private val json: Json<JsonNode> =
        Jackson

    fun routes(): HttpHandler =
        routes(
            "/ping" bind GET to this::ok,
            "/start" bind GET to this::handleStart,
            "/oauth/callback/code" bind GET to this::handleCode
        )

    fun asServer(serverConfig: ServerConfig): Http4kServer =
        routes().asServer(serverConfig)

    @Suppress("UNUSED_PARAMETER")
    private fun ok(request: Request): Response =
        Response(OK).body("OK")

    private fun handleStart(req: Request): Response {
        authorizationUrl().open()
        return ok(req)
    }

    private fun handleCode(req: Request): Response {
        val user = req.query("code")?.let { code ->
            logger.info { "received code ${code.take(4)}..." }
            val token = exchangeForToken(code)
            logger.info { "exchanged for token ${token.take(4)}..." }
            getUser(token)
        }
        return Response(OK)
            .header("Content-Type", APPLICATION_JSON.value)
            .body(user?.toPrettyString() ?: "{}")
    }

    private fun authorizationUrl(): String {
        val redirectUri = URLEncoder.encode("http://localhost:8080/oauth/callback/code", UTF_8)
        return "${config[OAUTH_URI]}/authorize" +
                "?client_id=${config[CLIENT_ID]}" +
                "&redirect_uri=${redirectUri}"
    }

    private fun accessTokenUrl(code: String): String {
        return "${config[OAUTH_URI]}/access_token" +
                "?client_id=${config[CLIENT_ID]}" +
                "&client_secret=${config[CLIENT_SECRET]}" +
                "&code=$code"
    }

    private fun exchangeForToken(code: String): String {
        val request = Request(POST, accessTokenUrl(code))
            .header("Accept", APPLICATION_JSON.value)
        val responseBody = httpClient.invoke(request).bodyString()
        val responseNode = json.parse(responseBody)
        return responseNode["access_token"].asText()
    }

    private fun getUser(token: String): JsonNode {
        val request = Request(GET, "https://api.github.com/user")
            .header("Authorization", "token $token")
        val responseBody = httpClient.invoke(request).bodyString()
        return json.parse(responseBody)
    }

    private fun String.open() {
        val command = "${config[OPEN_BROWSER]} $this"
        logger.info { "executing command `${command}`" }
        Runtime.getRuntime().exec(command)
    }

}

