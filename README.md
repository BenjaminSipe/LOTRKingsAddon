# LOTR Per Player Mob cap
## How to use it:
As with all minecraft mods, download the jar and drop it in your mods folder.
It is server only, and will do nothing on a single player world

## Expected uses:
This mod is expected to be a vanilla server plugin... it was not designed with spigot/bukkit 
or any other server plugins you have. . . it is just a mod that happens to be serverside only.

## How it works:
This mod does two things:
1. It disables the LOTR mods NPC spawning mechanics*
2. Adds custom algorithm to check which players are below the mob cap, and only attempts to spawn mobs in chunks near those players.

\* Technically this is done by lowering the hardcoded total mob cap to 1 ( per player ). This doesn't affect passive mob spawning or invasions/traders/etc.

## I'm a nerd, how does it work?

Well, since you asked (: 

Starting with the easier part to understand, the LOTR Mod's default spawning behavior selects the nearby chunks utilizing the full list of players. Copying this functionality over, I can pass in a list of players that I want to get the nearby chunks.
To that end, the actual spawning behavior has been copied/referenced essentially unchanged. I've considered trying to modify/improve it. But I doubt any gains would be worth the effort involved.

The more complicated portion is I decide which players are "under" mob cap, and which are "over". There are two main problems that need to be overcome:
1. If multiple players are in the same region, their mobcaps should be combined.
2. If multiple players are in the same area, their combined mobcap needs to be respected.

### Solution
Quick Psuedocode here:
* Confirm one or more players are in the dimension
* Make a list to track the nearby mob count of each player...
* Cycle through the list of all loaded entities
* if entity is an LOTR NPC, and counts toward the mob cap ( isn't persistent, etc. )
  * find the 'first'\* player that is nearby that entity and increase that player's mob count.
* Filter players based on which ones are below the mob cap, use that list to select which chunks to randomize spawn attempts in.
* Proceed to normal mob spawning algorithm.
