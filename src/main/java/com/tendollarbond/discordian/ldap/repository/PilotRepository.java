package com.tendollarbond.discordian.ldap.repository;

import com.tendollarbond.discordian.ldap.model.Pilot;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import javaslang.collection.List;
import javaslang.control.Option;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static com.tendollarbond.discordian.ldap.Constants.USER_DN;
import static com.unboundid.ldap.sdk.Filter.createEqualityFilter;

/**
 * Class for abstracting LDAP access to pilots.
 */
@Slf4j
public class PilotRepository {
  final private LDAPInterface connection;

  public PilotRepository(LDAPInterface connection) {
    this.connection = connection;
  }

  /**
   * Attempts to find a single pilot in the LDAP directory.
   * */
  public Option<Pilot> getPilot(String name) throws LDAPSearchException {
    log.info("Fetching pilot {} from LDAP", name);
    val specificPilotFilter = Filter.createANDFilter(
        createEqualityFilter("objectClass", "goonPilot"),
        createEqualityFilter("cn", name));

    val searchRequest = new SearchRequest(USER_DN, SearchScope.ONE, specificPilotFilter);
    val result = connection.search(searchRequest).getSearchEntries().stream()
        .map(PilotRepository::parsePilot)
        .findFirst();

    return Option.ofOptional(result);
  }

  /**
   * Returns a list of all currently active pilots in the LDAP directory.
   * */
  public List<Pilot> listActivePilots() throws LDAPSearchException {
    val activeFilter = createEqualityFilter("pilotActive", "true");
    return listPilots(Option.of(activeFilter));
  }

  /**
   * Lists all inactive pilots currently contained in the LDAP directory.
   *
   * In most cases this will be people who have just applied or who have previously been an alliance
   * member and have left.
   * */
  public List<Pilot> listInactivePilots() throws LDAPSearchException {
    val activeFilter = createEqualityFilter("pilotActive", "false");
    return listPilots(Option.of(activeFilter));
  }

  /**
   * Returns a list of all pilots that are contained in the LDAP directory.
   *
   * Optionally allows specifying an additional LDAP search filter which is added to the generic one
   * for filtering out pilots.
   * */
  public List<Pilot> listPilots(Option<Filter> additionalFilter) throws LDAPSearchException {
    // Basic filter to only retrieve pilot objects
    val pilotFilter = createEqualityFilter("objectClass", "goonPilot");

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

    Option.of(entry.getAttributeValue("cn")).forEach(builder::characterName);
    Option.of(entry.getAttributeValue("mail")).forEach(builder::mailAddress);
    builder.discordId(Option.of(entry.getAttributeValue("discordId")));

    // Corporation is not stored on the entry (yet)
    //Option.of(entry.getAttributeValue("")).forEach(cn -> builder.characterName(cn));

    return builder.build();
  }
}
