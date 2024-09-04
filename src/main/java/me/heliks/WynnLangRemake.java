package me.heliks;

import me.heliks.wynnlang.screen.SettingsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class WynnLangRemake implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(SettingsCommand::register);
    }
}