package me.heliks.wynnlang.translate.impl;

import me.heliks.wynnlang.translate.Translate;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatTranslate extends Translate<GameMessageS2CPacket> {
    public ChatTranslate(String name) {
        super(name, GameMessageS2CPacket.class);
    }

    @Override
    public GameMessageS2CPacket getPacket(GameMessageS2CPacket packet) {
        Text originalText = packet.content();
        String string = originalText.getString();

        if (isDialogue(string)) {
            String regex = ":\\s*(.*?)\\s*Press\\s.*?to continue";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(string);

            if (matcher.find()) {
                String extractedText = matcher.group(1).trim();
                String filteredText = extractedText.replaceAll("[\\[\\]]", "''").replaceAll("[^a-zA-Z' \"]", "");
                Text result = dialogueTextDetect(originalText, extractedText, applyTranslate(filteredText));
                return new GameMessageS2CPacket(result, packet.overlay());
            }
        }
        return packet;
    }

    private boolean isDialogue(String string) {
        return string.contains("Press ") && string.contains(" to continue");
    }

    private Text dialogueTextDetect(Text textComponent, String toReplace, String replacement) {
        List<Text> list2 = textComponent.getWithStyle(textComponent.getStyle());
        StringBuilder outText = new StringBuilder();
        boolean isText = false;
        boolean end = false;
        Text output = Text.literal("");
        for (Text text : list2) {
            if (isText) {
                outText.append(text.getString());
            } else {
                output = output.copy().append(text);
            }
            if (text.toString().contains("dark_green")) {
                isText = true;
            }
            if (text.toString().contains("literal{\n}")) {
                if (!end && outText.toString().contains(toReplace)) {
                    output = output.copy().append(Text.literal(outText.toString().replace(toReplace, replacement)).setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
                    end = true;
                }
                isText = false;
            }
        }
        return output;
    }
}
