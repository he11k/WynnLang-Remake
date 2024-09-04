package me.heliks.wynnlang;

import lombok.Getter;
import lombok.Setter;
import me.heliks.wynnlang.translate.TranslateRepository;

@Getter
public class Core {
    public static final Core INSTANCE = new Core();
    private TranslateRepository translateRepository;
    @Setter
    private boolean active;

    public Core() {
        translateRepository = new TranslateRepository();

    }

}
