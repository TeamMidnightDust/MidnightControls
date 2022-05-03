# MidnightControls

<!-- modrinth_exclude.start -->
![Java 16](https://img.shields.io/badge/language-Java%2017-9B599A.svg?style=flat-square) <!-- modrinth_exclude.end -->
[![GitHub license](https://img.shields.io/github/license/TeamMidnightDust/MidnightControls?style=flat-square)](https://raw.githubusercontent.com/TeamMidnightDust/MidnightControls/master/LICENSE)
![Environment: Client](https://img.shields.io/badge/environment-client-1976d2?style=flat-square)
[![Mod loader: Quilt/Fabric]][quilt] <!-- modrinth_exclude.start -->
![Version](https://img.shields.io/github/v/tag/TeamMidnightDust/MidnightControls?label=version&style=flat-square)
[![CurseForge](http://cf.way2muchnoise.eu/title/354231.svg)](https://www.curseforge.com/minecraft/mc-mods/midnightcontrols)
<!-- modrinth_exclude.end -->

A Fabric Minecraft mod which adds better controls, reach-around and controller support.  
Forked from [LambdaControls](https://github.com/LambdAurora/LambdaControls) by the amazing [LambdAurora](https://github.com/LambdAurora), which was sadly discontinued.

## What's this mod?

This mod adds better controls, reach-around features, etc.

Haven't you dreamed to travel in your modded Minecraft world with your controller? Yes? Then this mod is made for you!

This mod also adds controller support.

## ✅ Features:

- Controller support
- Touchscreen support (very experimental and buggy).
- Keyboard controls to look around.
- Toggleable on screen button indicator (like in Bedrock Edition).
- Vertical reach-around.
- Many Bedrock Edition features:
   - Toggleable fly drifting
   - Front block placing (be careful with this one)
- New controls settings!
- Many options in config to change to your liking.
- Many controllers supported and in a simply way your own controller mappings.
- An easy API for developers to add their own button bindings.

## 🎮 Supported Controllers:

- Dualshock controllers
- Dualsense controllers
- Xbox controllers
- Switch Pro controllers
- Joycons
- Steam controller and Steam Deck (WIP)
- And many more!

## Screenshots

![controller_controls](images/controller_controls.png)
![controller_options](images/controller_options.png)

<!-- modrinth_exclude.start -->
## Build

Just do `./gradlew build` and everything should build just fine!
<!-- modrinth_exclude.end -->

[quilt]: https://quiltmc.org
[Mod loader: Quilt/Fabric]: https://img.shields.io/badge/modloader-Quilt%2FFabric-blueviolet?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAADGAAAAxgGwdJvFAAAFU0lEQVR4nO1bwW7bOBCdCHtP+wUNxIvhS7yA7kkPOjf9AKPam4/OF2zzB96bblXhH0jPPkS5G1jnIvgib/IFtX/ALoYZemmKtEWRRe3aDzAsURbJGQ6HM4/02Wq1gmNGcNTSnxQA8IdakDK4AOCfJpj3SpiI98r2rAMAbxrWNWFFOMeL1nCJdXQa1gPTbpCbnm34ABIeBThv2hgAfOyVcF+2ZwkAfHGo54kVIRe6NVxiny4d6vpn2g36ugfqFLhwFB6kkUoc67k0XLv0qYKj9wEnBXiqZwEAf/ZKOOuV8BkLWBFesyI8A4C/LOt6AoC39C7HtBvg9Vt65hUnC/BUDzrOf1MGq5S9WkDZnuVle7ZqsBKgw/tO73K0hku8/u64EmhxsoA96MMvxdEroBIKO+CWoshnqqJPYTAGRJ8sql3QO3Op7D19D3z7AV8KeAGArFf+32lWhDwnKNuzuaUCclaE93KBiOU9hMQV+FLAOxz9lPHRR0VkZXs2oBDUNon5ULZnKOgcYwl4FTwna/K+CtRRwIK+dTnCCwkP9I0fkXmh4FcN+6UKqqtHblvtb+18po4TvKaPilsS8qVuYx5havsbJXS1I0ZVAXP1B5jfyzm+hAnN+We1XPluioX03kJtY9oNtG1T+b1aburDxhRAQVMGHw3z9k65F41nktmjUnjjrAj7ZXv27ECIyEKgBd7saBuka7lsPu0GA1MjR0+KVpwgMTlrMkN4YoztlZ/2camTvD0QjcWZF6KxMgcLyKbdAN+HKOb1y6PYH49gEsUwUKw1G48gi2KQZcApkYxH1eldUQBxeKbkRfXEQjDZ21+h2bMiHJDwH2wk1rSX0XWueHZd2yCZ/oVSnilTaA3VAiqjRRahQ0JBjuov3ijfPqAua0kUg67tmyjWCmvsS504wGQRnywjPJ8wtY3xw3827fjMBXziUalL3DcNrIzYRwXcTbvBZ7lgPHoNxKKYky1/+2xsHxXQbw2X/EIoggQHyjC9Yh8VcC6NshDc66jLOHpC5KSAPejDL8U+KuArboTQZgjHeARn+MFnvhvbRyfYaQ2XfNkTVFgUr/mIxlvkJuyjAjCae6BrYQUPW37vhDpT4MnAsCyIgbHBnYbcADJt7/t+deBCiSWsCG8sOv5IgU2FnJh2g+RnBDl1oCpgogqEx1TEURUFokx+tpCYnEzzjg1kh+fq/Ix9OXpG6OjjgI1VIGWcOMhlXh4PPdAz1VTe90rIU8Z/L9JUnALXSK4SjSUzOY+Y1ekyOlzjaamTvf3X8cjunBH1v6OeVtsG1QI66qYEVkoVmyA/O5fYmIHjgSsrskUavAc6q1BLeXXiANMZu0HKwGav7opGv+LticZqHOToLBeZrJRxC97qjOsowCTgZYO9OlNa25haMwgvsFMJPp3gLW1ji8b6dO89fhfYIbzAl23T4adtjyNvD6/mbbs9Xgs1hRcwWoIvCxDb47nQNm5aRDHvoPGcblNYCi+gtQSfU+AdLYfioLXYtHA9eruBhsILVJRwUIGQo/ACG0o4GAV4El5grQSffMA3SqbEnBdb1zeunfYsvAB3jL4UwE92KauA2NnNbLerNKhEqJ6Q+FIAOjpcATAVzjFHoC3qC9OurCW0W9s+6vXpAy4p0hPkSUL3ziNHiY3tqfNdQN4jORgnSEGMLyU8Udbq1QIEdEyRF3hSwlp4vKkwQimrHDuxAR6S4tleFIPzERnhSFXQEtbkD1kbwiMOlhJroISK8IiDpcQsp4NWeMRBc4I1lWAUHnHwpOgOJWwVHvFbsMIGJewUHvFb7QuQYxR/ttgIzbUAgB9KOM3uc+PN7QAAAABJRU5ErkJggg==
