# WynnLang-Remake

A Fabric mod that translates Wynncraft quest dialogues into Russian.

## How it works
The mod hooks into Minecraft's network layer using SpongePowered Mixin
and intercepts incoming packets before the client processes them.
`TranslateRepository` dispatches each packet to a matching `Translate<T>`
implementation, which rewrites the text while preserving the original
formatting.

## Status
Work in progress. The packet interception and text rewriting work;
the translation files are not filled in yet.

## What I would do differently
The mixin currently overwrites `channelRead0` entirely via `@At("HEAD")`
and `ci.cancel()`. Using `@ModifyVariable` would be cleaner and would
not conflict with other mods.
