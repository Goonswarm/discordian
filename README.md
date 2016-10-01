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

[this link]: https://discordapp.com/oauth2/authorize?client_id=&scope=bot&permissions=0
[permission documentation]: https://discordapp.com/developers/docs/topics/permissions#bitwise-permission-flags