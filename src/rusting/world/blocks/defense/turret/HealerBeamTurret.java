package rusting.world.blocks.defense.turret;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Posc;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import rusting.math.Mathr;

public class HealerBeamTurret extends PowerTurret {

    //offset from the block that the beam starts from
    public float firingDistance = 4;
    //whether the turret fires normally if no target block is found
    public boolean canShootBullet = false;
    //... how many squares the turret has
    public int squares = 5;
    //max size of the squares
    public float maxEffectSize = 6;
    //how much the alpha falls off when drawing multiple squares
    public float alphaFalloff = 0.35f;
    //yes, the turret can heal in percentages, but I'd rather not.
    public float healing = 1;
    //percentage healing option
    public boolean percentageHealing = false;
    //how long the turret has to stay still for before firing
    public float stillTime = 15;

    public boolean requiresWarmup = true;

    public HealerBeamTurret(String name) {
        super(name);
        heatColor = Pal.heal;
    }

    public class HealerBeamTurretBuild extends PowerTurretBuild{

        //stands for last position x and last position y
        public Vec2 LTP = new Vec2(0, 0);
        //Stands for current offset origin position
        public Vec2 COOP = new Vec2(x, y);
        //rotation of glowing squares
        public float effectRotations[] = new float[squares];
        //effect alpha
        public float effectAlphas[] = new float[squares];
        //Alpha of the repair beam
        public float beamAlpha = 0;
        //how long turret hasn't rotated for
        public float timeSinceTurn = 0;
        //last rotation
        float lastRotation = rotation;

        //used to set target last position
        public void setLastP(){
            if(isShooting()){
                if(this.isControlled() || this.logicControlled()){
                    //set the position of the beam to the turret's shooting position
                    LTP.set(
                        targetPos.x,
                        targetPos.y
                    );
                }
                else if(target != null){
                    //set the position of the beam to the target's x and target's y
                    LTP.set(
                        target.x(),
                        target.y()
                    );
                }
            }
        }

        @Override
        protected void updateShooting() {
            if(reload >= reloadTime && !charging){
                BulletType type = peekAmmo();

                shoot(type);

                reload = 0f;
            }else{
                reload += delta() * baseReloadSpeed();
            }
        }

        @Override
        public void updateTile(){
            //Set current origin of the beam to turret's end position by
            COOP.set(
                //Reset the position
                    0,
                    0)
                .trns(
                //Find the edge of the turret using vector translation
                    rotation,
                    firingDistance)
                .add(
                //Then the difference to the turret
                    x,
                    y
                );

            //Lerps current beam alpha and 0
            this.beamAlpha = Mathf.slerpDelta(this.beamAlpha, 0, 0.025f);

            //rotates each square a different amount
            for (int i = 0; i < effectRotations.length; i++){
                int index = i + 1;
                if((!requiresWarmup || timeSinceTurn >= stillTime) && (isShooting() || beamAlpha > 0.1)){
                    effectRotations[i] += efficiency() * Time.delta/index;
                    effectAlphas[i] = Math.min(effectAlphas[i] + Time.delta/60/index, 1);
                }
                else {
                    effectAlphas[i] = Math.max(effectAlphas[i] - Time.delta/25 * index, 0);
                }
                if (effectRotations[i] >= 360) effectRotations[i] -= 360;
            }

            timeSinceTurn += Time.delta * efficiency();

            //run super after to prevent undefined fields
            if(!validateTarget()) target = null;

            //wait what's this?
            wasShooting = false;

            //no
            recoil = Mathf.lerpDelta(recoil, 0f, restitution);
            //no.
            heat = Mathf.lerpDelta(heat, 0f, cooldown);

            //no...
            unit.health(health);
            //No
            unit.rotation(rotation);
            //NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
            unit.team(team);
            //HE'S DONE IT AGAIN
            unit.set(x, y);

            //HE'S SUCH A SMOOTHBRAINDUSTRY
            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            //IDIOT
            if(hasAmmo()){

                //YOU COULD HAVE JUST REWORKED THE EFFECT
                if(timer(timerTarget, targetInterval)){
                    findTarget();
                }

                //WORTHLESS, YOU IMBECILE
                if(validateTarget()){
                    boolean canShoot = true;

                    if(isControlled()){ //this is painfull to watch
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    }else if(logicControlled()){ //like just why woudn't you fix the effect
                        canShoot = logicShooting;
                    }else{ //I know you can see this I I doubt that you care anymore
                        targetPosition(target);

                        if(Float.isNaN(rotation)){
                            rotation = 0;
                        }
                    }

                    float targetRot = angleTo(targetPos);

                    if(shouldTurn()){
                        turnToTarget(targetRot);
                    }

                    if(Angles.angleDist(rotation, targetRot) < shootCone && canShoot && timeSinceTurn >= stillTime){
                        wasShooting = true;
                        updateShooting();
                    }
                }
            }

            if(acceptCoolant){
                updateCooling();
            }
        }

