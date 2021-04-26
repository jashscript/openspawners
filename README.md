# OpenSpawners

![](https://media.discordapp.net/attachments/802364992845512728/836125906799558686/openspawners_1.png?width=450&height=450)

OpenSpawners is a minecraft plugin for 1.16.5 that adds to the game custom spawners with upgradable tiers which spawns also tiered (and stacked) mobs with (customizable) drops per tier.

## Installation

Just download the latest [version](https://github.com/jashscript/openspawners/releases/download/1.0-SNAPSHOT/openSpawners-1.0-SNAPSHOT.jar).

For more details about how the developing is going see the releases page [Realeses](https://github.com/jashscript/openspawners/releases/tag/1.0-SNAPSHOT).

## Usage
```
After downloading the jar, put it on your server plugins.
As a admin you should make every kind of spawner you want to exist on the server, use:

/openspawners entityname (permission: openspawners)
The entity name should be in this format, using _ instead of spaces and all in lowercase. 
After that the spawner is created and you should populate it's costs (vault, to upgrade the spawner tier) and loot (per tier) 
with the in-game gui running the same command again.
To give to a player the spawner use:

/giveos player_name entity_type tier amount (permission: giveos)

Spawners of the same tier stack by chunk and the mobs stack when they've been made from the exact
same spawner and are 20 blocks near each other.
```
## Images
![](https://media.discordapp.net/attachments/802364992845512728/836124592174530670/unknown.png?width=559&height=400)

## Contributing

Actually i made all the code by myself [jash](https://github.com/jashscript) 😃

Thanks [0sss](https://github.com/0ssss) for the readme lol

## License
[MIT](https://choosealicense.com/licenses/mit/)
