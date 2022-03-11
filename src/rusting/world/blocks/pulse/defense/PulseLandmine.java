package rusting.world.blocks.pulse.defense;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.type.Category;
import mindustry.world.Tile;
import rusting.content.RustingBullets;
import rusting.world.blocks.pulse.PulseBlock;

public class PulseLandmine extends PulseBlock {

    TextureRegion indicatorRegion;

    public float reloadTime = 50;
    public float damageAmount = 10;
    public int shots = 3;
    public Sound shootSound = Sounds.spark;
    public float soundMinPitch = 0.5f, soundMaxPitch = 0.9f;

    @Override
    public void load() {
        super.load();
        indicatorRegion = Core.atlas.find(name +"-state");
    }

    public PulseLandmine(String name) {
        super(name);
        projectile = RustingBullets.craeBolt;
        category = Category.effect;
        targetable = false;
        hasShadow = false;
        solid = false;
    }

    @Override
    public int minimapColor(Tile tile){
        PulseLandmineBuild build = (PulseLandmineBuild)tile.build;
        return build != null && build.team == Vars.player.team() ? build.team.color.rgba() : tile.floor().mapColor.rgba();
    }

    public class PulseLandmineBuild extends PulseBlockBuild{

        //alpha of landmine after being stepped on for enemy teams
        public float drawAlpha = 0;

        //max amount of pulse landmine can store before reaching amx amount of bursts
        protected float maxPulseRequired = pulseCapacity - pulseCapacity % customConsumes.pulse;
        //sates:
        /*
        0: Default state before being set
        1: no cons valid
        2: cons valid, not full on pulse yet
        3: cons valid, full on pulse
         */
        int state = 0;
        protected Color[] stateColours = {Color.blue, Team.crux.color, Team.sharded.color, Team.green.color};

        @Override
        public void placed() {
            super.placed();
            addPulse();
        }

        @Override
        public void update() {
            super.update();
            if(allConsValid()){
                //Most likely never going to be 1, so get max amount of charges that it can store
                if(storage.pulse >= maxPulseRequired){
                    state = 3;
                }
                else state = 2;
            }
            else state = 1;
            drawAlpha = Math.max(drawAlpha - 0.01f * Time.delta, 0);
        }

        @Override
        public void unitOn(Unit unit) {
            if(allConsValid() && timer.get(0, reloadTime)){
                consume();
                customConsume();
                for(int i = 0; i < shots; i++){
                    projectile.create(this, team, x, y, Mathf.range(360));
                }
                shootSound.at(x, y, Mathf.range(soundMinPitch, soundMaxPitch));
                drawAlpha = 1;
                damage(damageAmount);
            }
        }

        @Override
        public void draw() {
            if(Vars.player.team() == team || Vars.player.team() == Team.derelict && team == Team.sharded) {
                super.draw();
                Draw.reset();
                Draw.color(stateColours[state]);
                Draw.rect(indicatorRegion, x, y, 270);
            }
            else if(drawAlpha > 0){
                Draw.alpha(drawAlpha);
                Draw.rect(region, x, y, 270);
                Draw.color(stateColours[state]);
                Draw.alpha(drawAlpha);
                Draw.rect(indicatorRegion, x, y, 270);
            }

            if(tile.floor().emitLight) tile.floor().drawEnvironmentLight(tile);
        }

        @Override
        //don't draw team
        public void drawTeam() {

        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(drawAlpha);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            drawAlpha = r.f();
        }
    }
}
