package rusting.content;

import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap.Entry;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.core.Version;
import mindustry.ctype.ContentList;
import mindustry.entities.Effect;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.Ranged;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;
import rusting.Varsr;
import rusting.ai.types.BossStingrayAI;
import rusting.ai.types.MultiSupportAI;
import rusting.entities.abilities.*;
import rusting.entities.bullet.*;
import rusting.entities.units.*;
import rusting.entities.units.flying.CraeUnitEntity;
import rusting.entities.units.flying.StingrayUnitEntity;
import rusting.entities.units.mech.BaseUnit;
import rusting.entities.units.spider.BaseSpiderEntity;
import rusting.entities.units.spider.SpecialWeaponsSpider;
import rusting.entities.units.weapons.SpecialBulletWeapon;
import rusting.interfaces.Targeting;

import static arc.graphics.g2d.Draw.color;
import static rusting.EndlessRusting.modname;
import static rusting.content.RustingAISwitches.*;

public class RustingUnits implements ContentList{
    //Steal from BetaMindy
    private static Entry<Class<? extends Entityc>, Prov<? extends Entityc>>[] types = new Entry[]{
            prov(youshoudntbehere.class, youshoudntbehere::new),
            prov(StingrayUnitEntity.class, StingrayUnitEntity::new),
            prov(CraeUnitEntity.class, CraeUnitEntity::new),
            prov(BaseUnitEntity.class, BaseUnitEntity::new),
            prov(BaseUnit.class, BaseUnit::new),
            prov(BaseSpiderEntity.class, BaseSpiderEntity::new),
            prov(SpecialWeaponsSpider.class, SpecialWeaponsSpider::new)
    };

    private static ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();

    /**
     * Internal function to flatmap {@code Class -> Prov} into an {@link Entry}.
     * @author GlennFolker
     */
    private static <T extends Entityc> Entry<Class<T>, Prov<T>> prov(Class<T> type, Prov<T> prov){
        Entry<Class<T>, Prov<T>> entry = new Entry<>();
        entry.key = type;
        entry.value = prov;
        return entry;
    }

    /**
     * Setups all entity IDs and maps them into {@link EntityMapping}.
     * @author GlennFolker
     */

    private static void setupID(){
        for(
                int i = 0,
                j = 0,
                len = EntityMapping.idMap.length;

                i < len;

                i++
        ){
            if(EntityMapping.idMap[i] == null){
                idMap.put(types[j].key, i);
                EntityMapping.idMap[i] = types[j].value;

                if(++j >= types.length) break;
            }
        }
    }

    /**
     * Retrieves the class ID for a certain entity type.
     * @author GlennFolker
     */
    public static <T extends Entityc> int classID(Class<T> type){
        return idMap.get(type, -1);
    }

    public static CraeUnitType
        duono, duoly, duanga;
    public static UnitType
        marrow, metaphys, ribigen, spinascene, trumpedoot,
        diaphysis,
        fahrenheit, celsius, kelvin;
    public static UnitType
        pulseBarrenBezerker;
    public static UnitType
        stingray;
    //Acrimynal's drone army
    public static AcriUnitType
        observantly, kindling, sharpen;
    //crux
    public static UnitType
        fusee;
    public static UnitType
        SYSTEM_DELETED_UNIT;

