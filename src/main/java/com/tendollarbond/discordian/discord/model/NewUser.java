package com.tendollarbond.discordian.discord.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation of a Discord user _before_ they have been added to a guild. This is built from
 * information retrieved from the user's OAuth session and LDAP.
 */
@Value
@Builder
public class NewUser {
  /** The user's Discord ID. */
  String id;

  /** Access token with 'guilds.join' scope. */
  String oAuthToken;

  /** User's EVE character name. */
  String characterName;
}
