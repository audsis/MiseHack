package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.spotify.SpotifyUtils;
import org.minecraft.wise.impl.features.modules.client.HudColors;

public class Spotify extends HudComponent {

    public Spotify() {
        super("Spotify");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        if (NullUtils.nullCheck() || SpotifyUtils.currentArtist == null)
            return;

        String artist = SpotifyUtils.currentArtist;
        String track = SpotifyUtils.currentTrack;

        String spotify = artist + " - " + track;

        FontManager.drawText(event.getContext(), spotify, xPos.getValue().intValue(), yPos.getValue().intValue(), HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
    }
}
