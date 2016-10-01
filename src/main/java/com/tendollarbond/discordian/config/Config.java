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

  /** Discord bot authentication token */
  String botToken;

  /** LDAP server hostname to connect to */
  String ldapHost;

  /** LDAP port to connect on */
  Integer ldapPort;
}
