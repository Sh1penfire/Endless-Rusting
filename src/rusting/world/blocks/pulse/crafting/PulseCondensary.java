package rusting.world.blocks.pulse.crafting;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import rusting.Varsr;
import rusting.content.*;
import rusting.interfaces.PulseBlockc;
import rusting.interfaces.Pulsec;

import static mindustry.Vars.*;

//takes a lot of Pulse, and condenses it into crystals.
public class PulseCondensary extends PulseGenericCrafter{
    public float range = 125;
    public float reload = 5;
    public float gatherAmount = 15;
    //minimum size to not get damaged
    public int minSize = 4;
    //scales with size
    public float gatherDamage = 40;
    //effect on damaged blocks
    public Effect damageEffect = Fx.steam;

    public PulseCondensary(String name) {
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team) {
        boolean condensaryFound = Vars.indexer.findTile(team, tile.worldx(), tile.worldy(), range * 1.5f, b -> b instanceof PulseCondensaryBuild) != null;
        return !condensaryFound && super.canPlaceOn(tile, team);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Tile tile = world.tile(x, y);

        if(tile != null) {
            //shoudn't be able to shoot
            Lines.stroke(1f);
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, chargeColourEnd);
            Draw.reset();
            indexer.eachBlock(Vars.player.team(), tile.worldx(), tile.worldy(), range, b -> b instanceof PulseBlockc, b -> {
                if(world.build(x, y) == b) return;
                Draw.color(Palr.pulseBullet, Palr.pulseChargeStart, Mathf.sign(Time.time / 100));
                Draw.alpha(Mathf.absin(Time.time / 100, 0.55f) + 0.35f);
                Fill.rect(b.x, b.y, b.block.size * 8, b.block.size * 8);
            });
        }
        if(tile != null && !canPlaceOn(tile, player.team())){
            if(!Varsr.research.researched(Vars.player.team(), this, this.researchTypes)) drawPlaceText(Core.bundle.get(validCenter(player.team()) ? "bar.requitesresearching" : "bar.dosnthavecenter"), x, y, valid);
            else drawPlaceText(Core.bundle.format("bar.toclosecondensary", this.localizedName), x, y, valid);
        }
    }

    public class PulseCondensaryBuild extends PulseGenericCrafterBuild{
        public float siphonTime = 0;
        public Seq<Building> gatheredTiles = Seq.with();

        @Override
        public void updateTile() {
            super.updateTile();
            if(siphonTime >= reload){
                Building build = Units.findAllyTile(team, x, y, range, b -> b != this && b instanceof PulseBlockc && !gatheredTiles.contains(b) && ((PulseBlockc) b).pulseModule().pulse >= gatherAmount);

                if(build == null){
                    //run out of tiles to take from, clear Seq
                    gatheredTiles.clear();
                }
                else {
                    PulseBlockc building = (PulseBlockc) build;
                    if (receivePulse(gatherAmount, (Pulsec) building)) {
                        building.removePulse(gatherAmount);
                    }
                    gatheredTiles.add(build);
                    siphonTime -= reload;
                    if(build.block.size < minSize){
                        build.damage(gatherDamage);
                        damageEffect.at(build.x, build.y, 0, this);
                    }
                }
            }
            else siphonTime += efficiency() * Time.delta * 1 * pulseEfficiency();
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            if(state.rules.damageExplosions) Damage.damage(x, y, tilesize * block.size * range/8f / 2.0f, 12500f);
            float tx = x, ty = y;
            for (int i = 0; i < 8; i++) {
                Time.run(i * 4 + Mathf.random(3), () -> {
                    Tmp.v1.trns(Mathf.random(360), Mathf.random(35));
                    RustingBullets.boltingVortex.create(this, tx + Tmp.v1.x, ty + Tmp.v1.y, Tmp.v1.angle());
                });
            }
            Fxr.pulseSmoke.at(x, y, 0, new Float[]{range * 8, 5f});
            Fxr.craeNukebuthehe.at(x, y, Mathf.random(0, 360));
            Sounds.laserblast.at(x, y, 0.35f);
        }

        @Override
        public void craft() {
            super.craft();
            Units.nearby(x - range, y - range, range * 2, range * 2, u -> {
                u.damage(35);
                u.damagePierce(15);
                u.apply(RustingStatusEffects.macrosis, 800);
            });
        }

        //if one of you dingusese somehow place these down to close together... well I'm sorry but no.
        @Override
        public void placed() {
            super.placed();
            PulseCondensaryBuild building = (PulseCondensaryBuild) Vars.indexer.findTile(team, tile.worldx(), tile.worldy(), range * 1.5f, b -> b instanceof PulseCondensaryBuild && b != this);
            if(building != null){
                kill();
                building.kill();
            }
        }
    }
}
