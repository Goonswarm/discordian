package com.tendollarbond.discordian.discord;

import java.util.EnumSet;

import sx.blah.discord.handle.obj.Permissions;

import static sx.blah.discord.handle.obj.Permissions.ADMINISTRATOR;
import static sx.blah.discord.handle.obj.Permissions.generatePermissionsNumber;

/**
 * Calculates the required permissions for Discordian.
 *
 * TODO: Lock this down from just "Administrator"
 */
public class PermissionHelper {
  private static EnumSet<Permissions> discordianPermissions() {
    return EnumSet.of(ADMINISTRATOR);
  }

  public static int calculateDiscordianPermissions() {
    return generatePermissionsNumber(discordianPermissions());
  }
}
