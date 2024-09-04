package me.heliks.wynnlang.translate;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class Translate<T> {
    String name;
    Class<T> template;
    @Setter
    @NonFinal
    boolean active;

    public Translate(String name, Class<T> template) {
        this.name = name;
        this.template = template;
    }

    public abstract T getPacket(T packet);

    public <A> A applyPacket(A packet) {
        return (A) getPacket((T) packet);
    }

    private final Map<String, String> translateMap = new HashMap<>();

    protected String applyTranslate(String string) {
        if (translateMap.get(string) == null) {
            String requestUrl = "https://lingva.ml/api/v1/en/ru/" + string;
            HttpResponse<JsonNode> response = Unirest.get(requestUrl).asJson();
            if (response.getStatus() == 200) {
                String out = response.getBody().getObject().getString("translation");
                translateMap.put(string, out);
                return out;
            } else {
                System.out.println("Error: " + response.getStatusText());
            }
        } else {
            return translateMap.get(string);
        }
        return string;
    }
}
