package com.tendollarbond.discordian.config;

import java.util.Map;

import javaslang.collection.List;
import javaslang.control.Validation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_BOT_TOKEN;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_ID;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_SECRET;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_REDIRECT_URL;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_HOST;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_PORT;

/**
 * Loads Discordian configuration from the environment.
 */
@Slf4j
public class ConfigLoader {
  /**
   * Enum representing all possible environment configuration variables.
   * */
  public enum ConfigFields {
    DISCORD_CLIENT_ID,
    DISCORD_CLIENT_SECRET,
    DISCORD_REDIRECT_URL,
    DISCORD_BOT_TOKEN,
    LDAP_HOST,
    LDAP_PORT
  }

  public static Config loadConfiguration() {
    val env = System.getenv();
    val config = validateConfig(env);

    if (config.isInvalid()) {
      config.getError().forEach(log::error);
      throw new RuntimeException("Invalid configuration.");
    }

    return config.get();
  }

  /** Validation of configuration values */
  public static Validation<List<String>, Config> validateConfig(Map<String, String> env) {
    return Validation.combine(
        validatePresent(env, DISCORD_CLIENT_ID),
        validatePresent(env, DISCORD_CLIENT_SECRET),
        validatePresent(env, DISCORD_REDIRECT_URL),
        validatePresent(env, DISCORD_BOT_TOKEN),
        validatePresent(env, LDAP_HOST),
        validatePort(env.getOrDefault(LDAP_PORT.toString(), "389"), LDAP_PORT)
    ).ap(Config::new);
  }

  private static Validation<String, String> validatePresent(Map<String, String> env,
                                                            ConfigFields fieldName) {
    val field = env.get(fieldName.toString());
    if (field == null) {
      val error = String.format("Missing configuration field: %s", fieldName.toString());
      return Validation.invalid(error);
    }

    return Validation.valid(field);
  }

  private static Validation<String, Integer> validatePort(String portNumber, ConfigFields fieldName) {
    if (portNumber.matches("\\d+")) {
      return Validation.valid(Integer.parseInt(portNumber));
    }

    val error = String.format("Configuration field %s must be a number", fieldName.toString());
    return Validation.invalid(error);
  }
}
