package rusting.world.blocks.defense.turret.power;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import rusting.core.holder.PanelHolder;

//Multi panel panel now supported
public class PanelTurret extends PowerTurret {
    public Seq<PanelHolder> panels = new Seq<>();

    @Override
    public void load() {
        super.load();
        panels.each(PanelHolder::load);
    }

    public PanelTurret(String name) {
        super(name);
    }

    public class PanelTurretBuild extends PowerTurretBuild {

        @Override
        protected void shoot(BulletType type) {
            super.shoot(type);
            panels.each(panel ->{
                if(!panel.independentBehaviour) panel.shoot(this);
            });
        }

        public void updateTile(){
            super.updateTile();
            panels.each(panel -> {
                panel.update(this);
            });
        }

        public void draw(){

            super.draw();
            Draw.reset();

            panels.each(panel -> {
                panel.draw(this);
            });
        }
    }
}
