package org.minecraft.wise.api.utils.render;

import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class TextUtils {

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static MutableText parseOrderedText(OrderedText orderedText) {
        MutableText parsedText = Text.empty();
        orderedText.accept((index, style, codePoint) -> {
            String charAsString = new String(Character.toChars(codePoint));
            MutableText literalText = Text.literal(charAsString).setStyle(style);
            parsedText.append(literalText);
            return true;
        });
        return parsedText;
    }

}
