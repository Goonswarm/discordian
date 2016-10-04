package com.tendollarbond.discordian.ldap.model;

import javaslang.control.Option;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Representation of a pilot object from LDAP.
 */
@Value
@Builder
public class Pilot {
  /** LDAP DN of this pilot */
  String distinguishedName;

  /** EVE character name */
  String characterName;

  String mailAddress;

  /** User ID of the user in Discord */
  @Wither Option<String> discordId;

  /** Pilot account status */
  Boolean pilotActive;
}
