package com.tendollarbond.discordian.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Loads Discordian configuration from the environment.
 */
@Slf4j
public class ConfigLoader {
  public static Config loadConfiguration() {
    val clientId = System.getenv("DISCORD_CLIENT_ID");
    val botToken = System.getenv("DISCORD_BOT_TOKEN");

    if (clientId == null || botToken == null) {
      log.error("DISCORD_CLIENT_ID and DISCORD_BOT_TOKEN must be specified in the environment");
      throw new RuntimeException("Missing configuration fields");
    }

    return new Config(clientId, botToken);
  }
}
