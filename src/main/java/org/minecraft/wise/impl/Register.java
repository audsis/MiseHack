package org.minecraft.wise.impl;

import org.minecraft.wise.api.management.*;
import org.minecraft.wise.api.utils.math.FramerateCounter;
import org.minecraft.wise.api.utils.spotify.SpotifyUtils;
import org.minecraft.wise.impl.features.commands.*;
import org.minecraft.wise.impl.features.hud.*;
import org.minecraft.wise.impl.features.modules.client.*;
import org.minecraft.wise.impl.features.modules.combat.*;
import org.minecraft.wise.impl.features.modules.misc.*;
import org.minecraft.wise.impl.features.modules.movement.*;
import org.minecraft.wise.impl.features.modules.player.*;
import org.minecraft.wise.impl.features.modules.render.*;

public class Register {

    public static Register INSTANCE;

    public void registerManagers() {
        SavableManager.INSTANCE = new SavableManager();
        MacroManager.INSTANCE = new MacroManager();
        FeatureManager.INSTANCE = new FeatureManager();
        BindManager.INSTANCE = new BindManager();
        CommandManager.INSTANCE = new CommandManager();
        FriendManager.INSTANCE = new FriendManager();
        TPSManager.INSTANCE = new TPSManager();
        FramerateCounter.INSTANCE = new FramerateCounter();
        FontManager.INSTANCE = new FontManager();
        RotationManager.INSTANCE = new RotationManager();
        InventoryManager.INSTANCE = new InventoryManager();
        TimerManager.INSTANCE = new TimerManager();
    }

