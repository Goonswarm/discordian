package com.tendollarbond.discordian;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tendollarbond.discordian.discord.manager.GuildManager;
import com.tendollarbond.discordian.discord.model.NewUser;
import com.tendollarbond.discordian.discord.model.NewUser.NewUserBuilder;
import com.tendollarbond.discordian.discord.util.DiscordOAuth2Flow;
import com.tendollarbond.discordian.ldap.model.Pilot;
import com.tendollarbond.discordian.ldap.repository.PilotRepository;
import com.unboundid.ldap.sdk.LDAPSearchException;

import org.json.JSONObject;

import java.io.IOException;

import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import spark.Request;
import spark.Response;

import static spark.Spark.exception;
import static spark.Spark.get;

/**
 * The web endpoint at which Discordian is available to users.
 */
@Slf4j
public class DiscordianEndpoint implements Runnable {
  final private PilotRepository pilotRepository;
  final private GuildManager manager;
  final private DiscordOAuth2Flow flow;

  public DiscordianEndpoint(PilotRepository pilotRepository, GuildManager manager, DiscordOAuth2Flow flow) {
    this.pilotRepository = pilotRepository;
    this.manager = manager;
    this.flow = flow;
  }

  @Override
  public void run() {
    log.info("Starting Discordian endpoint");
    get("/start", this::startDiscordRegistration);
    get("/token", this::handleDiscordCode);
    exception(DiscordianError.class, this::errorHandler);
  }

  private void errorHandler(Exception e, Request request, Response response) {
    log.error("Error during {} request to {}", request.requestMethod(), request.uri(), e);
    val error = new JSONObject().put("error", e.getMessage()).toString();
    response.status(500);
    response.body(error);
    response.type("application/json");
  }

  /**
   * Redirects the user to the Discord authentication page.
   * */
  private String startDiscordRegistration(Request request, Response response) {
    val discordUrl = flow.getAuthorizationUrl();
    response.redirect(discordUrl, 302);
    return "";
  }

  /**
   * Receives the code after a completed Discord authentication token request.
   *
   * This initiates the process of adding the user to the Discord server.
   * */
  private String handleDiscordCode(Request request, Response response) {
    val code = request.queryParams("code");
    if (code == null) {
      throw new DiscordianError("Did not receive authentication code from Discord.");
    }

    val user = request.headers("REMOTE_USER");
    if (user == null) {
      throw new DiscordianError("No GoonAuth user specified.");
    }

    val accessToken = flow.getAccessToken(code);

    val newUser = accessToken
        .mapTry(DiscordianEndpoint::getDiscordUserId)
        // Create a NewUserBuilder and apply the first known fields to it.
        .map(discordId -> NewUser.builder().id(discordId).oAuthToken(accessToken.get()))
        .flatMapTry(builder -> applyPilotFromLdap(user, builder))
        .onFailure(e -> {
          log.error("Error while handling Discord code.", e);
          throw new DiscordianError("Could not gather all required user information.");});

    newUser.map(manager::addMember);

    return "success";
  }

  /**
   * Completes a NewUser construction by looking up the supplied user in LDAP, checking that it is
   * still active and applying the character name to the builder.
   */
  private Try<NewUser> applyPilotFromLdap
  (String username, NewUserBuilder builder) throws LDAPSearchException {
    val optionPilot = pilotRepository.getPilot(username);
    return optionPilot
        .map(Pilot::getCharacterName)
        .map(builder::characterName)
        .map(NewUserBuilder::build)
        .toTry();
  }

  /**
   * Makes a call to the Discord API using the user's OAuth credentials in order to retrieve the
   * Discord user ID.
   */
  private static String getDiscordUserId(String accessToken) throws IOException, UnirestException {
    // https://discordapp.com/developers/docs/resources/user#get-current-user
    val url = "https://discordapp.com/api/users/@me";
    val authHeader = "Bearer " + accessToken;
    val response = Unirest.get(url)
        .header("Authorization", authHeader)
        .asJson();

    if (response.getStatus() != 200) {
      throw new DiscordianError("Could not retrieve user information from Discord.");
    }

    return response.getBody().getObject().getString("id");
  }


}
