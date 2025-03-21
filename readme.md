## HonnyCompass

Spigot plugin for BetonQuest compass.

The idea is taken from the plugin by autor Caleb Britannia:
https://github.com/CalebGitBritannia/calebcompass

But it only displays all compass destinations from BetonQuest targets.
If you select target in `/compass`, then pointer will be highlighted.

Targets has different marks if it is below or above. If you look in mark direction - it will display distance to the target.

I tried to make the compass lightweight as possible.

## How to use

Compass displays all targets from `/compass` from config `package.compass`

It displays all user compass directions:

```
compass add compass_name
compass add another_compass_name
```

BetonQuest compass docs: https://docs.betonquest.org/RELEASE/User-Documentation/Events-List/#compass-compass

## Permissions

```
honnycompass.reload
```

## Dependencies

- BetonQuest (3.0.0-DEV-114) - compass destinations.
- PlaceholderAPI

## Preview:

![preview](https://github.com/honnisha/HonnyCompass/blob/main/preview/Peek-2022-05-07.23-06.gif?raw=true)

# Bugs

If you encounter a bug, then write about it in the issues section. I'll try to fix it as soon as I have time.
