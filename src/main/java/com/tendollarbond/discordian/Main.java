package com.tendollarbond.discordian;

import com.tendollarbond.discordian.config.Config;
import com.tendollarbond.discordian.config.ConfigLoader;
import com.tendollarbond.discordian.discord.PermissionHelper;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import sx.blah.discord.util.DiscordException;

@Slf4j
public class Main {
  public static void main(String[] args) throws DiscordException {
    val config = ConfigLoader.loadConfiguration();

    printAuthorizationUrl(config);
  }

  /**
   * Prints the authorization URL that a Guild owner must visit to authorize Discordian.
   * */
  private static void printAuthorizationUrl(Config config) {
    val permissions = PermissionHelper.calculateDiscordianPermissions();
    val format = "https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%d";
    val url = String.format(format, config.getClientId(), permissions);

    log.info("To authorize Discordian please visit {}", url);
  }
}
