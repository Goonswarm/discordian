package com.tendollarbond.discordian.ldap;

import com.tendollarbond.discordian.config.Config;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;

import lombok.val;

/**
 * Helper class to establish LDAP connections.
 */
public class ConnectionHelper {
  /**
   * Establishes an LDAP connection in a pool with at most 5 members.
   * */
  public static LDAPInterface establishLdapConnection(Config config) throws LDAPException {
    val conn = new LDAPConnection(config.getLdapHost(), config.getLdapPort());
    val pool = new LDAPConnectionPool(conn, 1, 5);
    pool.setCreateIfNecessary(true);
    return pool;
  }
}
