Discordian
==========

[![Build Status](https://travis-ci.org/Goonswarm/discordian.svg?branch=master)](https://travis-ci.org/Goonswarm/discordian)

This is a Discord API client & bot that synchronises GoonAuth users from LDAP with Discord Guilds.

## Adding Discordian

1. Create a Discord application and note your client ID.
2. Configure and start Discordian
3. Discordian will print out the URL at which you can authorize Discordian for your guild.

## Permissions

Discordian currently runs with Administrator permissions. In the future these permissions may be
locked down.

See the [permission documentation][] for more information about this.

## Configuration

Discordian is configured via environment variables. Specifically:

* `DISCORD_CLIENT_ID`: Client ID of the Discord application
* `DISCORD_CLIENT_SECRET`: Secret key for the Discord application
* `DISCORD_REDIRECT_URL`: OAuth2 redirect URL for catching authentication codes
* `DISCORD_BOT_TOKEN`: Secret token for the Discord application bot
* `LDAP_HOST`: LDAP server hostname to connect to
* `LDAP_PORT`: LDAP server port to connect on

[this link]: https://discordapp.com/oauth2/authorize?client_id=&scope=bot&permissions=0
[permission documentation]: https://discordapp.com/developers/docs/topics/permissions#bitwise-permission-flags