    @Override
    public void load() {
        setupID();

        EntityMapping.nameMap.put("duono", CraeUnitEntity::new);
        duono = new CraeUnitType("duono"){{

            defaultController = MultiSupportAI::new;

            flying = true;
            hitSize = 7;
            itemCapacity = 50;
            health = 110;
            speed = 1.2f;
            accel = 0.045f;
            drag = 0.025f;
            isCounted = false;

            mineTier = 3;
            mineSpeed = 1.5f;

            pulseStorage = 25;
            repairRange = 40;

            projectileDeathSpawnInterval = 3;

            constructor = CraeUnitEntity::new;

            abilities.add(
                new UpkeeperFieldAbility(4.35f, 135, 45, 4f)
            );
            weapons.addAll(
                new Weapon("none") {{
                    x = 0;
                    y = 0;
                    mirror = false;
                    bullet = RustingBullets.paveBolt;
                    reload = 75;
                    shootCone = 35;
                    shots = 2;
                    shotDelay = 1;
                    inaccuracy = 1;
                }}
            );
        }};

        EntityMapping.nameMap.put("duoly", CraeUnitEntity::new);
        duoly = new CraeUnitType("duoly"){{
            defaultController = MultiSupportAI::new;

            flying = true;
            hitSize = 10;
            itemCapacity = 20;
            health = 320;
            speed = 2.3f;
            accel = 0.0225f;
            drag = 0.0185f;
            rotateSpeed = 2.85f;
            isCounted = false;

            mineTier = 3;
            mineSpeed = 2.3f;

            pulseStorage = 65;
            repairRange = 50;


            projectileDeathSpawnInterval = 5;

            constructor = CraeUnitEntity::new;

            abilities.add(
                    new UpkeeperFieldAbility(5.5f, 175, 55, 7f)
            );
            weapons.add(
                new Weapon("none") {{
                    x = 1;
                    y = 3;
                    bullet = RustingBullets.paveWeaver;
                    reload = 22.5f;
                }}
            );
        }};

        EntityMapping.nameMap.put("duanga", CraeUnitEntity::new);
        duanga = new CraeUnitType("duanga"){{
            defaultController = MultiSupportAI::new;

            range = 155;
            flying = true;
            hitSize = 15;
            itemCapacity = 35;
            health = 435;
            armor = 2;
            speed = 1.8f;
            accel = 0.0525f;
            drag = 0.0385f;
            rotateSpeed = 4.85f;
            buildSpeed = 0.75f;

            rotateShooting = false;

            isCounted = false;

            pulseStorage = 85;
            repairRange = 90;
            pulseAmount = 3.25f;
            pulseGenRange = 120;

            projectileDeathSpawnInterval = 8;

            constructor = CraeUnitEntity::new;

            abilities.add(
                new HealthEqualizerAbility(){{
                    health = 7.5f;
                    reload = 22.5f;
                    range = 95;
                    lineThickness = 7;
                    laserOffset = 2.35f;
                    maxWidth = 2;
                    y = -2;
                    x = 3.75f;
                    mirror = true;
                }},
                new PulseGeneratorAbility(){{
                    pulse = 3.25f;
                    reload = 135;
                    range = 120;
                    laserOffset = 2.35f;
                    x = 0;
                    y = -6.25f;
                }},
                new UpkeeperFieldAbility(7.5f, 235, 65, 6),
                new RegenerationAbility(3.5f/60)
            );
        }};

        //make sure ai works, it uses these switches which are toggleable ingame
        Varsr.switches.putSwitches(Seq.with(attackSwitch, mineSwitch, healUnitSwitch, healBlockSwitch), duono);
        Varsr.switches.putSwitches(Seq.with(attackSwitch, mineSwitch, healUnitSwitch, healBlockSwitch), duoly);
        Varsr.switches.putSwitches(Seq.with(attackSwitch, mineSwitch, healUnitSwitch, healBlockSwitch), duanga);


        EntityMapping.nameMap.put("fahrenheit", BaseUnit::new);
        fahrenheit = new UnitType("fahrenheit"){{

            flying = false;
            canBoost = true;
            hitSize = 7;
            health = 235;
            speed = 1.05f;
            accel = 0.45f;
            drag = 0.25f;

            defaultController = SuicideAI::new;

            constructor = BaseUnit::new;
            weapons.add(
                new Weapon("none") {{
                    x = 0;
                    y = 0;
                    reload = 35;
                    shootCone = 360;
                    mirror = false;
                    bullet = new BombBulletType(0f, 0f, "clear"){{
                        hitEffect = Fx.pulverize;
                        lifetime = 10f;
                        speed = 1.3f;
                        splashDamageRadius = 58f;
                        instantDisappear = true;
                        splashDamage = 5;
                        hittable = false;
                        collidesAir = true;
                        fragBullets = 13;
                        fragBullet = RustingBullets.craeBoltKill;
                    }};
                }},
                new Weapon("none") {{
                    x = 0;
                    y = 0;
                    reload = 35;
                    shootCone = 360;
                    mirror = false;
                    bullet = new BombBulletType(0f, 0f, "clear"){{
                        hitEffect = Fx.pulverize;
                        lifetime = 10f;
                        speed = 1.3f;
                        splashDamageRadius = 58f;
                        instantDisappear = true;
                        splashDamage = 15;
                        killShooter = true;
                        hittable = false;
                        collidesAir = true;
                        fragBullets = 5;
                        fragBullet = RustingBullets.mhemaeShard;
                    }};
                }}
            );

            immunities.addAll(StatusEffects.burning, RustingStatusEffects.amberstriken, RustingStatusEffects.umbrafliction);
        }};

        Effect lanceEffect = new Effect(32, e -> {

            Draw.blend(Blending.additive);
            Draw.z(Layer.effect + 1);

            Fill.light(e.x, e.y, 8, 8 + e.fout() * 3, Tmp.c1.set(Palr.pulseLaser).a(e.fout()/2), Tmp.c2.set(Palr.chillDecalLight).a(0));

            Draw.blend();
            Draw.z(Layer.effect);

            for(int i : Mathf.signs){
                color(Palr.chillDecalDark);
                Drawf.tri(e.x, e.y, 4f * e.fout(), 24 + e.fout() * 9, e.rotation + 90f * i);
                color(Palr.pulseLaser);
                Drawf.tri(e.x, e.y, 2f * e.fout(), 16 + e.fout() * 3, e.rotation + 90f * i);

                color(Palr.chillDecalDark);
                Drawf.tri(e.x, e.y, 4f * e.fout(), 24 + e.fout() * 9, e.rotation + 75f * i);
                color(Palr.pulseLaser);
                Drawf.tri(e.x, e.y, 2f * e.fout(), 16 + e.fout() * 3, e.rotation + 75f * i);
            }
        });

        EntityMapping.nameMap.put("celsius", BaseUnit::new);
        celsius = new UnitType("celsius"){{
            flying = false;
            canBoost = true;
            hitSize = 11;
            health = 460;
            armor = 5;
            speed = 0.725f;
            accel = 0.55f;
            drag = 0.35f;
            boostMultiplier = 2.5f;
            constructor = BaseUnit::new;

            weapons.add(
                new Weapon("none"){
                    {
                        mirror = false;
                        rotate = true;
                        shootY = 0;
                        shootX = 0;
                        rotateSpeed = 360;
                        shootCone = 360;
                        reload = 50;
                        shake = 5;
                        shootSound = Sounds.bang;
                        bullet = new ConsBulletType(0, 350, "none"){{
                            useRange = true;
                            range = 75;
                            killShooter = true;
                            collides = false;
                            splashDamage = 45;
                            splashDamageRadius = 75;
                            consHit = (b) -> {
                                for(int i = 0; i < 15; i++){
                                    Vec2 pos = new Vec2(b.x, b.y);
                                    Time.run(i * 3 + Mathf.random(4), () -> {
                                        RustingBullets.mhemaeShardling.create(b.owner, b.team, pos.x, pos.y, Mathf.random() * 360, 5, Mathf.random() * 0.15f + 1, 1, 0);
                                    });
                                }
                            };
                            lifetime = 0;
                            instantDisappear = true;
                            fragBullets = 1;
                            fragBullet = new BulletSpawnBulletType(0, 100, "none") {{
                                bullets = Seq.with(
                                        new BulletSpawner() {{
                                            bullet = RustingBullets.celsiusLance;
                                            shootEffect = lanceEffect;
                                            reloadTime = 45;
                                            intervalIn = 50;
                                            intervalOut = 50;
                                            idleInaccuracy = 0;
                                            manualAiming = false;
                                            bulletTargeting = true;
                                            shootWhenIdle = false;
                                        }},
                                        new BulletSpawner() {{
                                            bullet = new LightningBulletType(){{
                                                damage = 13;
                                                lightningDamage = 12f;
                                                lightningLength = 15;
                                                lightningColor = Palr.lightstriken;
                                                status = StatusEffects.shocked;
                                            }};
                                            shootEffect = Fx.sparkShoot;
                                            reloadTime = 25;
                                            intervalIn = 50;
                                            intervalOut = 50;
                                            idleInaccuracy = 360;
                                            manualAiming = false;
                                            bulletTargeting = false;
                                            shootWhenIdle = true;
                                        }},
                                        new BulletSpawner() {{
                                            bullet = new LightningBulletType(){{
                                                damage = 5;
                                                lightningDamage = 4f;
                                                lightningLength = 9;
                                                lightningColor = Palr.lightstriken;
                                                status = StatusEffects.shocked;
                                            }};
                                            shootEffect = Fx.sparkShoot;
                                            reloadTime = 7.5f;
                                            intervalIn = 135;
                                            intervalOut = 95;
                                            idleInaccuracy = 360;
                                            manualAiming = false;
                                            bulletTargeting = false;
                                            shootWhenIdle = true;
                                        }}
                                );

                                frontColor = Palr.chillDecalLight;
                                backColor = Palr.chillDecalDark;
                                despawnEffect = Fx.sparkShoot;
                                lifetime = 650;
                                width = height = 11f;
                                splashDamage = 33f;
                                splashDamageRadius = 35f * 0.75f;
                                shootEffect = Fx.shootBig;
                                scaleDrawIn = 9;
                                scaleDrawOut = 4;

                                shrinkX = 0.15f;
                                shrinkY = 0.63f;


                                drawSize = 4;
                            }};
                        }};
                    }}
            );

            immunities.addAll(StatusEffects.burning, RustingStatusEffects.amberstriken, RustingStatusEffects.umbrafliction);

        }};

        EntityMapping.nameMap.put("kelvin", BaseUnit::new);
        kelvin = new UnitType("kelvin"){{

            flying = false;
            canBoost = true;
            hitSize = 11;
            health = 980;
            armor = 5;
            speed = 0.725f;
            accel = 0.55f;
            drag = 0.35f;
            boostMultiplier = 2.5f;
            constructor = BaseUnit::new;
            weapons.add(
                new Weapon("none"){{
                    x = 8.5f;
                    y = 1f;
                    shootY = 7;
                    shootX = -1.5f;
                    shootCone = 35;
                    reload = 250;
                    shake = 5;
                    shootSound = Sounds.bang;
                    bullet = new ConsBulletType(0.725f, 35, "shell"){{
                        width = 15;
                        height = 15;
                        lifetime = 150;
                        scaleVelocity = true;
                        shootEffect = Fx.shootBig;
                        trailChance = 0.15f;
                        trailEffect = Fx.artilleryTrail;
                        backColor = Palr.darkPyraBloom;
                        frontColor = Palr.lightstriken;
                        trailColor = Palr.darkPyraBloom;
                        hitEffect = Fxr.instaltSummonerExplosion;
                        hitShake = 4;
                        hitSound = Sounds.explosion;
                        fragBullet = new BounceBulletType( 2.5f,16, "bullet"){{
                            consUpdate = bullet -> {
                                Tmp.v1.set(bullet.x, bullet.y);
                                //handle modded cases of bullet owners first
                                if(bullet.owner instanceof Targeting){
                                    Tmp.v1.set(((Targeting) bullet.owner).targetPos());
                                }
                                else if(bullet.owner instanceof TurretBuild) {
                                    Tmp.v1.set(((TurretBuild) bullet.owner).targetPos.x, ((TurretBuild) bullet.owner).targetPos.y);
                                }
                                else if (bullet.owner instanceof Unitc){
                                    Tmp.v1.set(((Unitc) bullet.owner).aimX(), ((Unitc) bullet.owner).aimY());
                                }
                                Tmp.v3.set(((Posc) bullet.owner()).x(), ((Posc) bullet.owner()).y());
                                Tmp.v1.sub(Tmp.v3).clamp(0, ((Ranged) bullet.owner).range()).add(Tmp.v3);
                                bullet.vel.add(Tmp.v2.trns(bullet.angleTo(Tmp.v1), bullet.type.homingPower * Time.delta)).clamp(0, bullet.type.speed);

                                Fxr.burningFlame.at(bullet.x, bullet.y, bullet.rotation());
                                //essentualy goes to owner aim pos, without stopping homing and no lifetime reduction
                            };
                            pierceBuilding = false;
                            despawnEffect = Fx.fireSmoke;
                            hitEffect = Fx.fire;
                            bounceEffect = Fxr.shootMhemFlame;
                            incendAmount = 10;
                            status = StatusEffects.burning;
                            statusDuration = 350;
                            maxRange = 156;
                            width = 6;
                            height = 8;
                            hitSize = 12;
                            lifetime = 350;
                            homingPower = 0.15f;
                            homingRange = 0;
                            homingDelay = 35;
                            hitEffect = Fx.hitFuse;
                            trailLength = 0;
                            bounciness = 0.85f;
                            bounceCap = 0;
                            pierceCap = 1;
                            weaveScale = 4;
                            weaveMag = 3;
                            knockback = 3;
                            drag = 0.0015f;
                        }};
                        fragBullets = 4;
                    }};
                }},
                new Weapon("endless-rusting-kelvin-launcher") {{
                    x = 8.5f;
                    y = 1f;
                    shootY = 7;
                    shootX = -1.5f;
                    shootCone = 360;
                    inaccuracy = 23;
                    shots = 5;
                    shotDelay = 2.5f;
                    reload = 65;
                    top = false;
                    shootSound = Sounds.spark;
                    soundPitchMin = 0.65f;
                    soundPitchMax = 0.85f;
                    bullet = new ConsBulletType(5, 1, "bullet"){{
                        useRange = true;
                        range = 175.5f;
                        width = 8;
                        height = 11;
                        scaleVelocity = true;
                        trailChance = 0.15f;
                        trailEffect = Fx.artilleryTrail;
                        backColor = Palr.chillDecalDark;
                        frontColor = Palr.lightstriken;
                        trailColor = Palr.chillDecalDark;
                        speed = 5;
                        lifetime = 22.5f;
                        shootEffect = new Effect(15, e -> {
                            color(Palr.chillDecalDark);

                            for(int i : Mathf.signs){
                                Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i * (e.fout() * 0.2f + 0.8f));
                            }

                            Draw.alpha(e.fout());
                            Fill.circle(e.x, e.y, (1 - e.finpow()) * 8);
                        });
                        fragBullets = 1;
                        consDespawned = (b) -> {
                            if(!(b.owner instanceof Unit)) return;
                            Unit u = (Unit) b.owner;

                            float rotation = b.angleTo(u.aimX(), u.aimY());


                            RustingBullets.kelvinLance.create(u, b.x, b.y, rotation);
                            lanceEffect.at(b.x, b.y, rotation);
                        };
                    }};
                }}
            );

            immunities.addAll(StatusEffects.burning, RustingStatusEffects.amberstriken, RustingStatusEffects.umbrafliction);

        }};

