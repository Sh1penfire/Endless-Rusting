package rusting.world.blocks.defense.turret.healer;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import arc.util.io.Reads;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.*;
import rusting.math.Mathr;

public class AreaHealerBeamTurret extends HealerBeamTurret {

    public float healRadius = 8;
    public int beams = 6;

    public AreaHealerBeamTurret(String name) {
        super(name);
    }

    public class AreaHealerBeamTurretBuild extends HealerBeamTurretBuild{

        public Vec2[] beamPoss = new Vec2[beams];

        @Override
        protected void shoot(BulletType type) {
            if(Vars.indexer.findTile(team, targetPos.x, targetPos.y, healRadius, b -> b.damaged()) != null) {
                setLastP();
                healTargets();
            }
            else if(canShootBullet) super.shoot(type);
        }

        @Override
        public void healTargets() {
            //clamp off distance
            Tmp.v1.set(LTP.x, LTP.y).sub(x, y).clamp(0, range).add(x, y);
            Vars.indexer.eachBlock(team, Tmp.v1.x, Tmp.v1.y, healRadius, b -> b.damaged(), b -> {
                healBuilding(b);
            });
        }

        @Override
        public void updateTile() {
            super.updateTile();
            for (int i = 0; i < beamPoss.length; i++) {
                beamPoss[i].trns(360/beamPoss.length * i + effectRotations[0], healRadius).add(LTP.x, LTP.y);
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.reset();
            for (int i = 0; i < effectRotations.length; i++){
                int index = i + 1;
                Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), effectAlphas[i]);
                Draw.alpha(effectAlphas[i]/3/(index * alphaFalloff));
                Lines.square(COOP.x, COOP.y, 2 + maxEffectSize * effectAlphas[i], effectRotations[i]);
            }

            if(this.beamAlpha > 0){
                float pulsate = Mathr.helix(8, 1, 1, this.beamAlpha * this.beamAlpha) * 0.25f + this.beamAlpha * 0.75f;
                for (Vec2 LTPb : beamPoss) {
                    float er = effectRotations[0];
                    Draw.z(Layer.bullet);
                    Lines.stroke(pulsate * 3);
                    Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), this.beamAlpha);
                    Draw.alpha(this.beamAlpha);
                    Lines.line(COOP.x, COOP.y, LTPb.x, LTPb.y);
                    Fill.circle(COOP.x, COOP.y, pulsate * 3);
                    Fill.circle(LTPb.x, LTPb.y,pulsate * 3);
                    Draw.color(Color.valueOf("#ffffff"), Color.valueOf("#e8ffd7"), this.beamAlpha);
                    Draw.alpha(this.beamAlpha);
                    Lines.stroke(pulsate);
                    Lines.line(COOP.x, COOP.y, LTPb.x, LTPb.y);
                    Fill.circle(COOP.x, COOP.y, pulsate);
                    Fill.circle(LTPb.x, LTPb.y, pulsate);

                    Draw.color(Color.white, Pal.heal, this.beamAlpha * this.beamAlpha);
                    Lines.circle(COOP.x, COOP.y, 4 - 1.5f * this.beamAlpha * this.beamAlpha);

                        if(!Core.settings.getBool("er.advancedeffects")) {
                            Draw.alpha(beamAlpha);
                            Lines.line(LTPb.x, LTPb.y, LTP.x, LTP.y);
                        }
                    }

                }
                if(Core.settings.getBool("er.advancedeffects")) Fill.light(LTP.x, LTP.y, 10, healRadius, Tmp.c1.set(Color.valueOf("#62ac7d")).a(beamAlpha), Tmp.c2.set(Color.valueOf("#82f48f")).a(beamAlpha));
                else {
                    Draw.color(Color.valueOf("#e8ffd7"), Color.valueOf("#ffffff"), this.beamAlpha);
                    Draw.alpha(this.beamAlpha / 2);
                    Fill.circle(LTP.x, LTP.y, healRadius);
                    Lines.stroke(healRadius / 5 * beamAlpha);
                    Lines.circle(LTP.x, LTP.y, healRadius);
                }
            }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            for (int i = 0; i < beamPoss.length; i++) {
                beamPoss[i] = new Vec2(x, y);
            }
        }

        @Override
        public void created() {
            super.created();
            for (int i = 0; i < beamPoss.length; i++) {
                beamPoss[i] = new Vec2(x, y);
            }
        }
    }
}
