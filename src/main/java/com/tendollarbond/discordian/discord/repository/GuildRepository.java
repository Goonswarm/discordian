package com.tendollarbond.discordian.discord.repository;

import javaslang.collection.List;
import lombok.val;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Methods for interacting with guilds in Discord.
 */
public class GuildRepository {
  final private IDiscordClient client;

  public GuildRepository(IDiscordClient client) {
    this.client = client;
  }

  /**
   * Lists all currently connected guilds in Discord.
   * */
  public List<IGuild> listConnectedGuilds() {
    val guilds = client.getGuilds();
    return List.ofAll(guilds);
  }
}
