package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.type.Liquid;
import mindustry.ui.Cicon;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PumpLiquidTurret extends LiquidTurret {

    public float pumpSpeed = 0.045f;
    public float effectChance = 0.024f;

    public PumpLiquidTurret(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, 60f * pumpSpeed * size * size, StatUnit.liquidSecond);

    }

    public boolean canPump(Tile tile){
        return  tile.floor().liquidDrop != null && ammoTypes.get(tile.floor().liquidDrop) != null;
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        float amount = 0f;
        Liquid liquidDrop = null;

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canPump(other)){
                liquidDrop = other.floor().liquidDrop;
                amount += other.floor().liquidMultiplier;
            }
        }

        if(liquidDrop != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.pumpspeed", amount * pumpSpeed * 60f, 0), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(liquidDrop.icon(Cicon.small), dx, dy - 1);
            Draw.reset();
            Draw.rect(liquidDrop.icon(Cicon.small), dx, dy);
        }
    }

    public class PumpLiquidTurretBuild extends LiquidTurretBuild{
        @Override
        public void updateTile() {
            super.updateTile();
            if((liquids().current() == tile.floor().liquidDrop || canPump(tile)) && liquids().currentAmount() + pumpSpeed * Time.delta < liquidCapacity) {
                liquids.add(tile.floor().liquidDrop, pumpSpeed * Time.delta);

                float worldSize = size * tilesize;
                if(Mathf.chance(effectChance)) tile.floor().liquidDrop.effect.effect.at(x + Mathf.random(worldSize) - worldSize/2, y + Mathf.random(worldSize) - worldSize/2);
            }
        }
    }
}
