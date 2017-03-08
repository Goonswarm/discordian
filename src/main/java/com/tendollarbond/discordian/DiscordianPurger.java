package com.tendollarbond.discordian;

import com.tendollarbond.discordian.discord.manager.GuildManager;
import com.tendollarbond.discordian.ldap.model.Pilot;
import com.tendollarbond.discordian.ldap.repository.PilotRepository;

import net.dv8tion.jda.entities.User;

import java.util.function.Predicate;

import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j @RequiredArgsConstructor
public class DiscordianPurger implements Runnable {
  final private GuildManager guildManager;
  final private PilotRepository pilotRepository;

  @Override
  public void run() {
    final List<User> discordUsers = guildManager.getMembers();
    Try.of(pilotRepository::listActivePilots)
        .map(pilots -> pilots.map(Pilot::getDiscordId).filter(Option::isDefined).map(Option::get))
        .map(discordIds -> {
          final Predicate<User> inactiveUserFilter = createInactiveUserFilter(discordIds);
          return discordUsers.filter(inactiveUserFilter);
        })
        .onSuccess(this::purgeInactiveUsers)
        .onFailure(cause -> log.error("Could not purge inactive users: ", cause));
  }

  private void purgeInactiveUsers(List<User> usersToPurge) {
    usersToPurge.forEach(user -> {
      log.info("Deactivating user {} (id: {})", user.getUsername(), user.getId());
      guildManager.kickMember(user);
    });
  }

  private static Predicate<User> createInactiveUserFilter(List<String> discordIds) {
    return (user -> !discordIds.contains(user.getId()));
  }
}
