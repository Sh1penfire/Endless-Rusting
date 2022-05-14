package rusting.world.blocks.defense.turret;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Posc;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

public class LiquidBeamTurret extends LiquidTurret {

    //cap for reload gain percentage
    public float pressureCap = 3.85f;

    //how fast pressure builds per tick
    public float pressureBuildSpeed = 0.005f;

    //how fast turret loses pressure
    public float pressureLoseSpeed = 0.025f;


    public LiquidBeamTurret(String name) {
        super(name);
    }

    public class LiquidBeamTurretBuild extends LiquidTurretBuild{
        //how much of the reload is regained after shooting
        public float pressure = 0;

        @Override
        public void update() {
            super.update();
            pressure = Mathf.lerpDelta(pressure, isShooting() ? pressureCap : 0, isShooting() ? pressureBuildSpeed : pressureLoseSpeed);
            reload += pressure * efficiency() * (hasAmmo() ? peekAmmo().reloadMultiplier : 0.5f);
        }

        @Override
        protected void shoot(BulletType type) {
            super.shoot(type);
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(pressure);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            pressure = r.f();
        }

        @Override
        public void targetPosition(Posc pos) {
            targetPos.set(pos.x(), pos.y());
        }
    }
}
