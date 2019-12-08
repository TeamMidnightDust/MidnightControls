# Contributing to LambdaControls

:tada: First of all, thanks for taking time to contribute! :tada:

The following is a set of guidelines for contributing to LambdaControls. 
Feel free to propose changes to this document in a pull request.

**Table of Contents**

[Code of Conduct](#code-of-conduct)

[What should I know before I get started?](#what-should-i-know-before-i-get-started)

[How can I contribute?](#how-can-i-contribute)

[Styleguides](#styleguides)

## Code of Conduct

This project and everyone participating in it is governed by the [Code of Conduct](https://github.com/LambdAurora/LambdaControls/blob/master/CODE_OF_CONDUCT.md).
By participating, you are expected to uphold this code. Please report unacceptable behavior at [aurora42lambda@gmail.com](mailto:aurora42lambda@gmail.com).

## What should I know before I get started?

### Fabric

[Fabric](https://fabricmc.net/) is the mod loader and the software which allows Gradle to setup the workspace.

### Java 8

Java is the main language used to make LambdaControls alive.
Knowing how to code in Java is necessary if you contribute to the code.

### Minecraft

As it is a Minecraft mod you should know a bit how Minecraft works and how modding works.

### Mixins

[Mixins](https://github.com/SpongePowered/Mixin/wiki) are a main part in this mod, they allow the necessary modifications to the Minecraft Client.

### Gradle

[Gradle](https://gradle.org/) is the build tool used for this project.

### Git

Git is the control version software we use for LambdaControls, please know how to use it if you consider contributing to the code.

Git commits should be and must be signed.

## How can I contribute?

### Reporting Bugs

#### Before submitting a bug report

- Check if you can reproduce it on other platforms, on multiple web browsers.
- Perform a search to see if the problem has already been reported. If it has **and the issue is still open**, add a comment to the existing issue instead of opening a new one.

#### How do I submit a bug report?

Go in the issues tab in GitHub and read the [bug report guide](https://github.com/LambdAurora/LambdaControls/blob/master/.github/ISSUE_TEMPLATE/bug_report.md)

### Suggesting enhancements

Enhancement suggestions are tracked as [GitHub issues](https://github.com/LambdAurora/LambdaControls/issues).
Check out the [feature request](https://github.com/LambdAurora/LambdaControls/blob/master/.github/ISSUE_TEMPLATE/feature_request.md) guide.

### Do pull requests

You can help LambdaControls by writing code and submit it with pull requests.

Pull requests will be accepted if they follow the [styleguide](#styleguides), if they are useful, etc...
We can refuse a pull request if the commits are not signed, so don't forget to [sign them](https://help.github.com/en/articles/signing-commits)!

Feel free to pull request! 

## Styleguides

### Git commit messages

* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Consider starting the commit message with an emote, emotes for your commit can be found at the [gitmoji guide](https://gitmoji.carloscuesta.me/).
* (Not for the message) Don't forget to sign the commit. 

### Naming convention

Names in the code should be explicit and always in `snake_case`, `camelCase` will not be allowed.
`PascalCase` can be used for class name.

We chose `snake_case` because it is more accessible for everyone: for people who don't speak English as their native language it is more easy to see the words when they are separated,
it also allows the correct use of screen reader on the code with `snake_case` due to the absence of upper case characters.

### Brace placement

Every braces should be at the end of the line of function declaration, etc...
The only exception is class declarations: braces must be on the next line.

### Quick note for users of the Intellij IDEA IDE

As a user of the Intellij IDEA IDE you have the format code shortcut which use a codestyle described by a file.
You can import the codestyle file here: [LambdAurora's dotfiles](https://github.com/LambdAurora/dotfiles/blob/master/jetbrains/lambdacodestyle2.xml).
