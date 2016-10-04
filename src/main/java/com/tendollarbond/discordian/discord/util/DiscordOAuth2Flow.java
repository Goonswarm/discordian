package com.tendollarbond.discordian.discord.util;

import com.mashape.unirest.http.Unirest;
import com.tendollarbond.discordian.config.Config;

import javaslang.collection.List;
import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Implementation of the OAuth 2 flow supported by Discord.
 *
 * https://discordapp.com/developers/docs/topics/oauth2
 */
@Slf4j
public class DiscordOAuth2Flow {
  final private String TOKEN_URL = "https://discordapp.com/api/oauth2/token";
  final private String AUTHORIZE_URL = "https://discordapp.com/api/oauth2/authorize";
  final private List<String> SCOPES = List.of("identify", "guilds.join");
  final private Config config;

  public DiscordOAuth2Flow(Config config) {
    this.config = config;
  }

  public String getAuthorizationUrl() {
    return Unirest.get(AUTHORIZE_URL)
        .queryString("client_id", config.getClientId())
        .queryString("redirect_uri", config.getRedirectUrl())
        .queryString("response_type", "code")
        .queryString("scope", SCOPES.mkString(" "))
        .getUrl();
  }

  public Try<String> getAccessToken(String code) {
    val optionResponse = Try.of(() -> Unirest.post(TOKEN_URL)
        .queryString("grant_type", "authorization_code")
        .queryString("code", code)
        .queryString("redirect_uri", config.getRedirectUrl())
        .queryString("client_id", config.getClientId())
        .queryString("client_secret", config.getClientSecret())
        .asJson());
    return optionResponse.map(response ->
        response.getBody().getObject().getString("access_token"));
  }
}
