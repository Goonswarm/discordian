package com.tendollarbond.discordian.config;

import lombok.Value;

/**
 * Represents Discordian application configuration.
 */
@Value
public class Config {
  /** Discord application client ID */
  String clientId;

  /** Discord bot authentication token */
  String botToken;
}
