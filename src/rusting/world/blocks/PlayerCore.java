package rusting.world.blocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock;
import rusting.net.Call;

public class PlayerCore extends CoreBlock {
    public float constructTime = 150;
    public float targetScale = Scl.scl(1), moveSpeed = 0.006f;
    public TextureRegion unitRegion;

    public PlayerCore(String name) {
        super(name);
        config(Byte[].class, (build, b) -> {
            PlayerCoreBuild core = ((PlayerCoreBuild) build);
            for(byte id : b){
                Player spawn = Groups.player.getByID(id);
                if(spawn != null && !core.que.contains(spawn)) core.que.add(spawn);
            }
        });
    }

    @Override
    public void load() {
        super.load();
        unitRegion = Core.atlas.find(unitType.name + "-full");
    }

    public class PlayerCoreBuild extends CoreBuild{
        public Seq<Player> que = Seq.with();
        public Vec2 constructPos = new Vec2();
        public float progress = 0;

        @Override
        public void update(){
            super.update();
            if(que.size > 0) {
                progress += Time.delta;
                constructPos.set(x, y);
                Player p = que.get(0);
                if(p == Vars.player){
                    Vars.renderer.setScale(targetScale);
                    Core.camera.position.set(x, y);
                }
                if(progress >= constructTime) {
                    progress = 0;
                    Unit coreUnit = unitType.spawn(team, constructPos.x, constructPos.y);
                    que.remove(0);
                    Call.playerControl(p, coreUnit);
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            if(que.size > 0) {
                Lines.line(this.x, this.y, constructPos.x, constructPos.y);
                Draw.alpha(progress/constructTime);
                Draw.rect(unitRegion, constructPos.x, constructPos.y);
            }
        }

        public void requestSpawn(Player player){
            configure(new Byte[]{(byte) player.id});
        }
    }
}