        EntityMapping.nameMap.put("pulse-barren-bezerker", BaseUnit::new);
        pulseBarrenBezerker = new UnitType("pulse-barren-bezerker"){{
            health = 85;
            armor = 5;
            drawCell = false;
            mechLegColor = Color.valueOf("#3b3e49");

            constructor = BaseUnit::new;

            weapons.addAll(
                new Weapon("none") {{
                    mirror = false;
                    y = 0;
                    x = 0;
                    shootStatus = StatusEffects.unmoving;
                    shootStatusDuration = 150;
                    bullet = RustingBullets.craeWeaver;
                    shots = 3;
                    spacing = 7;
                    shotDelay = 19;
                    reload = 150;
                }},
                new Weapon("none") {{
                    mirror = false;
                    y = 0;
                    x = 0;
                    shootStatus = RustingStatusEffects.balancedPulsation;
                    shootStatusDuration = 150;
                    firstShotDelay = 55;
                    bullet = RustingBullets.craeWeaver;
                    shots = 4;
                    spacing = 15;
                    reload = 150;
                }}
            );

            immunities.addAll(StatusEffects.unmoving, RustingStatusEffects.balancedPulsation);
        }};

        EntityMapping.nameMap.put("marrow", BaseUnit::new);
        marrow = new UnitType("marrow"){{
            hitSize = 8;
            health = 215;
            armor = 1;
            speed = 0.5225f;
            accel = 0.5f;
            drag = 0.25f;
            lightRadius = hitSize * 2.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.15f;
            itemCapacity = 15;
            commandLimit = 4;
            mechLegColor = Palr.dustriken;
            //v7 compatability
            constructor = BaseUnit::new;
            abilities.add(
                    new RegenerationAbility(0.1f)
            );

            immunities.addAll(
                StatusEffects.wet,
                StatusEffects.burning,
                StatusEffects.sporeSlowed,
                StatusEffects.sapped,
                RustingStatusEffects.shieldShatter
            );

            weapons.add(
                new Weapon("none") {{
                    x = 4;
                    y = 4.25f;
                    shots = 3;
                    spacing = 3;
                    shotDelay = 5;
                    bullet = RustingBullets.horizonInstalt;
                    shootSound = Sounds.bang;
                    reload = 115;
                }}
            );

        }};

