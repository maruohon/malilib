[![Curseforge](http://cf.way2muchnoise.eu/full_malilib_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/malilib) [![Curseforge](http://cf.way2muchnoise.eu/versions/For%20MC_malilib_all.svg)](https://www.curseforge.com/minecraft/mc-mods/malilib)

## malilib (or MaLiLib)

malilib is a library and code utility mod primarily for client-side Minecraft mods.
It was started in mid 2018 for ise in the LiteLoader versions of masa's client-side MC mods (Item Scroller, Litematica, MiniHUD, and Tweakeroo), to remove code duplication and to improve the interoperability of the mods.


### What does it do/have?

Most notably malilib contains a configuration options system, the associated configuration screens, a *very configurable* multi-key capable keybind system, and lots of various utility classes and methods, such as various inventory utils, rendering utils etc.


### "pre-rewrite" vs. "post-rewrite"

<details>
<summary>(Click) Information and rambling about the branches, state of the mod, the mod rewrites, and the future of the mod</summary>

A large rewrite and refactor and code cleanup effort started around June of 2020 in the main development branch of the mod, which was the `liteloader_1.12.2` branch.
In this rewrite basically all of the mod code has gone through various rewrites and cleanup, and also later on the mod package structure was changed from `fi.dy.masa.malilib` to just `malilib`.

The rewrite aims to make is easier to develop mods, requiring less boilerplate code, making it simpler to register things to malilib and do most of the common things, and also to make malilib a lot more flexible. The "old code" was quite frankly annoyingly hard coded and annoyingly structured in many ways. For example it was not possible to add new config option types without extending and overriding lots of the config screen stuff.

The "new code" version also partially rewrites the hotkey system such that the code that runs from hotkey presses are moved to a separate "Actions" system. This allows them to also be executed via other means, like the newly added "Action prompt" screen or the "Action execution screen" which might be better described as a customizable "Command deck" screen. Additionally there are other new systems such as the "Info Overlays" system that attempts to make it easier for multiple mods to show things on the screen as overlays, while being automatically positioned based on the other mods' overlays and things. Also there is now support for the server to request configs to be locked to a given value, or disabling running certain Actions. There are also lots of added utility and wrapper methods, basically in an attempt to hide most of the code and mapping changes between MC versions and mod loaders behind a small set of methods in malilib, making it easier for other mods to be ported between MC versions (less changed code lines just for trivial renames etc.).

As of 2023 coming to and end, the rewrite is still not entirely finished, mostly due to very little time going to working on the code in the recent months or even years due to IRL stuff. The `liteloader_1.12.2` and now also `ornithe/1.12.2` branches are quite close to being ready for first public releases of the "new code" though. There aren't many major things missing or major changes planned, but I'm still not ready to call the code "API stable" by any means, as there are still various things I'd like to clean up before the proper "stable release".

So as of late 2023, **all** of the 1.13+ branches, and also the 1.12.2 Forge branch (`pre-rewrite/forge/1.12.2`) are still based on the "old code", which was last updated from the main development code from the `liteloader_1.12.2` branch around June 2019 when it was last merged to the `rift_1.13.2` branch, and then all the Fabric branches are based on that Rift branch.


#### So what does all of this mean?!

Well, it means that at some point large code changes are coming, once the main development branch starts getting ported to the other MC versions. But the timeline for that is still completely unknown.

However there will most likely start to be mod versions based on the "new code" for the latest MC version first, as I'd really like to stop porting the old crappy code to new MC versions anymore.

And then at some point a lot later, when I finish more of my todo list (ideally after the entire todo list is finished... like that's ever going to happen...) I would then port the "final version" of the mods from 1.12.2 to all the later MC versions, in order. That will most likely only happen for the last minor version of each MC version though, so basically 1.12.2 -> 1.13.2 -> 1.14.4 -> 1.15.2 -> 1.16.5 -> 1.17.1 -> 1.18.2 -> 1.19.4 -> 1.20.? and so on. There may also be ports to other minor versions on request if some of those are still being used a lot for whatever reason.

So for other mod developers this basically means that I would not recommend putting lots of effort to supporting the quirks of the "old code" malilib. The "new code" is coming to the latest MC versions "somewhat soon" (tm).

</details>


## Building/compiling the mod for use

- Make sure you have the appropriate Java JDK version installed (see below)
- Clone the repository
- Open a command prompt/terminal to the repository directory
- run `./gradlew build` on \*nix or `gradlew.bat build` on Windows
- The built jar file will be in `build/libs/`
  - The jar you want for playing the game is the one with the shortest name: `...-<version>.jar`
  - The `-sources.jar` and `-deobf.jar` or `-dev.jar` are for mod development (usually used via a Maven dependency)


## Required Java JDK versions

malilib is developed using the minimum required Java version for a given MC version.
Additionally, to make it easier to port code between MC versions, I'm actually only using Java 8 features even in the branches for newer MC versions, at least for now.

The minimum required Java version to run the game are the following:
- MC `1.12 - 1.16.5`: Java `8`
- MC `1.17.x`: Java `16+`
- MC `1.18+`: Java `17+`

> Also note that older MC versions like 1.12.x might not work properly if you run them on newer Java versions. Some of the used old library versions may break in newer Java versions, or especially if you use Forge, the mod loader stuff is written against Java's internals, so Forge on 1.12.2 will simply not work on Java 9 or newer.

The above are also the required JDK versions for development in the "older" branches.
But the tool chains are evolving, and some of the Gradle plugins may have bumped the required gradle and/or Java version.
So even though the development up to 1.16.5 was originally done using JDK 8 and older Gradle versions, those versions may
not work anymore due to moved maven servers, updated Gradle plugins and dependencies and their Java version dependencies and whatnot.

For example the Ornithe 1.12.2 and 1.13.2 branches need JDK 17 at least for setting up the development environment,
but the mod is still built against Java 8 for releases.