        @Override
        public void targetPosition(Posc pos) {
            targetPos.set(pos);
        }

        @Override
        protected void turnToTarget(float targetRot) {
            super.turnToTarget(targetRot);
            if(rotation != lastRotation && Math.abs(lastRotation - rotation) > shootCone) timeSinceTurn = 0;
            lastRotation = rotation;
        }

        @Override
        protected void findTarget() {
            Seq<Building> buildings = Seq.with();
            Vars.indexer.eachBlock(team, x, y, range, Building::damaged, build -> buildings.add(build));
            if(buildings.size == 0) return;
            target = buildings.sort(Building::healthf).get(0);
        }

        @Override
        protected boolean validateTarget() {
            if(logicControlled() || isControlled() || target instanceof Building && ((Building) target).team == team && ((Building) target).damaged()) return true;
            else if(canShootBullet) return super.validateTarget();
            else return false;
        }

        @Override
        protected void shoot(BulletType type) {
            if(((logicControlled() || isControlled()) && within(targetPos, range)) || target != null && target instanceof Building && ((Building) target).damaged() && validateTarget()) {
                setLastP();
                healTargets();
            }
            else if(canShootBullet) super.shoot(type);
        }

        public void healTarget(Posc targ){
            if(targ instanceof Building) healBuilding((Building) targ);
        }

        public void healBuilding(Building TempBuild){
            TempBuild.heal(percentageHealing ? (TempBuild.maxHealth/100 * healing) : healing);
            Fx.healBlockFull.at(TempBuild.x, TempBuild.y, TempBuild.block.size, Color.valueOf("#82f48f"));
            beamAlpha = 1;
        }

        public void healTargets(){
            if((logicControlled() || isControlled()) && within(targetPos, range)){
                Building TempBuild = Vars.world.buildWorld(LTP.x, LTP.y);
                if(TempBuild != null && TempBuild.team == team && TempBuild.damaged()){
                    recoil = recoilAmount;
                    heat = 1;
                    healBuilding(TempBuild);
                }
            }
            else if(target != null && target instanceof Building && ((Building) target).damaged() && validateTarget()){
                this.recoil = 1;
                recoil = recoilAmount;
                heat = 1;
                healTarget(target);
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.reset();
            for (int i = 0; i < effectRotations.length; i++){
                int index = i + 1;
                Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), effectAlphas[i]);
                Draw.alpha(effectAlphas[i]/3/(index * alphaFalloff));
                Lines.square(COOP.x, COOP.y, 2 + maxEffectSize * effectAlphas[i], effectRotations[i]);
            }

            if(this.beamAlpha > 0){
                Draw.z(Layer.bullet);
                float pulsate = Mathr.helix(8, 1, 1, this.beamAlpha * this.beamAlpha) * 0.25f + this.beamAlpha * 0.75f;
                Draw.alpha(this.beamAlpha);
                Lines.stroke(pulsate * 3);
                Draw.color(Color.valueOf("#62ac7d"), Color.valueOf("#82f48f"), this.beamAlpha);
                Lines.line(COOP.x, COOP.y, LTP.x, LTP.y);
                Fill.circle(COOP.x, COOP.y, pulsate * 3);
                Fill.circle(LTP.x, LTP.y,pulsate * 3);
                Draw.color(Color.valueOf("#ffffff"), Color.valueOf("#e8ffd7"), this.beamAlpha);
                Lines.stroke(pulsate);
                Lines.line(COOP.x, COOP.y, LTP.x, LTP.y);
                Fill.circle(COOP.x, COOP.y, pulsate);
                Fill.circle(LTP.x, LTP.y,pulsate);

                Draw.color(Color.white, Pal.heal, this.beamAlpha * this.beamAlpha);
                Lines.circle(LTP.x, LTP.y, 4 - 1.5f * this.beamAlpha * this.beamAlpha);
            }
        }
    }
}