        EntityMapping.nameMap.put("metaphys", BaseUnit::new);
        metaphys = new UnitType("metaphys"){{
            hitSize = 10;
            health = 830;
            armor = 4;
            speed = 0.45f;
            accel = 0.15f;
            drag = 0.05f;
            lightRadius = hitSize * 2.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.35f;
            itemCapacity = 35;
            commandLimit = 3;
            mechLegColor = Palr.dustriken;
            //v7 compatability
            constructor = BaseUnit::new;
            abilities.add(
                    new RegenerationAbility(0.1f)
            );

            immunities.addAll(
                StatusEffects.wet,
                StatusEffects.burning,
                StatusEffects.sporeSlowed,
                StatusEffects.sapped,
                RustingStatusEffects.shieldShatter
            );

            weapons.addAll(
                new Weapon("endless-rusting-metaphys-sidearms"){{
                    top = false;
                    alternate = true;
                    x = 7.25f;
                    y = 1.25f;
                    recoil = 2;
                    shots = 3;
                    spacing = 25;
                    inaccuracy = 3;
                    bullet = RustingBullets.pavenShardling;
                    shootSound = Sounds.flame2;
                    reload = 77.5f;
                }}
            );

        }};

        EntityMapping.nameMap.put("ribigen", BaseUnit::new);
        ribigen = new UnitType("ribigen"){{
            hitSize = 13;
            health = 1235;
            armor = 10;
            speed = 0.35f;
            accel = 0.35f;
            drag = 0.15f;
            lightRadius = hitSize * 3.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.05f;
            itemCapacity = 75;
            commandLimit = 2;
            mechLegColor = Palr.dustriken;
            //v7 compatability
            constructor = BaseUnit::new;
            abilities.add(
                    new RegenerationAbility(0.3f)
            );

            immunities.addAll(
                    StatusEffects.wet,
                    StatusEffects.burning,
                    StatusEffects.sporeSlowed,
                    StatusEffects.sapped,
                    RustingStatusEffects.shieldShatter,
                    RustingStatusEffects.amberstriken,
                    RustingStatusEffects.umbrafliction
            );

            weapons.addAll(
                new Weapon("endless-rusting-ribigen-instalt-launcher"){{
                    x = 13.25f;
                    y = 6.25f;
                    reload = 120;
                    recoil = 3;
                    shots = 4;
                    spacing = 4;
                    shootSound = Sounds.artillery;
                    bullet = RustingBullets.timelessInstalt;
                    top = false;
                }},
                new Weapon("endless-rusting-ribigen-weapon"){{
                    x = 0;
                    y = 0;
                    shootY = 7.75f;
                    shots = 5;
                    spacing = 1;
                    inaccuracy = 0.25f;
                    shotDelay = 1;
                    recoil = 0;
                    reload = 150;
                    heatColor = new Color(Pal.turretHeat).a(0.54f);
                    bullet = Bullets.heavyOilShot;
                    shootSound = Sounds.release;
                    mirror = false;
                }},
                new Weapon("none"){{
                    x = 0;
                    y = 0;
                    shots = 25;
                    spacing = 2;
                    inaccuracy = 1;
                    shotDelay = 0.35f;
                    reload = 150;
                    bullet = Bullets.oilShot;
                    shootSound = Sounds.none;
                }}
            );
        }};

