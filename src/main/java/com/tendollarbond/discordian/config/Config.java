package com.tendollarbond.discordian.config;

import lombok.Value;

/**
 * Represents Discordian application configuration.
 */
@Value
public class Config {
  /** Discord application client ID */
  String clientId;

  /** Discord application client secret */
  String clientSecret;

  /** Redirect URL for Discord OAuth flow */
  String redirectUrl;

  /** Discord bot authentication token */
  String botToken;

  /** Name of the Discord guild to administrate */
  String guildName;

  /** LDAP server hostname to connect to */
  String ldapHost;

  /** LDAP port to connect on */
  Integer ldapPort;
}
