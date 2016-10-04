package com.tendollarbond.discordian;

/**
 * Represents errors that occur during the Discordian flows.
 *
 * If they occur during an HTTP request in the endpoint they should be shown to the user.
 * In addition, they should always be logged.
 */
public class DiscordianError extends RuntimeException {
  public DiscordianError(String message) {
    super(message);
  }
}