        EntityMapping.nameMap.put("spinascene", BaseUnit::new);
        spinascene = new UnitType("spinascene"){{
            hitSize = 24;
            health = 9760;
            armor = 13;
            speed = 0.25f;
            accel = 0.35f;
            drag = 0.25f;
            rotateSpeed = 0.55f;
            lightRadius = hitSize * 4.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.125f;
            itemCapacity = 125;
            commandLimit = 2;
            mechLegColor = Palr.dustriken;
            //v7 compatability
            constructor = BaseUnit::new;
            abilities.add(
                new RegenerationAbility(0.7f),
                new StatusFieldAbility(RustingStatusEffects.corruptShield, 1440, 600, 45)
            );

            immunities.addAll(
                StatusEffects.wet,
                StatusEffects.burning,
                StatusEffects.sporeSlowed,
                StatusEffects.sapped,
                RustingStatusEffects.shieldShatter,
                RustingStatusEffects.amberstriken,
                RustingStatusEffects.umbrafliction
            );

            weapons.addAll(
                new Weapon("endless-rusting-spinascene-branches"){{
                    x = 16.5f;
                    y = -0.35f;
                    shootY = 19.45f;
                    reload = 430;
                    shots = 9;
                    spacing = 3;
                    shootSound = Sounds.none;
                    recoil = 3;
                    bullet = RustingBullets.timelessInstalt;
                    shootCone = 90;
                    top = false;
                }},
                new Weapon("endless-rusting-spinascene-beam"){{
                    x = 19.17f;
                    y = 3.45f;
                    bullet = RustingBullets.nummingVortex;
                    shootSound = Sounds.bang;
                    shootCone = 90;
                    reload = 430;
                    inaccuracy = 4;
                    recoil = 3;
                }},
                new Weapon("none"){{
                    x = 0;
                    y = 0;
                    bullet = RustingBullets.darkShard;
                    shootSound = Sounds.none;
                    shots = 15;
                    reload = 45 * 7.5f;
                    inaccuracy = 360;
                    shotDelay = 22.5f;
                    shootCone = 360;
                    rotate = true;
                }}
            );
        }};

