package org.minecraft.wise.api.utils.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public final class SpotifyUtils {
    public static boolean isSpotifyRunning;
    public static String currentTrack;
    public static String lastTrack;
    public static String currentArtist;
    public static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(10);

    public static void init() {
        scheduled.scheduleAtFixedRate(SpotifyUtils::updateCurrentTrack, 100, 100, TimeUnit.MILLISECONDS);
    }

    public static void reset() {
        currentTrack = null;
        currentArtist = null;
        lastTrack = null;
    }

    public static void updateCurrentTrack() {

        isSpotifyRunning = isSpotifyActive();

        if (!isSpotifyRunning) {
            reset();
            return;
        }

        String[] metadata = getCurrentTrack();

        if (metadata == null) {
            reset();
            return;
        }

        String artist = metadata[0];
        String track = metadata[1];

        if (artist == null || track == null) {
            reset();
            return;
        }

        currentArtist = artist.trim();
        currentTrack = track.trim();
    }

    public static boolean isSpotifyActive() {

        AtomicBoolean isRunning = new AtomicBoolean(false);
        Stream<ProcessHandle> liveProcesses = ProcessHandle.allProcesses();

        liveProcesses.filter(ProcessHandle::isAlive).forEach(ph ->
                ph.info().command().ifPresent(command ->
                {
                    if (command.contains("Spotify") || command.contains("spotify")) {
                        isRunning.set(true);
                    }
                }));

        return isRunning.get();
    }

    public static boolean hasMedia() {
        return isSpotifyRunning && currentTrack != null && currentArtist != null;
    }

    public static String getMedia() {
        if (!hasMedia()) return "No song is playing.";
        return currentArtist + " - " + currentTrack;
    }

    public static String[] getCurrentTrack() {
        ArrayList<String> results = new ArrayList<>();
        try {

            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "for /f \"tokens=* skip=9 delims= \" %g in ('tasklist /v /fo list /fi \"imagename eq spotify*\"') do @echo %g");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("Window Title:")) {
                        results.add(line);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }

        if (results.isEmpty()) return null;

        for (String line : results) {
            if (line.contains("-")) {
                String song = line.replace("Window Title: ", "");

                return song.split(" - ", 2);
            }
        }
        return null;
    }
}