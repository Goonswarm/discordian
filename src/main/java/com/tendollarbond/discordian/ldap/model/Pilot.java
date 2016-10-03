package com.tendollarbond.discordian.ldap.model;

import javaslang.control.Option;
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
  Option<String> discordId;
  //String corporation;
  //boolean pilotActive;
}
