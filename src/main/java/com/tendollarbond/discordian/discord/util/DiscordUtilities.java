package com.tendollarbond.discordian.discord.util;

import com.tendollarbond.discordian.discord.model.NewUser;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.requests.Requester;
import net.dv8tion.jda.requests.Requester.Response;

import org.json.JSONObject;

import lombok.val;

import static net.dv8tion.jda.requests.Requester.DISCORD_API_PREFIX;

/**
 * Utilities for things that were left out in the various Discord Java libraries, for no particular
 * reason.
 */
public class DiscordUtilities {
  /**
   * This call adds a user to a guild. In order to perform this operation, an OAuth token for the
   * user must be supplied that includes the scope 'guilds.join'.
   * The bot account performing this call must have the 'CREATE_INSTANT_INVITE' permission.
   *
   * See https://discordapp.com/developers/docs/resources/guild#add-guild-member for more
   * information.
   */
  public static Response addMemberToGuild(JDA jda, Guild guild, NewUser user) {
    val requester = ((JDAImpl) jda).getRequester();
    val url = DISCORD_API_PREFIX + "guilds/" + guild.getId() + "/members/" + user.getId();
    val req = new JSONObject()
        .put("access_token", user.getOAuthToken())
        .put("nick", user.getCharacterName());
    return requester.put(url, req);
  }
}
