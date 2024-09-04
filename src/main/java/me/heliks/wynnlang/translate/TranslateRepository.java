package me.heliks.wynnlang.translate;

import me.heliks.wynnlang.Core;
import me.heliks.wynnlang.translate.impl.ChatTranslate;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class TranslateRepository {
    private final List<Translate<?>> translateList = new ArrayList<>();

    public TranslateRepository() {
        translateList.add(new ChatTranslate("chat"));
    }


    public Packet<?> applyCurrentPacket(Packet<?> packet) {
        for (Translate<?> translate : translateList) {
            if (translate.getTemplate().equals(packet.getClass())) {
                return translate.applyPacket(packet);
            }
        }
        return packet;
    }
}
