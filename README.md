# sandbox-oauth

Playground for playing around with

* [GitHub's OAuth implementation](https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps)
* [http4k](https://www.http4k.org/)

## Usage

### Configure the GitHub OAuth Client

In the developer settings of the GitHub OAuth app, set the homepage URL to `http://localhost:8080`
and
the [redirect URL](https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps#redirect-urls)
to `http://localhost/oauth/callback`.

### Start the Server

Start the server (on Unix-like systems):

```shell
export CLIENT_ID=<your GitHub OAuth client id>
export CLIENT_SECRET=<your GitHub OAuth client secret>
./gradlew run
```

Remark: Executing

```shell
CLIENT_ID=<client-id>;CLIENT_SECRET=<client-secret>;./gradlew run`
```

in one line should also work but doesn't, and I don't know why yet.

### Start the Flow

Start the flow by calling <http://localhost:8080/start> in your browser.

