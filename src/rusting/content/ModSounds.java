package rusting.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader.SoundParameter;
import arc.audio.Sound;
import mindustry.Vars;

public class ModSounds {
    protected static Sound loadSound(String soundName) {
        String name = "sounds/" + soundName;
        String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Sound sound = new Sound();

        AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundParameter(sound));
        desc.errored = Throwable::printStackTrace;

        return sound;
    }

    public static Sound
            harpoonLaunch = new Sound(),
            whoosh = new Sound()
            ;

    public static void load() {
        if(Vars.headless) return;

        harpoonLaunch = loadSound("chain-launch");
        whoosh = loadSound("whoosh");
    }
}
