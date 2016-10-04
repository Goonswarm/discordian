package com.tendollarbond.discordian.ldap.repository;

import com.tendollarbond.discordian.discord.model.NewUser;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import javaslang.control.Option;
import lombok.val;

import static com.tendollarbond.discordian.ldap.Constants.BASE_DN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PilotRepositoryTest {
  private InMemoryDirectoryServer testServer;
  private int testServerPort;

  @Before
  public void setUp() throws Exception {
    testServerPort = ThreadLocalRandom.current().nextInt(20000, 30000);
    val config = new InMemoryDirectoryServerConfig(BASE_DN);
    config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("test", testServerPort));
    config.setSchema(null); // Disable schema validation
    testServer = new InMemoryDirectoryServer(config);
    testServer.importFromLDIF(true, "src/test/resources/testdata.ldif");
  }

  @After
  public void tearDown() throws Exception {
    testServer.shutDown(true);

  }

  @Test
  public void listActivePilots() throws Exception {
    val repository = new PilotRepository(testServer);
    val pilots = repository.listActivePilots();

    assertEquals("Two active pilots are returned", 2, pilots.size());
  }

  @Test
  public void listInactivePilots() throws Exception {
    val repository = new PilotRepository(testServer);
    val pilots = repository.listInactivePilots();

    assertEquals("One inactive pilot is returned", 1, pilots.size());
  }

  @Test
  public void listPilots() throws Exception {
    val repository = new PilotRepository(testServer);
    val pilots = repository.listPilots(Option.none());

    assertEquals("Three pilots are returned in total", 3, pilots.size());
  }

  @Test
  public void getPilot() throws Exception {
    val repository = new PilotRepository(testServer);
    val pilot = repository.getPilot("Vincent Claeson");

    assertTrue("Existing pilot is found", pilot.isSuccess());
  }

  @Test
  public void updateDiscordId() {
    val repository = new PilotRepository(testServer);
    val newUser = NewUser.builder().id("123").build();
    val result = repository
        .getPilot("Vincent Claeson")
        .map(p -> repository.updateDiscordId(p, newUser));

    assertTrue("Update operation is a success", result.isSuccess());

    val updated = repository.getPilot("Vincent Claeson");

    assertEquals("New Discord ID matches", "123", updated.get().getDiscordId().get());
  }
}