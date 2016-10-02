package com.tendollarbond.discordian.discord;

import net.dv8tion.jda.Permission;

import org.junit.Test;

import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import lombok.val;

import static org.junit.Assert.*;

public class PermissionHelperTest {

  @Test
  public void testNoPermissions() {
    final Set<Permission> set = HashSet.of();
    final List<Permission> tested =
        List.ofAll(Permission.getPermissions(PermissionHelper.calculatePermissions(set)));

    assertTrue("No permissions are included", set.containsAll(tested));
  }

  @Test
  public void testDiscordianPermissions() {
    val permissions = PermissionHelper.discordianPermissions();
    val permsInt = PermissionHelper.calculatePermissions(permissions);
    val tested = List.ofAll(Permission.getPermissions(permsInt));

    assertTrue("Included permissions match requested", tested.containsAll(permissions));
  }
}