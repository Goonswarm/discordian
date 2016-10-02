package com.tendollarbond.discordian;

import com.tendollarbond.discordian.config.Config;
import com.tendollarbond.discordian.config.ConfigLoader;
import com.tendollarbond.discordian.discord.PermissionHelper;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

import javax.security.auth.login.LoginException;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class Main {
  public static void main(String[] args) throws LoginException, InterruptedException {
    val config = ConfigLoader.loadConfiguration();
    printAuthorizationUrl(config);

    val client = getClient(config);
  }


  private static JDA getClient(Config config) throws LoginException, InterruptedException {
    return new JDABuilder().setBotToken(config.getBotToken()).buildBlocking();
  }

  /* private static IDiscordClient getClient(Config config) throws DiscordException {
    val builder = new ClientBuilder().withToken(config.getBotToken());
    builder.login();
    return builder.build();
  } */

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
