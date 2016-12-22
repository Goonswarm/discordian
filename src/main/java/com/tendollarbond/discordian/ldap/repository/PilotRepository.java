package com.tendollarbond.discordian.ldap.repository;

import com.tendollarbond.discordian.discord.model.NewUser;
import com.tendollarbond.discordian.ldap.model.Pilot;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import org.json.JSONObject;

import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static com.tendollarbond.discordian.ldap.Constants.USER_DN;
import static com.unboundid.ldap.sdk.Filter.createEqualityFilter;

/**
 * Class for abstracting LDAP access to pilots.
 */
@Slf4j
public class PilotRepository {
  final private static String PILOT_OBJECT_CLASS = "pilot";
  final private static String PILOT_STATUS_ATTRIBUTE = "accountStatus";
  final private static List<String> PILOT_ACTIVE_STATUS_VALUES = List.of("Internal", "Ally");
  final private static Filter PILOT_ACTIVE_FILTER = Filter.createORFilter(PILOT_ACTIVE_STATUS_VALUES
      .map(value -> createEqualityFilter(PILOT_STATUS_ATTRIBUTE, value))
      .toJavaList());

  final private LDAPInterface connection;

  public PilotRepository(LDAPInterface connection) {
    this.connection = connection;
  }

  /**
   * Attempts to find a single pilot in the LDAP directory.
   * */
  public Try<Pilot> getPilot(String name) {
    log.debug("Fetching pilot {} from LDAP", name);
    val specificPilotFilter = Filter.createANDFilter(
        createEqualityFilter("objectClass", PILOT_OBJECT_CLASS),
        createEqualityFilter("characterName", name));

    val searchRequest = new SearchRequest(USER_DN, SearchScope.ONE, specificPilotFilter);

    return Try.of(() -> connection.search(searchRequest))
        .map(SearchResult::getSearchEntries)
        .map(entryList -> entryList.get(0))
        .map(PilotRepository::parsePilot);
  }

  /**
   * Returns a list of all currently active pilots in the LDAP directory.
   * */
  public List<Pilot> listActivePilots() throws LDAPSearchException {
    return listPilots(Option.of(PILOT_ACTIVE_FILTER));
  }

  /**
   * Lists all inactive pilots currently contained in the LDAP directory.
   *
   * In most cases this will be people who have just applied or who have previously been an alliance
   * member and have left.
   * */
  public List<Pilot> listInactivePilots() throws LDAPSearchException {
    return listPilots(Option.of(Filter.createNOTFilter(PILOT_ACTIVE_FILTER)));
  }

  /**
   * Updates a pilot's Discord user ID in LDAP.
   */
  public Try<Pilot> updateDiscordId(Pilot pilot, NewUser newUser) {
    log.info("Setting Discord ID for {} to {}", pilot.getCharacterName(), newUser.getId());
    return Try.of(() -> connection.getEntry(pilot.getDistinguishedName()))
        .map(entry -> entry.getAttributeValue("metadata"))
        .map(metadata -> insertDiscordId(metadata, newUser.getId()))
        .map(updated -> new Modification(ModificationType.REPLACE, "metadata", updated))
        .map(modification -> new ModifyRequest(pilot.getDistinguishedName(), modification))
        .mapTry(connection::modify)
        .map(r -> pilot.withDiscordId(Option.of(newUser.getId())));
  }

  /**
   * Returns a list of all pilots that are contained in the LDAP directory.
   *
   * Optionally allows specifying an additional LDAP search filter which is added to the generic one
   * for filtering out pilots.
   * */
  public List<Pilot> listPilots(Option<Filter> additionalFilter) throws LDAPSearchException {
    // Basic filter to only retrieve pilot objects
    val pilotFilter = createEqualityFilter("objectClass", PILOT_OBJECT_CLASS);

    // Combine the filters into a new one if an additional filter has been supplied.
    final Filter combinedFilter;
    if (additionalFilter.isDefined()) {
      combinedFilter = Filter.createANDFilter(pilotFilter, additionalFilter.get());
    } else {
      combinedFilter = pilotFilter;
    }

    val searchRequest = new SearchRequest(USER_DN, SearchScope.ONE, combinedFilter);
    val searchResult = connection.search(searchRequest);
    val pilots = List.ofAll(searchResult.getSearchEntries())
        .map(PilotRepository::parsePilot);

    log.debug("Retrieved {} pilots", pilots.size());

    return pilots;
  }

  /**
   * Parses an LDAP search result entry into a Pilot object.
   * */
  private static Pilot parsePilot(SearchResultEntry entry) {
    val builder = Pilot.builder();

    Option.of(entry.getDN()).forEach(builder::distinguishedName);
    Option.of(entry.getAttributeValue("characterName")).forEach(builder::characterName);
    Option.of(entry.getAttributeValue(PILOT_STATUS_ATTRIBUTE))
        .map(PILOT_ACTIVE_STATUS_VALUES::contains);
    builder.discordId(Option.of(entry.getAttributeValue("metadata"))
        .flatMap(PilotRepository::getDiscordId));

    return builder.build();
  }

  static Option<String> getDiscordId(String metadata) {
    return Try.of(() -> new JSONObject(metadata).getString("discordId")).toOption();
  }

  static String insertDiscordId(String metadata, String discordId) {
    return Try
        .of(() -> new JSONObject(metadata).put("discordId", discordId).toString())
        .getOrElse(metadata);
  }
}
