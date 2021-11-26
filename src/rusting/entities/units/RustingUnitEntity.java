package rusting.entities.units;

import mindustry.gen.UnitEntity;

public class RustingUnitEntity extends UnitEntity {
/*
    @Override
    public boolean damaged(){
        return this.health != this.maxHealth || this.shieldChargef() < 1;
    }

    @Override
    public void heal(float number){
        if(number != null){
            if(this.health >= this.maxHealth){
                if(this.vRecharge <= 0.8 && this.sBroken) this.vRecharge += 0.003;
                else if(this.shieldChargef() < 1){
                    //increase the unit's shield depending on how much the unit is being healed for relative to the unit's health
                    this.vShield = Mathf.clamp(number/this.maxHealth + this.vShield, 0, this.sLimit);
                    //if the unit isn't healing itself from this, or the healing is at the same or lower rate as the unit heals itself, don't display shield
                    if(number > this.type.health/this.HPS/6000 && this.eAlpha < 0.2) this.shieldAlphaf(0.25);
                }
            }
            else{
                this.health += number;
                this.clampHealth();
            }
        }
        else{
            this.health = this.maxHealth;
        }
    }

    @Override
    collision(Bullet b){
        if(b != null && b.type.healPercent > 0)
        {
            this.hitShield(0.05);
            this.damagePierce(b.type.healPercent * 2 + this.type.armor);
        }
    }


    public void hitShield(float number){
        if(this.vShield >= 1){
            this.DR = 1.1;
            this.vShield -= number;
            this.shieldAlphaf(number);
            if(this.vShield < 1 && !this.sBroken){
                this.sBroken = true;
                this.vRecharge = 0;
                voidPop.at(this.x, this.y, 0, [this, 5, this.hitSize + 3, this.eAlpha]);
            }
            else this.vRecharge += 0.01;
        }
        else{
            this.DR = Mathf.slerpDelta(this.DR, 0, 0.005);
            this.vShield = 0;
        }
    }

    public float shieldAlphaf(float number){
        if(number != null) this.eAlpha = Mathf.clamp(this.eAlpha + number, 0, 1);
        else return this.eAlpha;
    }

    public float shieldCharge(float number){
        return this.vShield;
    }

    public float shieldChargef(){
        return this.vShield/this.sLimit;
    }

    public String vstring(){
        if(this.sBroken === true) return "Shield Shattered";
        else return "Void Shield Charge";
    }

    @Override
    public void damage(float number){
        if(number > 0){
            this.hitShield(number >= this.type.armor * 5 ? 1 : 0.5);
            if(number < this.type.health * 12.5 || number > this.type.health * 50) number = number * (1 - this.DR);
            else number = number * (1 - this.DR * 0.5);
            this.super$damage(Mathf.clamp(number, 0, number));
        }
        else this.shieldAlphaf(1);
        if(number <= 0) this.hitTime = 1;
    }

    @Override
    public void apply(StatusEffect status, float time){
        if(status != StatusEffects.none && status != null && !this.isImmune(status)){
            if(status.damage <= 0) super.apply(status, time);
            else if(status.permanent == true) this.heal(Math.abs(status.damage) * 60);
            else if(this.DR <= 0.75 && this.shield <= 1 && status.damage > 0) super.apply(status, time);
        }
    }

    @Override
    public void update(){
        super.update();
        if(Mathf.chance(Time.delta)){

            if(this.maxHealth != this.type.health){
                this.DR = Mathf.clamp(this.DR + 0.1, 0, 1);
                this.maxHealth = this.type.health;
            }

            if(this.damaged()) this.healFract(this.HPS/6000);

            this.DR = Mathf.slerpDelta(this.DR, 0, 0.01);

            if(!this.sBroken) this.vShield = Mathf.slerpDelta(this.vShield, this.sLimit, 0.001);

            this.eAlpha = Mathf.slerpDelta(this.eAlpha, 0, 0.01);

            if(this.vRecharge < 1 && this.sBroken) this.vRecharge += 0.003;
            else if(this.sBroken) this.sBroken = false;

            this.dCol1.a = this.vShield/2.15 * this.eAlpha *  Mathf.clamp(Math.round(this.vShield), 0, 1);

            this.dCol2.a = this.eAlpha *  Mathf.clamp(Math.round(this.vShield), 0, 1);
        }
    }

    @Override
    public void draw(){
        super.draw();
        if(this.eAlpha > 0) Fill.light(this.x, this.y, 5, this.hitSize * 1.25, dCol1, dCol2);
        for(let i = 0; i < this.vShield; i++){
            //use this variable instead of vShield when drawing.
            float i2 = (this.vShield - i)/this.sLimit;
            //Size of circle in addition to the unit's hitSize
            float circleSize = (i2 - 1) * 3 * i;
            //clamped from 0 to 1
            float scaling = Mathf.clamp(i2 * sLimit, 0, 1);
            Draw.color(Color.valueOf("#9c7ae1"),Color.valueOf("#231841"), scaling);
            Draw.alpha(0.1 + scaling/2 * (circleSize * shieldChargef()/(3 * i) != null ? 1 - -circleSize * this.shieldChargef()/(3 * i) : 0.1) * 2);
            if(scaling != 1) Lines.swirl(x, y, this.hitSize + circleSize, scaling, this.rotation);
            else Lines.circle(x, y, hitSize + circleSize);
        }
    }

    @Override
    public void killed() {
        super.killed();
    }
    */
}
