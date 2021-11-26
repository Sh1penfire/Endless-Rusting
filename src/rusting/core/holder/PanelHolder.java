package rusting.core.holder;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Layer;
import rusting.world.blocks.defense.turret.PanelTurret;

public class PanelHolder {
    public String name;
    public float panelDampening = 3f;
    public double panelX = 0, panelY = 0;
    public TextureRegion panelRegion, panelOutlineRegion, panelHeatRegion;
    public BulletType shootType;
    //can be used for shooting panels and others;
    public boolean independentBehaviour = false, useTurretAmmo = true;

    //Always use turret name for one panel, only add onto turret name for multiple panels.
    public PanelHolder(String name) {
        this.name = name;
    }

    public void load(){
        panelRegion = Core.atlas.find(name + "-panel", Core.atlas.find("clear"));
        panelOutlineRegion = Core.atlas.find(name + "-panel-outline", Core.atlas.find("clear"));
        panelHeatRegion = Core.atlas.find(name + "-panel-heat", panelRegion);
    }

    public float rotation(){
        return 0;
    }

    public float rotation(PanelTurret.PanelTurretBuild Turret){
        return Turret.rotation;
    }

    public void shoot(PanelTurret.PanelTurretBuild Turret){
        
    }
    
    public void update(PanelTurret.PanelTurretBuild Turret){
        //empty method left for different panel holders
    }

    public float px(double heat, double recoil){
        return (float) (panelX * (1 + heat/panelDampening - recoil/3));
    }

    public float py(double heat, double recoil){
        return (float) (panelY - recoil * heat);
    }

    public float getX(PanelTurret.PanelTurretBuild Turret){
        return (float) Turret.x + Angles.trnsx(Turret.rotation - 90, px(Turret.heat, Turret.recoil), py(Turret.heat, Turret.recoil));
    }

    public float getY(PanelTurret.PanelTurretBuild Turret){
        return (float) Turret.y + Angles.trnsy(Turret.rotation - 90, px(Turret.heat, Turret.recoil), py(Turret.heat, Turret.recoil));
    }

    public void draw(PanelTurret.PanelTurretBuild Turret){
        Draw.z(Layer.turret - 0.1f);
        //typecasting required, for more precise panels
        float rotation = Turret.rotation - 90;
        double reload = Turret.reload, reloadTime = ((PanelTurret) Turret.block).reloadTime;
        Color heatColor = ((PanelTurret) Turret.block).heatColor;
        float panx = getX(Turret), pany = getY(Turret);
        Draw.z(Layer.turret - 1);
        Draw.rect(panelRegion, panx, pany, rotation);
        Draw.rect(panelOutlineRegion, panx, pany, rotation);
        Draw.color(heatColor, heatColor, (float) (panelRegion == panelHeatRegion ? reload/reloadTime/4 : reload/reloadTime));
        Draw.alpha((float) (panelRegion == panelHeatRegion ? reload/reloadTime/4 : reload/reloadTime));
        Draw.rect(panelHeatRegion, panx, pany, rotation);
        Draw.reset();
    }
}
