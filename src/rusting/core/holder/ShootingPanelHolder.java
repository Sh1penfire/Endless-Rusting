package rusting.core.holder;

import mindustry.content.Fx;
import mindustry.entities.Effect;
import rusting.world.blocks.defense.turret.PanelTurret;

public class ShootingPanelHolder extends PanelHolder{
    Effect shootEffect = Fx.shootSmall;
    float cooldown = 0.02f, restitution = 0.02f;
    float recoil = 0, heat = 0;
    public float lifetimeMulti = 1, speedMulti = 1;
    public ShootingPanelHolder(String name) {
        super(name);
    }

    @Override
    public void update(PanelTurret.PanelTurretBuild Turret){
    }

    public void effects(PanelTurret.PanelTurretBuild Turret){
        shootEffect.at(getX(Turret), getY(Turret), rotation(Turret));
    }

    @Override
    public void shoot(PanelTurret.PanelTurretBuild Turret){
        shootType.create(Turret, Turret.team, getX(Turret), getY(Turret), Turret.rotation, speedMulti, lifetimeMulti);
        if(useTurretAmmo) Turret.useAmmo();
        //like turret shoot code but no
    }
}
//friend blossom_3271