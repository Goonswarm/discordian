package com.tendollarbond.discordian.config;

import java.util.Map;

import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Validation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_BOT_TOKEN;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_ID;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_CLIENT_SECRET;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_GUILD_NAME;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.DISCORD_REDIRECT_URL;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_HOST;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_PASSWORD;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_PORT;
import static com.tendollarbond.discordian.config.ConfigLoader.ConfigFields.LDAP_USER;

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
    DISCORD_GUILD_NAME,
    LDAP_HOST,
    LDAP_PORT,
    LDAP_USER,
    LDAP_PASSWORD
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
    /* Javaslang can validate up to 8 values. After that the following happens. */
    val builder = Config.builder();

    // Validate and apply the first 8 :/
    val first = Validation.combine(
        validatePresent(env, DISCORD_CLIENT_ID),
        validatePresent(env, DISCORD_CLIENT_SECRET),
        validatePresent(env, DISCORD_REDIRECT_URL),
        validatePresent(env, DISCORD_BOT_TOKEN),
        validatePresent(env, DISCORD_GUILD_NAME),
        validatePresent(env, LDAP_HOST),
        validatePort(env.getOrDefault(LDAP_PORT.toString(), "389"), LDAP_PORT),
        validateOption(env, LDAP_USER)
        // validateOption(env, LDAP_PASSWORD)
    ).ap((a1, a2, a3, a4, a5, a6, a7, a8) -> builder.clientId(a1).clientSecret(a2)
        .redirectUrl(a3).botToken(a4).guildName(a5).ldapHost(a6).ldapPort(a7).ldapUser(a8));

    // Validate the next options.
    val second = validateOption(env, LDAP_PASSWORD).map(a1 -> builder.ldapPassword(a1));

    // Manually concatenate the errors
    final List<String> errors = List.of(first, second).flatMap(validation -> {
      if (validation.isInvalid()) {
        return (List<String>) validation.getError();
      } else {
        return List.empty();
      }});

    if (errors.nonEmpty()) {
      return Validation.invalid(errors);
    } else {
      return Validation.valid(builder.build());
    }
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

  private static Validation<String, Option<String>> validateOption(Map<String, String> env,
                                                                   ConfigFields fieldName) {
    return Validation.valid(Option.of(env.get(fieldName.toString())));
  }
}
