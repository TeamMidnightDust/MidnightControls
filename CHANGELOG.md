# Changelog

## v1.0.0

:tada: First release! :tada: 

- Added controller support.
- Added new controls settings GUI.
- Added experimental touchscreen support.
- Added controller controls GUI.
- Added a lot of options.
- Added key bindings for look around.
- And more!

### v1.0.1

- Fixed tutorial toast to look around not affected by camera movement done with a controller. ([#2](https://github.com/LambdAurora/LambdaControls/issues/2))

### v1.0.2 (Unofficial)

This update was never pushed but was aiming to fix [#4](https://github.com/LambdAurora/LambdaControls/issues/4).

- Fixed the toggle sneak button binding.
- Fixed broken chat arrow keys.
- Optimized a little bit the button indicator. (need more work)

## v1.1.0 - Chording update

This update also has a backport 1.14.4 version ([#9](https://github.com/LambdAurora/LambdaControls/issues/9)).

- Rewrote everything (almost).
- Added [networking](https://github.com/LambdAurora/LambdaControls/wiki/LambdaControls-Networking) for some features.
- Added second controller support (Joycons supported now hopefully).
- Added chording.
- Added better developer API
- Added hover messages ([#5](https://github.com/LambdAurora/LambdaControls/issues/5)).
- Added hotbar button bindings ([#7](https://github.com/LambdAurora/LambdaControls/issues/7)).
- Added front block placing feature ([#8](https://github.com/LambdAurora/LambdaControls/issues/8)).
- Added no creative fly drifting ([#8](https://github.com/LambdAurora/LambdaControls/issues/8)).
- Added option to enable controller focus.
- Added [OkZoomer](https://github.com/joaoh1/OkZoomer) compatibility.
- Added D-pad movements in inventories.
- Increased max speed ranges.
- Added [SpruceUI](https://github.com/LambdAurora/SpruceUI) for cleaner custom UI widgets.
- Added reset settings button.
- HUD side affects button indicators now.
- Added support for Advancements tabs.

### v1.1.1

## v1.2.0-1.3.0

- Improved rotation algorithm ([#11](https://github.com/LambdAurora/LambdaControls/issues/11)).
- Added virtual mouse.
- Added outline on front block placing.
- Added fast block placement ([#8](https://github.com/LambdAurora/LambdaControls/issues/8)).
- Added [REI](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items) compatibility.
- Improved HUD.
- Added recipe book control.
- And more!
- v1.3.0 specific: Updated to Minecraft 1.16.1

### v1.3.1

- Fixed broken inventory interactions ([#13](https://github.com/LambdAurora/LambdaControls/issues/13))
- Fixed virtual mouse preventing continuous attack (thus making breaking blocks impossible).
- Added support for [ModUpdater](https://gitea.thebrokenrail.com/TheBrokenRail/ModUpdater) hopefully.
- Updated [SpruceUI](https://github.com/LambdAurora/SpruceUI) to 1.5.2.

### v1.3.2

- Added vertical reacharound.
- Added more API for compatibility handlers.
- Improved reacharound API.
- Improved [REI](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items) compatibility.

## v1.4.0

 - Added analog movements ([#10](https://github.com/LambdAurora/LambdaControls/issues/10)).
 - Improved Ok Zoomer compability.
 - Updated [SpruceUI](https://github.com/LambdAurora/SpruceUI) to 1.5.8 to ensure 1.16.2 compability.
 - Internal changes:
   - Added analog input value to button bindings.
   - Replace lot of strings with Texts.
   - Improved block outline rendering injection.
   - Shadow library jars instead of Jar-in-Jar.
 - Fixed crash in inventory ([#16](https://github.com/LambdAurora/LambdaControls/issues/16))
 - WIP:
   - Started to work on action ring.
     - Will allow for better compability with other mods.
     - Might be interesting for keyboard users too.

### v1.4.1

 - Fixed crash with [REI](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items).
 
## v1.5.0

 - Added mappings string editor screen.
 - Added Simplified Chinese translations ([#18](https://github.com/LambdAurora/LambdaControls/pull/18)).
 - Added Mexican Spanish translations ([#22](https://github.com/LambdAurora/LambdaControls/pull/22)).
 - Added Xbox 360 button skin and overhauled Xbox button skin.
 - Added debug option.
 - Respect toggle setting in Accessibility screen.
 - Tweaked rotation speeds.
 - Updated to Minecraft 1.16.2.
 - Updated [SpruceUI](https://github.com/LambdAurora/SpruceUI) to 1.6.4.
 - Overhauled REI compatibility.
 - Improved horizontal reach-around.
 - Fixed crashes with Ok Zoomer.
 - Fixed crashes with key unbinding.
 - More WIP on keybind ring.
