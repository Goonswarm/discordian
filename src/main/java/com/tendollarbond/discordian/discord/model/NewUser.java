package com.tendollarbond.discordian.discord.model;

import lombok.Value;

/**
 * Representation of a Discord user _before_ they have been added to a guild. This is built from
 * information retrieved from the user's OAuth session and LDAP.
 */
@Value
public class NewUser {
  String id;
  String oAuthToken;
  String characterName;
}
