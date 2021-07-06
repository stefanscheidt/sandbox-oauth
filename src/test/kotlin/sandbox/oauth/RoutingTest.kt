package sandbox.oauth

import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test


class RoutingTest {

    @Test
    internal fun `call ping endpoint`() {
        App().routes().invoke(Request(GET, "/ping")) shouldBe Response(OK).body("OK")
    }

}