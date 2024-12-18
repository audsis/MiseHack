package org.minecraft.wise.api.utils;

import net.minecraft.client.util.InputUtil;

public class Keyboard {

    public static int getKeyIndex(String string) {
        string = string.replace(" ", ".").toLowerCase();
        switch (string) {
            case "+":
                return 334;
            case ".":
                return 46;
            case "=":
                return 61;
            case "*":
                return 332;
            case "-":
                return 47;
            case "'":
                return 39;
            case ",":
                return 44;
            case "rshift":
                return 344;
            case "shift":
                return 340;
            case "rctrl":
                return 345;
            case "ctrl":
                return 341;
            case "alt":
                return 342;
            case "ralt":
                return 346;
        }
        try {
            return InputUtil.fromTranslationKey("key.keyboard." + string).getCode();
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
