package rusting.world.blocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock;
import rusting.content.RustingUnits;
import rusting.entities.units.PlayerUnitType;

public class PlayerCore extends CoreBlock {
    private static boolean canSpawn = false;

    public float constructTime = 150;
    public float targetScale = Scl.scl(1);
    public TextureRegion unitRegion;
    public Seq<PlayerUnitType> unitTypes = Seq.with();

    public Seq<PlayerUnitType> playerUnitTypes(){
        return unitTypes;
    };

    public PlayerCore(String name) {
        super(name);
        config(Byte[].class, (build, b) -> {
            PlayerCoreBuild core = ((PlayerCoreBuild) build);
            for(byte id : b){
                Player spawn = Groups.player.getByID(id);
                if(spawn != null) {
                    if(!core.que.contains(spawn)) core.que.add(spawn);
                    else {
                        switch (b[1]) {
                            case 1:
                                core.que.remove(spawn);
                            case 2:
                                core.togglePause(spawn);
                        }
                    }
                };
            }
            //Log.info("configed with\n|player id: " + b[0] + "\n|interrupt: " + b[1]);
        });
        config(Integer.class, (build, b) -> {
            PlayerCoreBuild core = ((PlayerCoreBuild) build);
            core.setType((PlayerUnitType) Vars.content.units().get(b));
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
        //used for saving
        public int typeId;

        public IntSeq paused = IntSeq.with();

        public PlayerUnitType type = RustingUnits.glimpse;

        public void setType(PlayerUnitType type){
            this.type = type;
            typeId = type.id;
        }

        public void respawn(byte playerId, byte interrupt){
            configure(new Byte[]{playerId, interrupt});
        }

        public void pause(byte playerId){
            configure(new Byte[]{playerId, 2});
        }

        public void togglePause(Player player){
            que.remove(player);
            if(paused.contains(player.id)) {
                paused.removeValue(player.id);
                if(!que.contains(player)) que.add(player);
                return;
            }
            paused.add(player.id);
            if(que.contains(player)) que.remove(player);
        }

        @Override
        public void update(){
            super.update();
            if(que.size > 0 && type != null) {
                progress += Time.delta;
                constructPos.set(x, y);
                Player p = que.get(0);
                canSpawn = true;
                if(paused.contains(p.id)){
                    canSpawn = false;
                    if(que.size > 1) {
                        que.remove(p);
                        que.add(p);
                    }
                }
                if(false && p == Vars.player){
                    Vars.renderer.setScale(targetScale);
                    Core.camera.position.set(x, y);
                }
                if(progress >= constructTime && canSpawn) {
                    progress = 0;
                    Unit coreUnit = type.spawn(team, constructPos.x, constructPos.y);
                    coreUnit.spawnedByCore = true;
                    que.remove(0);
                    Call.unitControl(p, coreUnit);
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            if(que.size > 0 && type != null) {
                Lines.line(this.x, this.y, constructPos.x, constructPos.y);
                Draw.alpha(progress/constructTime);
                Draw.rect(unitRegion, constructPos.x, constructPos.y);
            }
        }

        public void requestSpawn(Player player){
            configure(new Byte[]{(byte) player.id, 0});
        }
    }
}
