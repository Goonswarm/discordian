package com.tendollarbond.discordian.discord.manager;

import com.tendollarbond.discordian.config.Config;
import com.tendollarbond.discordian.discord.model.NewUser;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;

import org.json.JSONObject;

import javaslang.control.Option;
import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static net.dv8tion.jda.requests.Requester.DISCORD_API_PREFIX;

/**
 * Methods for interacting with guilds in Discord.
 */
@Slf4j
public class GuildManager {
  final private JDA client;
  final private Guild guild;

  private GuildManager(JDA client, Guild guild) {
    this.client = client;
    this.guild = guild;
  }

  /**
   * Attempts to retrieve a guild by name and creates a guild manager for it.
   * If multiple guilds with the same name are connected to Discordian the outcome is undefined.
   */
  public static Option<GuildManager> create(JDA client, Config config) {
    val optGuild = client.getGuildsByName(config.getGuildName()).stream().findAny();
    return Option.ofOptional(optGuild).map(guild -> new GuildManager(client, guild));
  }

  /**
   * Kicks a member from a Discord guild.
   */
  public void kickMember(User user) {
    log.info("Kicking user {} from guild {}", user.getUsername(), guild.getName());
    new net.dv8tion.jda.managers.GuildManager(guild).kick(user);
  }

  /**
   * This call adds a user to a guild. In order to perform this operation, an OAuth token for the
   * user must be supplied that includes the scope 'guilds.join'.
   * The bot account performing this call must have the 'CREATE_INSTANT_INVITE' permission.
   *
   * See https://discordapp.com/developers/docs/resources/guild#add-guild-member for more
   * information.
   */
  public Try<NewUser> addMember(NewUser user) {
    log.info("Adding user {} to guild {}", user.getCharacterName(), guild.getName());
    val requester = ((JDAImpl) client).getRequester();
    val url = DISCORD_API_PREFIX + "guilds/" + guild.getId() + "/members/" + user.getId();
    val req = new JSONObject()
        .put("access_token", user.getOAuthToken())
        .put("nick", user.getCharacterName());

    // We need the NewUser value further downstream, but also want to know if this call succeeded.
    return Try.of(() -> requester.put(url, req)).map(resp -> user);
  }
}
