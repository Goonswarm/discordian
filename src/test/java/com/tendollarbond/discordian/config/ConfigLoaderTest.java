package com.tendollarbond.discordian.config;

import org.junit.Test;

import java.util.HashMap;

import lombok.val;

import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_BOT_TOKEN;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_ID;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_SECRET;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_HOST;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigLoaderTest {
  @Test
  public void validateValidConfig() throws Exception {
    val env = new HashMap<String, String>();
    env.put(DISCORD_CLIENT_ID.toString(), "id");
    env.put(DISCORD_CLIENT_SECRET.toString(), "secret");
    env.put(DISCORD_BOT_TOKEN.toString(), "bot-token");
    env.put(LDAP_HOST.toString(), "ldap-host");

    val config = ConfigLoader.validateConfig(env);

    assertTrue("Configuration is valid", config.isValid());
    assertEquals("Client ID field matches", "id", config.get().getClientId());
  }

  @Test
  public void validateInvalidConfig() throws Exception {
    val env = new HashMap<String, String>();
    env.put(LDAP_PORT.toString(), "invalid-port");

    val config = ConfigLoader.validateConfig(env);

    assertFalse("Configuration is invalid", config.isValid());
    assertEquals("One error is returned per field", 5, config.getError().size());
  }
}