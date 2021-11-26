package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.world.Tile;
import rusting.math.Mathr;

public class ChainHealerBeamTurret extends HealerBeamTurret{
    public int chains = 3;
    public float chainRange = 35;
    private static Seq<Building> builds = Seq.with(), builds2 = Seq.with();

    public ChainHealerBeamTurret(String name) {
        super(name);
    }

    public class ChainHealerBeamTurretBuild extends HealerBeamTurretBuild{
        //stands for chained target position x and chained target position y
        public Seq<Vec2> CTP = new Seq(chains);

        public Vec2 orbPos = new Vec2();

        @Override
        public void updateTile() {
            super.updateTile();
            if(isShooting()) setLastP();
            orbPos.lerp(timeSinceTurn >= stillTime && (isShooting() || beamAlpha > 0.1) ? LTP : COOP, 0.05f * Time.delta);
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Draw.color();

            Draw.z(Layer.turret);

            tr2.trns(rotation, -recoil);

            Drawf.shadow(region, x + tr2.x - elevation, y + tr2.y - elevation, rotation - 90);
            drawer.get(this);

            if(heatRegion != Core.atlas.find("error")){
                heatDrawer.get(this);
            }

            Draw.reset();
            for (int i = 0; i < effectRotations.length; i++){
                int index = i + 1;
                Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), effectAlphas[i]);
                Draw.alpha(effectAlphas[i]/3/(index * alphaFalloff));
                Lines.square(COOP.x, COOP.y, 2 + maxEffectSize * effectAlphas[i], effectRotations[i]);
            }

            if(beamAlpha > 0.005f) {
                Draw.z(Layer.bullet);
                float pulsate = Mathr.helix(8, 1, 1, this.beamAlpha * this.beamAlpha) * 0.25f + this.beamAlpha * 0.75f;
                Draw.alpha(this.beamAlpha);
                Lines.stroke(pulsate * 6);
                Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), this.beamAlpha);
                Lines.line(COOP.x, COOP.y, orbPos.x, orbPos.y);
                Fill.circle(COOP.x, COOP.y, pulsate * 7);
                Fill.circle(orbPos.x, orbPos.y, pulsate * 7);
                Draw.color(Color.valueOf("#ffffff"), Color.valueOf("#e8ffd7"), this.beamAlpha);
                Lines.stroke(beamAlpha * 4.5f);
                Lines.line(COOP.x, COOP.y, orbPos.x, orbPos.y);
                Fill.circle(COOP.x, COOP.y, pulsate * 3);
                Fill.circle(orbPos.x, orbPos.y, pulsate * 3);
                Fill.light(orbPos.x, orbPos.y, 8, beamAlpha * 15, Tmp.c1.set(Color.white).a(0.55f * beamAlpha), Tmp.c2.set(Pal.heal).a(0.15f * beamAlpha));
                Draw.color(Color.white, Pal.heal, this.beamAlpha * this.beamAlpha);
                Lines.circle(orbPos.x, orbPos.y, 8 - 3 * this.beamAlpha * this.beamAlpha);

                for (int i = 0; i < CTP.size; i++) {
                    if (i == 0) Tmp.v1.set(orbPos);
                    else Tmp.v1.set(CTP.get(i - 1));
                    Tmp.v2.set(CTP.get(i));

                    Draw.z(Layer.bullet);
                    Draw.alpha(this.beamAlpha);
                    Lines.stroke(pulsate * 3);
                    Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), this.beamAlpha);
                    Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, pulsate * 3);
                    Fill.circle(Tmp.v2.x, Tmp.v2.y, pulsate * 3);
                    Draw.color(Color.valueOf("#ffffff"), Color.valueOf("#e8ffd7"), this.beamAlpha);
                    Lines.stroke(pulsate);
                    Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, pulsate);
                    Fill.circle(Tmp.v2.x, Tmp.v2.y, pulsate);

                    Draw.color(Color.white, Pal.heal, this.beamAlpha * this.beamAlpha);
                    Lines.circle(Tmp.v2.x, Tmp.v2.y, 4 - 1.5f * this.beamAlpha * this.beamAlpha);
                }
            }
        }

        @Override
        public void healTargets(){
            if((logicControlled() || isControlled()) && within(targetPos, range)){
                Tmp.v2.set(orbPos);
                chainHealTargets();
            }
            else if(target != null && target instanceof Building && ((Building) target).damaged() && validateTarget()){
                Tmp.v2.set(orbPos);
                chainHealTargets();
            }
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            for(int i = 0; i < chains; i++) {
                CTP.add(new Vec2());
            }
            return super.init(tile, team, shouldAdd, rotation);
        }

        public void chainHealTargets(){
            builds.clear();
            builds2.clear();
            Building TempBuild;
            for (int i = 0; i < chains; i++) {
                Vars.indexer.eachBlock(team, Tmp.v2.x, Tmp.v2.y, chainRange, b -> b.damaged() && !builds2.contains(b), b -> builds.add(b));
                if(builds.size == 0) return;
                if(i == 0) TempBuild = builds.sort(b -> b.dst(orbPos)).get(0);
                else TempBuild = builds.sort(Building::healthf).get(0);
                if(TempBuild != null && TempBuild.team == team && TempBuild.damaged()){
                    recoil = recoilAmount;
                    heat = 1;
                    healBuilding(TempBuild);
                    Tmp.v2.set(TempBuild.x, TempBuild.y);
                    CTP.get(i).set(Tmp.v2);
                    builds2.add(TempBuild);
                }
            }
        }
    }
}
