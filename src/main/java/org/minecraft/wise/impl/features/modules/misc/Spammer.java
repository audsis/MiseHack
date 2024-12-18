package org.minecraft.wise.impl.features.modules.misc;


import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spammer extends Module {

    public static Spammer INSTANCE;
    final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(5).withRange(0, 20).register(this);
    final Value<Boolean> greenText = new ValueBuilder<Boolean>().withDescriptor("GreenText").withValue(false).register(this);
    public File spammerFile;
    protected List<String> strings = new ArrayList<>();

    private final Timer timer = new Timer.Single();

    public Spammer() {
        super("Spammer", Category.Misc);
        INSTANCE = this;
    }

    public void setCurrentFile(File file) {
        spammerFile = file;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            strings.clear();
            while ((line = reader.readLine()) != null) {
                if (line.replace("\\s", "").isEmpty()) continue;
                strings.add(line);
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck()) return;

        if (spammerFile != null && !strings.isEmpty()) {
            if (timer.hasPassed(delay.getValue().intValue() * 1000L)) {
                String text = strings.getFirst();

                if (greenText.getValue()) {
                    mc.player.networkHandler.sendChatMessage("> " + text);
                } else {
                    mc.player.networkHandler.sendChatMessage(text);
                }

                strings.removeFirst();
                strings.add(text);
                timer.reset();
            }
        }
    }
}
