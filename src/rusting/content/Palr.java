package rusting.content;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class Palr {
    public static Color
        pulseChargeStart = Color.valueOf("#c9f0f0"),
        pulseChargeEnd = new Color(Color.sky).lerp(Pal.lightTrail, 0.25f).lerp(Color.valueOf("#a4ddf2"), 0.05f),
        pulseBullet = new Color(Pal.lancerLaser).lerp(Pal.lightPyraFlame, 0.05f).lerp(pulseChargeEnd, 0.1f),
        pulseLaser = Color.valueOf("#8cc8d0"),
        pulseShieldStart = Color.valueOf("#5c79d0"),
        pulseShieldEnd = new Color(Color.blue).lerp(Color.valueOf("#6e8adf"), 0.75f).lerp(Color.black, 0.25f),
        darkerPulseChargeStart = Color.valueOf("#393a4b"),
        chillDecalDark = Color.valueOf("#b6cad6"),
        chillDecalLight = Color.valueOf("#d7e0e5"),
        voidBullet = Color.valueOf("#5d4982"),
        voidBulletBack = Color.valueOf("#342b40"),
        voidLightBullet = Color.valueOf("#9c7ae1"),
        voidLightBulletBack = Color.valueOf("#231841"),
        dustriken = Color.valueOf("#70696c"),
        lightstriken = Color.valueOf("#ddcece"),
        darkPyraBloom = new Color(Pal.darkPyraFlame).lerp(Color.white, 0.15f),
        camaintLiquid = Color.valueOf("#586b5f"),
        camaintLightning = Color.valueOf("#86958c"),
        mhemFactionColour = Color.valueOf("#d4b182"),
        paveFactionColour = Color.valueOf("#83f490"),
        craeFactionColour = Color.valueOf("#7892e0")
    ;
}