        EntityMapping.nameMap.put("trumpedoot", BaseUnit::new);
        trumpedoot = new UnitType("trumpedoot"){{
            hitSize = 28;
            health = 29500;
            armor = 19;
            speed = 0.35f;
            accel = 0.65f;
            drag = 0.45f;
            rotateSpeed = 0.95f;
            lightRadius = hitSize * 4.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.013f;
            itemCapacity = 125;
            commandLimit = 5;
            mechLegColor = Palr.dustriken;

            constructor = BaseUnit::new;

            abilities.addAll(
                new RegenerationAbility(0.21f),
                new SpeedupAbility(){{
                    range = 115;
                    lineThickness = 7;
                    laserOffset = 2.35f;
                    maxWidth = 2;
                    x = 15.5f;
                    mirror = true;
                }}
            );

            weapons.addAll(
                new Weapon("endless-rusting-triumpedoot-weapon"){{
                    bullet = RustingBullets.gunnersVortex;
                    reload = 195;
                    shootX = 21;
                    shootY = 6;
                    shots = 4;
                    x = 0;
                    shootCone = 360;
                    spacing = 15;
                    alternate = false;
                    top = false;
                    recoil = 15;
                    shake = 5;
                }},
                new Weapon("none"){{
                    x = 0;
                    mirror = false;
                    shots = 3;
                    spacing = 120;
                    shootCone = 360;
                    reload = 1200;
                    bullet = RustingBullets.guardianVortex;
                    shotDelay = 5;
                }}
            );
            immunities.addAll(
                StatusEffects.wet,
                StatusEffects.burning,
                StatusEffects.sporeSlowed,
                StatusEffects.sapped,
                RustingStatusEffects.amberstriken,
                RustingStatusEffects.umbrafliction
            );
        }};

