package rusting.world.blocks.pulse.defense;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import rusting.content.RustingAchievements;
import rusting.world.blocks.pulse.PulseBlock;

public class PulseParticleSpawner extends PulseBlock {

    public Effect[] effects = {Fx.smoke};
    public float effectFrequency = 0.65f, consumeFrequency = 360, lightRadius = 0, lightAlpha = 1, warmupRate = 0.05f;
    public Color lightColor = Color.white;

    public PulseParticleSpawner(String name) {
        super(name);
        baseEfficiency = 0.15f;
        configurable = true;
        saveConfig = true;

        config(Integer.class, (PulseParticleSpawnerBuild entity, Integer i) -> {
            Log.info(i);
            entity.state = i;
            entity.clampState();
        });
    }

    public class PulseParticleSpawnerBuild extends PulseBlockBuild{

        int state = 0;
        float particleSpawnInterval = 0, consumeTimer = 0;
        float warmup = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(allConsValid()) {
                warmup = Mathf.lerpDelta(warmup, 1, warmupRate);
                if (effects.length > 0) {
                    particleSpawnInterval += pEfficiency() * effectFrequency;
                }

                if (particleSpawnInterval >= 1) {
                    effects[state].at(x, y);
                    particleSpawnInterval = 0;
                }

                if (consumeTimer >= consumeFrequency) {
                    consume();
                    pConsume();
                    consumeTimer = 0;
                }
                consumeTimer++;
            }
            else warmup = Mathf.clamp(warmup - warmupRate * Time.delta, 0, 1);
        }

        @Override
        public Object config() {
            return state;
        }

        public void clampState(){
            state = Mathf.clamp(state, 0, effects.length);
        }

        @Override
        public void buildConfiguration(Table table) {
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            Table buttons = new Table();

            for(int l = 0; l < effects.length; l++){
                final int i = l;
                buttons.button(String.valueOf(i), () -> {
                    configure(i);
                    if(!RustingAchievements.powerfulLight.unlocked()) RustingAchievements.powerfulLight.unlock();
                }).size(44);
            }
            table.add(buttons);
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.i(state);
            w.f(particleSpawnInterval);
            w.f(consumeTimer);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            state = r.i();
            //n mn m nmn mn mn nm nm jm njkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk,
            particleSpawnInterval = r.f();
            consumeTimer = r.f();
        }

        @Override
        public void draw() {
            super.draw();
            if(lightRadius > 0) Drawf.light(x, y, lightRadius * warmup, lightColor, lightAlpha * warmup);
        }
    }
}
