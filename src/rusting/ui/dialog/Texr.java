package rusting.ui.dialog;

import arc.Core;
import arc.scene.style.Drawable;
import mindustry.core.Version;
import mindustry.gen.Tex;

public class Texr {
    public static Drawable button, filledButton;

    public static void load() {
        button = Version.number >= 7 ? Core.atlas.getDrawable("button") : Tex.button;
        filledButton = Core.atlas.getDrawable("endless-rusting-button-filled");
    }
}
