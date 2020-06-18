arne branch warning
===================
**Do not base any external work on these arne branches!!
These are temporary features added for specific mods' needs, before the "upstream"
(via master branch and then ported to the later MC versions) starts supporting these features
properly. These arne branches can/will get rebased and force pushed without mercy when needed.**


malilib
==============
malilib is a library mod used by masa's LiteLoader mods. It contains some common code previously
duplicated in most of the mods, such as multi-key capable keybinds, configuration GUIs etc.

Compiling
=========
* Clone the repository
* Open a command prompt/terminal to the repository directory
* run 'gradlew build'
* The built jar file will be in build/libs/