package com.tendollarbond.discordian;

import com.tendollarbond.discordian.config.Config;
import com.tendollarbond.discordian.config.ConfigLoader;
import com.tendollarbond.discordian.discord.PermissionHelper;
import com.tendollarbond.discordian.discord.manager.GuildManager;
import com.tendollarbond.discordian.discord.util.DiscordOAuth2Flow;
import com.tendollarbond.discordian.ldap.ConnectionHelper;
import com.tendollarbond.discordian.ldap.repository.PilotRepository;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

import javax.security.auth.login.LoginException;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class Main {
  @SneakyThrows
  public static void main(String[] args) {
    val config = ConfigLoader.loadConfiguration();
    printAuthorizationUrl(config);

    // Instantiate LDAP & Discord connections, then create managers and repositories
    val ldapConnection = ConnectionHelper.establishLdapConnection(config);
    val client = getClient(config);
    val pilotRepository = new PilotRepository(ldapConnection);
    val guildManager = GuildManager.create(client, config);
    val flow = new DiscordOAuth2Flow(config);

    if (guildManager.isEmpty()) {
      throw new DiscordianError("Could not find guild " + config.getGuildName());
    }

    new DiscordianEndpoint(pilotRepository, guildManager.get(), flow).run();
  }

  private static JDA getClient(Config config) throws LoginException, InterruptedException {
    return new JDABuilder().setBotToken(config.getBotToken()).buildBlocking();
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
