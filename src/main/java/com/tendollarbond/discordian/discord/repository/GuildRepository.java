package com.tendollarbond.discordian.discord.repository;

import com.tendollarbond.discordian.discord.model.NewUser;
import com.tendollarbond.discordian.discord.util.DiscordUtilities;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.managers.GuildManager;

import javaslang.collection.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Methods for interacting with guilds in Discord.
 */
@Slf4j
public class GuildRepository {
  final private JDA client;

  public GuildRepository(JDA client) {
    this.client = client;
  }

  /**
   * Lists all currently connected guilds in Discord.
   * */
  public List<Guild> listConnectedGuilds() {
    val guilds = client.getGuilds();
    return List.ofAll(guilds);
  }

  /**
   * Adds a member to a Discord guild.
   */
  public void addMember(Guild guild, NewUser user) {
    log.info("Adding user {} to guild {}", user.getCharacterName(), guild.getName());
    DiscordUtilities.addMemberToGuild(client, guild, user);
  }

  /**
   * Kicks and bans a member from a Discord guild.
   */
  public void banMember(Guild guild, User user) {
    log.info("Kicking user {} from guild {}", user.getUsername(), guild.getName());
    new GuildManager(guild).kick(user);
  }
}
