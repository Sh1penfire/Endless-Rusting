package rusting.world.blocks.defense;

import arc.math.Rand;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import rusting.content.Fxr;
import rusting.content.RustingBullets;

public class ProjectileAttackWall extends Wall {
    private static Rand random = new Rand();

    public BulletType bullet = RustingBullets.saltyShard;
    public float projectileMinVel = 0.35f, projectileMaxVel = 0.95f,
            projectileMinLife = 0.86f, projectileMaxLife = 1f;
    public BulletType deathBullet = RustingBullets.saltyShard;
    public float deathProjectiles = 12;
    public float deathProjectileMinVel = 0.35f, deathProjectileMaxVel = 0.95f,
            deathProjectileMinLife = 0.86f, deathProjectileMaxLife = 1f;
    public float damagePerProjectile = 35;
    public Effect deathEffect = Fxr.instaltSummonerExplosion;

    public boolean destroyableDerelict = false;

    public ProjectileAttackWall(String name) {
        super(name);
    }

    @Override
    public boolean canBreak(Tile tile) {
        return super.canBreak(tile) && (destroyableDerelict || tile.build.team != Team.derelict);
    }

    public class ProjectileAttackWallBuild extends WallBuild{
        public float totalDamage = 0;
        public float progressNextBullet = 0;
        public float lastDamagedFrom = rotation;

        @Override
        public void damage(float damage) {
            super.damage(damage);
            updateProjectileSpawn(damage);
        }

        @Override
        public void damagePierce(float amount) {
            super.damagePierce(amount);
            updateProjectileSpawn(amount);
        }


        public void updateProjectileSpawn(float totalDamage){
            progressNextBullet += totalDamage;
            this.totalDamage += totalDamage;

            progressNextBullet = Math.min(progressNextBullet, block.health);

            random.setSeed((long) (totalDamage + deathProjectiles));

            for (int i = 0; i < progressNextBullet/damagePerProjectile; i++) {
                progressNextBullet -= damagePerProjectile;
                bullet.create(this, team, x, y, lastDamagedFrom, random.random(projectileMinVel, projectileMaxVel), random.random(projectileMinLife, projectileMaxLife));
            }
        }

        @Override
        public void onDestroyed() {
            random.setSeed((long) (tile.pos() + deathProjectiles));
            for (int i = 0; i < deathProjectiles; i++) {
                deathBullet.create(this, team, x, y, random.range(360), random.random(deathProjectileMinVel, deathProjectileMaxVel), random.random(deathProjectileMinLife, deathProjectileMaxLife));
            }
            deathEffect.at(x, y);
            super.onDestroyed();
        }

        @Override
        public boolean collision(Bullet bullet) {
            if(bullet != null) lastDamagedFrom = angleTo(bullet);
            return super.collision(bullet);
        }

        @Override
        public void drawTeam() {
            if(team != Team.derelict) super.drawTeam();
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(totalDamage);
            w.f(progressNextBullet);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            totalDamage = r.f();
            progressNextBullet = r.f();
        }
    }
}
