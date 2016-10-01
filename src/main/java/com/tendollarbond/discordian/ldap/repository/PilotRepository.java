package com.tendollarbond.discordian.ldap.repository;

import com.tendollarbond.discordian.ldap.model.Pilot;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import javaslang.collection.List;
import javaslang.control.Option;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static com.tendollarbond.discordian.ldap.Constants.USER_DN;

/**
 * Class for abstracting LDAP access to pilots.
 */
@Slf4j
public class PilotRepository {
  final private LDAPInterface connection;

  public PilotRepository(LDAPInterface connection) {
    this.connection = connection;
  }

  public List<Pilot> listActivePilots() throws LDAPSearchException {
    final Filter activeFilter = Filter.createEqualityFilter("pilotActive", "true");
    return listPilots(Option.of(activeFilter));
  }

  public List<Pilot> listInactivePilots() throws LDAPSearchException {
    final Filter activeFilter = Filter.createEqualityFilter("pilotActive", "false");
    return listPilots(Option.of(activeFilter));
  }

  public List<Pilot> listPilots(Option<Filter> additionalFilter) throws LDAPSearchException {
    // Basic filter to only retrieve pilot objects
    final Filter pilotFilter = Filter.createEqualityFilter("objectClass", "goonPilot");

    // Combine the filters into a new one if an additional filter has been supplied.
    final Filter combinedFilter;
    if (additionalFilter.isDefined()) {
      combinedFilter = Filter.createANDFilter(pilotFilter, additionalFilter.get());
    } else {
      combinedFilter = pilotFilter;
    }

    final SearchRequest searchRequest = new SearchRequest(USER_DN, SearchScope.ONE, combinedFilter);
    final SearchResult searchResult = connection.search(searchRequest);
    final List<Pilot> pilots = List.ofAll(searchResult.getSearchEntries())
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

    // Corporation is not stored on the entry (yet)
    //Option.of(entry.getAttributeValue("")).forEach(cn -> builder.characterName(cn));

    return builder.build();
  }
}
