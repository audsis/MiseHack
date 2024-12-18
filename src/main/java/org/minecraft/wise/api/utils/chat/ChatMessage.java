package org.minecraft.wise.api.utils.chat;

public class ChatMessage {
    final int messageID;
    final boolean override;
    final String text;

    public ChatMessage(String text, boolean override, int messageID) {
        this.text = text;
        this.override = override;
        this.messageID = messageID;
    }

    public String getText() {
        return this.text;
    }

    public int getMessageID() {
        return this.messageID;
    }

    public boolean doesOverride() {
        return this.override;
    }
}