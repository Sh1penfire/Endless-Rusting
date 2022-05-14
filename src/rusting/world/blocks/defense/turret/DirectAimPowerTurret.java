package rusting.world.blocks.defense.turret;

import mindustry.gen.Posc;
import mindustry.world.blocks.defense.turrets.PowerTurret;

//aims directly at target for convenience with homing bullets. I can't believe it's come to this but it'll make my life easier and I just. I just. NEEDEDDDD THIS FOR SO LONG DAMIT WHY DIDN'T I MAKE THIS A FEW MONTHS AGO I COULD HAVE SAVED SO MUCH TIME AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
public class DirectAimPowerTurret extends PowerTurret {

    public DirectAimPowerTurret(String name) {
        super(name);
    }

    public class DirectAimPowerTurretBuild extends PowerTurretBuild{

        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public void targetPosition(Posc pos) {
            targetPos.set(pos.x(), pos.y());
        }
    }
}