        EntityMapping.nameMap.put("diaphysis", SpecialWeaponsSpider::new);
        diaphysis = new SpecialWeaponsUnitType("diaphysis"){{
            hitSize = 15;
            health = 3500;
            armor = 9;
            speed = 1.5f;
            accel = 0.65f;
            drag = 0.45f;

            legCount = 3;
            legLength = 14;
            legTrns = 0.6f;
            legMoveSpace = 3.25f;
            hovering = true;

            rotateSpeed = 4.95f;
            lightRadius = hitSize * 4.5f;
            lightColor = Palr.dustriken;
            lightOpacity = 0.013f;
            itemCapacity = 125;
            commandLimit = 5;

            visualElevation = 0.65f;
            groundLayer = Layer.legUnit;

            constructor = SpecialWeaponsSpider::new;

            singleTarget = false;

            specialWeapons.addAll(new SpecialBulletWeapon(modname + "-diaphysis-harpoon-launcher"){{
                bulletType = RustingBullets.stingrayShard;
                y = -5;
                x = 0;
            }});

            weapons.addAll(
                new Weapon(modname + "-flamethrower-small"){{
                    bullet = RustingBullets.shortPyraFlame;
                    shots = 1;
                    reload = 3;
                    top = false;
                    x = 3.5f;
                    y = 9f;
                    shootY = 5.15f;
                    shootSound = Sounds.flame2;
                    shootCone = 65;
                }},
                new Weapon(modname + "-diaphysis-backwards-launcher"){{
                    bullet = RustingBullets.raehWeaver;
                    shots = 3;
                    reload = 120;
                    recoil = 5;
                    alternate = false;
                    top = false;
                    x = 0f;
                    y = 0f;
                    shootX = 6.5f;
                    shootY = -3.25f;
                    shootSound = Sounds.bang;
                    soundPitchMax = 0.5f;
                    soundPitchMin = 0.35f;
                    shootCone = 360;
                }}
            );
        }};

        EntityMapping.nameMap.put("guardian-sulphur-stingray", StingrayUnitEntity::new);
        stingray = new UnitType("guardian-sulphur-stingray"){{
            health = 6500;
            armor = 2;
            rotateSpeed = 3.65f;
            lightOpacity = 0.35f;
            lightColor = Palr.pulseBullet;
            hitSize = 18;
            drag = 0.05f;
            accel = 0.055f;
            speed = 2.85f;
            flying = true;
            circleTarget = true;
            faceTarget = true;
            omniMovement = false;
            singleTarget = true;
            constructor = StingrayUnitEntity::new;
            if(Version.number < 7) defaultController = BossStingrayAI::new;
            else defaultController = FlyingAI::new;
            //yadayadablahblahblah nobody can read this sh1p
            abilities.addAll(
                new RegenerationAbility(0.75f)
            );
            weapons.addAll(
                new Weapon("none"){{
                    bullet = RustingBullets.saltyLightGlaive;
                    shootCone = 360;
                    shots = 5;
                    shotDelay = 5;
                    spacing = -15;
                    reload = 450;
                    singleTarget = true;
                    mirror = false;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.saltyLightGlaive;
                    shootCone = 360;
                    shots = 5;
                    shotDelay = 5;
                    spacing = 15;
                    reload = 450;
                    singleTarget = true;
                    mirror = false;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.stingrayShard;
                    shots = 2;
                    spacing = 5;
                    inaccuracy = 2;
                    shootCone = 360;
                    x = 11.5f;
                    y = 5.25f;
                    reload = 4.65f;
                    shootSound = Sounds.bang;
                    singleTarget = true;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.melomaeShot;
                    shots = 5;
                    shotDelay = 4;
                    shootCone = 360;
                    x = 17.25f;
                    y = 4.25f;
                    reload = 45.65f;
                    shootSound = Sounds.explosion;
                    singleTarget = true;
                }},
                new Weapon("none") {{
                    bullet = RustingBullets.mhemShard;
                    shootCone = 360;
                    x = 8.75f;
                    y = -7.5f;
                    reload = 15;
                    singleTarget = true;
                }}
            );
            immunities.addAll(
                StatusEffects.wet,
                StatusEffects.shocked,
                StatusEffects.freezing,
                StatusEffects.blasted,
                StatusEffects.sporeSlowed,
                StatusEffects.sapped,
                StatusEffects.burning,
                StatusEffects.unmoving,
                RustingStatusEffects.amberstriken,
                RustingStatusEffects.umbrafliction,
                RustingStatusEffects.macrosis,
                RustingStatusEffects.macotagus,
                RustingStatusEffects.hailsalilty
            );
        }};

        EntityMapping.nameMap.put("observantly", BaseUnitEntity::new);
        observantly = new AcriUnitType("observantly"){{

            flying = true;
            drag = 0.025f;
            accel = 0.0525f;
            speed = 1.75f;
            health = 850f;
            armor = 10;
            rotateSpeed = 3.5f;
            hitSize = 19;
            itemCapacity = 14;

            constructor = BaseUnitEntity::new;


            weapons.addAll(
                new Weapon("none"){{
                    bullet = RustingBullets.melomaeShot;
                    shots = 15;
                    x = 0;
                    y = 0;
                    spacing = 72;
                    reload = 125;
                    shotDelay = 5;
                    shootCone = 360;
                    shootSound = Sounds.none;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.melomaeShot;
                    shots = 15;
                    x = 0;
                    y = 0;
                    spacing = -72;
                    reload = 125;
                    shotDelay = 5;
                    shootCone = 360;
                    shootSound = Sounds.none;
                }},
                new Weapon("none"){{
                    bullet = new LaserBulletType(25){{
                        recoil = 5;
                        length = 166;
                    }};
                    shots = 3;
                    x = 0;
                    y = 0;
                    spacing = 3;
                    reload = 135;
                    shootSound = Sounds.laser;
                    soundPitchMax = 0.35f;
                    soundPitchMin = 0.25f;
                }}
            );
        }};

