package com.tendollarbond.discordian.discord;

import net.dv8tion.jda.Permission;

import javaslang.collection.HashSet;
import javaslang.collection.Set;

import static net.dv8tion.jda.Permission.ADMINISTRATOR;

/**
 * Calculates the required permissions for Discordian.
 *
 * TODO: Lock this down from just "Administrator"
 */
public class PermissionHelper {
  public static Set<Permission> discordianPermissions() {
    return HashSet.of(ADMINISTRATOR);
  }

  public static int calculateDiscordianPermissions() {
    return calculatePermissions(discordianPermissions());
  }

  public static int calculatePermissions(Set<Permission> permissions) {
    return permissions
        .map(permission -> 1 << permission.getOffset())
        .fold(0, (p1, p2) -> p1 |= p2);
  }
}
