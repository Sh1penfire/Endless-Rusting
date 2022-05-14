package rusting.type.statusEffect;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Weapon;

public class CrystalStatusEffect extends ConsStatusEffect {
    public TextureRegion crystalRegion;
    public float colorOpacity;
    public Color drawColor;
    //max hitsize before untis become auto immune
    public float hitSizeMax = -1;
    Color crystalDrawColor;
    public static Seq<CrystalStatusEffect> crystalStatusEffectSeq = Seq.with();

    @Override
    public void load() {
        super.load();
        crystalRegion = Core.atlas.find(name + "-crystal");
        crystalStatusEffectSeq.add(this);
        PixmapRegion region = Core.atlas.getPixmap(name + "-crystal");
        //spagehti...
        crystalDrawColor = new Color(region.get((int) region.width/2, (int) region.height/2));
    }

    public CrystalStatusEffect(String name) {
        super(name);
        this.speedMultiplier = 0;
        this.reloadMultiplier = 1.35f;
        this.drawColor = Color.sky;
        this.colorOpacity = 0.1f;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Draw.mixcol();
        Draw.color(drawColor, Color.white, (float) (Mathf.absin(Time.time, 1) * 0.45 + 0.35));
        Draw.z(Mathf.lerpDelta(Layer.groundUnit, Layer.flyingUnit, unit.elevation) + 0.1f);
        Draw.alpha((float) (Mathf.absin(Time.time, 1) * 0.45 + 0.35));
        Draw.rect(crystalRegion, unit.x, unit.y, unit.type.hitSize * 128/crystalRegion.width, unit.type.hitSize * 128/crystalRegion.height, unit.rotation);

        Draw.alpha(1);

        Draw.color(crystalDrawColor, crystalDrawColor, 1);
        Draw.rect(unit.type.region, unit.x, unit.y, unit.rotation - 90);
        Draw.rect(unit.type.outlineRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.color(crystalDrawColor.lerp(unit.type.cellColor(unit), unit.healthf()), crystalDrawColor,Mathf.absin(Time.time, 1));
        Draw.rect(unit.type.cellRegion, unit.x, unit.y, unit.rotation - 90);

        Draw.color(crystalDrawColor, 1);
        for(WeaponMount mount : unit.mounts){
            Weapon weapon = mount.weapon;

            float rotation = unit.rotation - 90;
            float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
            float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
                    wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
            float outlineSpace = 0.01f;
            if(weapon.shadow > 0){
                Drawf.shadow(wx, wy, weapon.shadow);
            }

            if(weapon.outlineRegion.found()){
                float z = Draw.z();
                if(!weapon.top) Draw.z(z - outlineSpace);

                Draw.rect(weapon.outlineRegion,
                        wx, wy,
                        weapon.outlineRegion.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                        weapon.region.height * Draw.scl,
                        weaponRotation);

                Draw.z(z);
            }

            Draw.rect(weapon.region,
                    wx, wy,
                    weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                    weapon.region.height * Draw.scl,
                    weaponRotation);

            if(weapon.heatRegion.found() && mount.heat > 0){
                Draw.color(weapon.heatColor, crystalDrawColor, mount.heat);
                Draw.blend(Blending.additive);
                Draw.rect(weapon.heatRegion,
                        wx, wy,
                        weapon.heatRegion.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                        weapon.heatRegion.height * Draw.scl,
                        weaponRotation);
                Draw.blend();
                Draw.color();
            }
        }
        Drawf.light(unit.x, unit.y, unit.type.hitSize, crystalDrawColor, colorOpacity);

        Draw.reset();
    }
}
