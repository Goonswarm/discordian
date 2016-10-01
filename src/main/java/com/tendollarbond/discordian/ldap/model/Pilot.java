package com.tendollarbond.discordian.ldap.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation of a pilot object from LDAP.
 */
@Value
@Builder
public class Pilot {
  String characterName;
  String mailAddress;
  //String corporation;
  //boolean pilotActive;
}