    public void registerFeatures() {
        // Modules
        FeatureManager.INSTANCE.getFeatures().add(new ClickGUI());
        FeatureManager.INSTANCE.getFeatures().add(new GangWarrior());
        FeatureManager.INSTANCE.getFeatures().add(new Watermark());
        FeatureManager.INSTANCE.getFeatures().add(new ChatSuffix());
        FeatureManager.INSTANCE.getFeatures().add(new HudColors());
        FeatureManager.INSTANCE.getFeatures().add(new Coords());
        FeatureManager.INSTANCE.getFeatures().add(new Info());
        FeatureManager.INSTANCE.getFeatures().add(new FeatureList());
        FeatureManager.INSTANCE.getFeatures().add(new PvpInfo());
        FeatureManager.INSTANCE.getFeatures().add(new Sprint());
        FeatureManager.INSTANCE.getFeatures().add(new Parkour());
        FeatureManager.INSTANCE.getFeatures().add(new Speed());
        FeatureManager.INSTANCE.getFeatures().add(new Undead());
        FeatureManager.INSTANCE.getFeatures().add(new AutoFish());
        FeatureManager.INSTANCE.getFeatures().add(new Compass());
        FeatureManager.INSTANCE.getFeatures().add(new ArmorHud());
        FeatureManager.INSTANCE.getFeatures().add(new Phase());
        FeatureManager.INSTANCE.getFeatures().add(new NoAccel());
        FeatureManager.INSTANCE.getFeatures().add(new Spammer());
        FeatureManager.INSTANCE.getFeatures().add(new UnicodeSpammer());
        FeatureManager.INSTANCE.getFeatures().add(new AntiDeathScreen());
        FeatureManager.INSTANCE.getFeatures().add(new DeathEffects());
        FeatureManager.INSTANCE.getFeatures().add(new AutoTotem());
        FeatureManager.INSTANCE.getFeatures().add(new FullBright());
        FeatureManager.INSTANCE.getFeatures().add(new TimeChanger());
        FeatureManager.INSTANCE.getFeatures().add(new XCarry());
        FeatureManager.INSTANCE.getFeatures().add(new AntiPotions());
        FeatureManager.INSTANCE.getFeatures().add(new Shaders());
        FeatureManager.INSTANCE.getFeatures().add(new Velocity());
        FeatureManager.INSTANCE.getFeatures().add(new AutoBreak());
        FeatureManager.INSTANCE.getFeatures().add(new AutoFeetPlace());
        FeatureManager.INSTANCE.getFeatures().add(new Blink());
        FeatureManager.INSTANCE.getFeatures().add(new AntiHunger());
        FeatureManager.INSTANCE.getFeatures().add(new FastFall());
        FeatureManager.INSTANCE.getFeatures().add(new IceSpeed());
        FeatureManager.INSTANCE.getFeatures().add(new Yaw());
        FeatureManager.INSTANCE.getFeatures().add(new NoSoundLag());
        FeatureManager.INSTANCE.getFeatures().add(new AutoRespawn());
        FeatureManager.INSTANCE.getFeatures().add(new CrystalAura());
        FeatureManager.INSTANCE.getFeatures().add(new FakePlayer());
        FeatureManager.INSTANCE.getFeatures().add(new Manager());
        FeatureManager.INSTANCE.getFeatures().add(new AntiAFK());
        FeatureManager.INSTANCE.getFeatures().add(new FastClimb());
        FeatureManager.INSTANCE.getFeatures().add(new EntityControl());
        FeatureManager.INSTANCE.getFeatures().add(new KillAura());
        FeatureManager.INSTANCE.getFeatures().add(new PortalGodMode());
        FeatureManager.INSTANCE.getFeatures().add(new BetterPortals());
        FeatureManager.INSTANCE.getFeatures().add(new NoFall());
        FeatureManager.INSTANCE.getFeatures().add(new AutoWalk());
        FeatureManager.INSTANCE.getFeatures().add(new AntiCrash());
        FeatureManager.INSTANCE.getFeatures().add(new ExtraPlace());
        FeatureManager.INSTANCE.getFeatures().add(new MultiTask());
        FeatureManager.INSTANCE.getFeatures().add(new ViewClip());
        FeatureManager.INSTANCE.getFeatures().add(new ESP());
        FeatureManager.INSTANCE.getFeatures().add(new RPC());
        FeatureManager.INSTANCE.getFeatures().add(new NoSlow());
        FeatureManager.INSTANCE.getFeatures().add(new LongJump());
        FeatureManager.INSTANCE.getFeatures().add(new NoRender());
        FeatureManager.INSTANCE.getFeatures().add(new AspectRatio());
        FeatureManager.INSTANCE.getFeatures().add(new AutoLog());
        FeatureManager.INSTANCE.getFeatures().add(new AntiCheat());
        FeatureManager.INSTANCE.getFeatures().add(new FontMod());
        FeatureManager.INSTANCE.getFeatures().add(new Chat());
        FeatureManager.INSTANCE.getFeatures().add(new Criticals());
        FeatureManager.INSTANCE.getFeatures().add(new AutoReplenish());
        FeatureManager.INSTANCE.getFeatures().add(new Announcer());
        FeatureManager.INSTANCE.getFeatures().add(new HudEditor());
        FeatureManager.INSTANCE.getFeatures().add(new Ambience());
        FeatureManager.INSTANCE.getFeatures().add(new HoleESP());
        FeatureManager.INSTANCE.getFeatures().add(new BlockHighlight());
        FeatureManager.INSTANCE.getFeatures().add(new Trajectories());
        FeatureManager.INSTANCE.getFeatures().add(new FastUse());
        FeatureManager.INSTANCE.getFeatures().add(new Step());
        FeatureManager.INSTANCE.getFeatures().add(new NotificationsHud());
        FeatureManager.INSTANCE.getFeatures().add(new Warner());
        FeatureManager.INSTANCE.getFeatures().add(new MiddleClick());
        FeatureManager.INSTANCE.getFeatures().add(new ExtraTab());
        FeatureManager.INSTANCE.getFeatures().add(new Nametags());
        FeatureManager.INSTANCE.getFeatures().add(new TextRadar());
        FeatureManager.INSTANCE.getFeatures().add(new LagNotifier());
        FeatureManager.INSTANCE.getFeatures().add(new ViewmodelChanger());
        FeatureManager.INSTANCE.getFeatures().add(new WeatherChanger());
        FeatureManager.INSTANCE.getFeatures().add(new CustomSky());
        FeatureManager.INSTANCE.getFeatures().add(new ParticleEditor());
        FeatureManager.INSTANCE.getFeatures().add(new Welcomer());

        if (WiseMod.isBaritonePresent()) {
            FeatureManager.INSTANCE.getFeatures().add(new Baritone());
        }

        FeatureManager.INSTANCE.getFeatures().add(new Spotify());
        FeatureManager.INSTANCE.getFeatures().add(new TriggerBot());
        FeatureManager.INSTANCE.getFeatures().add(new LogoutESP());
        FeatureManager.INSTANCE.getFeatures().add(new FastLatency());
        FeatureManager.INSTANCE.getFeatures().add(new HitboxDesync());
        FeatureManager.INSTANCE.getFeatures().add(new PlayerTweaks());
        FeatureManager.INSTANCE.getFeatures().add(new Timer());
        FeatureManager.INSTANCE.getFeatures().add(new TickSpeed());
        FeatureManager.INSTANCE.getFeatures().add(new FovModifier());
        FeatureManager.INSTANCE.getFeatures().add(new Chams());

        // Commands
        CommandManager.INSTANCE.getCommands().add(new Goat());
        CommandManager.INSTANCE.getCommands().add(new Macros());
        CommandManager.INSTANCE.getCommands().add(new Friend());
        CommandManager.INSTANCE.getCommands().add(new Bind());
        CommandManager.INSTANCE.getCommands().add(new Enable());
        CommandManager.INSTANCE.getCommands().add(new SpammerFile());
        CommandManager.INSTANCE.getCommands().add(new Help());
        CommandManager.INSTANCE.getCommands().add(new Modules());
        CommandManager.INSTANCE.getCommands().add(new Coordinates());
    }

    public void registerAll() {
        registerManagers();
        System.out.println("Registering Managers.");
        registerFeatures();
        System.out.println("Registering Features.");
        SpotifyUtils.init();

    }
}
