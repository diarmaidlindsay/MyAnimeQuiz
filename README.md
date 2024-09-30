# Building

Put the following in a new ./private.properties file:

```properties
CLIENT_ID="your_mal_api_client_id"
```

To get a Client ID, go to the [API panel](https://myanimelist.net/apiconfig) and create a new app
with *App Type* set to `android` and *Redirect URL* set to `com.myanimequiz.auth://callback`