        EntityMapping.nameMap.put("kindling", BaseUnitEntity::new);
        kindling = new AcriUnitType("kindling"){{
            flying = true;

            accel = 0.025f;
            drag = 0.001f;
            speed = 2.35f;
            rotateSpeed = 4.5f;
            itemCapacity = 75;
            mineTier = 3;
            mineSpeed = 6.5f;
            health = 635f;
            armor = 13;
            hitSize = 14;
            defaultController = MinerAI::new;
            constructor = BaseUnitEntity::new;
            abilities.addAll(
                new HealthEqualizerAbility(){{
                    mountName = "none";
                    mirror = false;
                }},
                new RegenerationAbility(0.95f)
            );
        }};

        EntityMapping.nameMap.put("kindling", BaseUnitEntity::new);
        sharpen = new AcriUnitType("sharpen"){{
            flying = true;
            circleTarget = true;

            accel = 0.025f;
            drag = 0.0081f;
            speed = 2.35f;
            rotateSpeed = 7.5f;
            itemCapacity = 15;
            health = 340;
            armor = 6;
            hitSize = 14;
            constructor = BaseUnitEntity::new;
            abilities.addAll(
                new StatusFieldAbility(StatusEffects.overdrive, 1690, 1380, 85)
            );
            weapons.addAll(
                    new Weapon(modname + "-sharpen-weapon"){{
                        x = 8.5f;
                        y = -0.5f;
                        reload = 2;
                        shootCone = 15;
                        top = false;
                        alternate = false;
                        bullet = new ContinuousLaserBulletType(5){{
                            length = 130;
                            width = 0.75f;
                            hitEffect = Fx.hitLancer;
                            drawSize = 420f;
                            lifetime = 6;
                            shake = 0.1f;
                            despawnEffect = Fx.none;
                            smokeEffect = Fx.none;
                            fadeTime = 2;

                            shootEffect = Fx.none;

                            colors = new Color[]{Palr.chillDecalDark.cpy().a(.2f), Palr.chillDecalDark.cpy().a(.5f), Palr.chillDecalDark.cpy().mul(1.2f), Palr.chillDecalLight};
                        }};
                    }},
                    new Weapon("none"){{
                        x = 8.5f;
                        y = -0.5f;
                        reload = 2;
                        shootCone = 15;
                        firstShotDelay = 2;
                        top = false;
                        alternate = false;
                        bullet = new ContinuousLaserBulletType(5){{
                            length = 130;
                            width = 0.75f;
                            hitEffect = Fx.hitLancer;
                            drawSize = 420f;
                            lifetime = 6;
                            shake = 0.1f;
                            despawnEffect = Fx.none;
                            smokeEffect = Fx.none;
                            fadeTime = 2;

                            shootEffect = Fx.none;

                            colors = new Color[]{Palr.chillDecalDark.cpy().a(.2f), Palr.chillDecalDark.cpy().a(.5f), Palr.chillDecalDark.cpy().mul(1.2f), Palr.chillDecalLight};
                        }};
                    }}
            );
        }};

        EntityMapping.nameMap.put("SYSTEM_DELETED_UNIT", youshoudntbehere::new);
        SYSTEM_DELETED_UNIT = new UnitType("SYSTEM_DELETED_UNIT"){{
            health = 50000;
            armor = 10;
            speed = 2;
            accel = 100;
            drag = 99;
            defaultController = FlyingAI::new;
            constructor = youshoudntbehere::new;

            singleTarget = true;

            weapons.addAll(
                new Weapon("none"){{
                    bullet = RustingBullets.ddd;
                    shots = 3;
                    spacing = 15;
                    reload = 1500;
                    x = 0;
                    y = 15;
                    mirror = false;
                    rotate = false;
                    shootCone = 360;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.eee;
                    shots = 35;
                    spacing = 175;
                    shotDelay = 10;
                    reload = 500;
                    x = 5;
                    y = 0;
                    mirror = false;
                    rotate = false;
                    shootCone = 360;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.eee;
                    shots = 35;
                    spacing = 175;
                    shotDelay = 10;
                    firstShotDelay = 5;
                    reload = 500;
                    x = -5;
                    y = 0;
                    mirror = false;
                    rotate = false;
                    shootCone = 360;
                }},
                new Weapon("none"){{
                    bullet = RustingBullets.fff;
                    shots = 4;
                    spacing = 90;
                    reload = 150;
                    mirror = false;
                    rotate = false;
                    y = -10.5f;
                    x = 6;
                }}
            );
        }};
    }
}
