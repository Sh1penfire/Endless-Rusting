package rusting.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;

public class PelletBulletType extends ConsBulletType{
    public PelletBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
    }

    public int shieldSides = 20;
    public float shieldRadius = hitSize;
    public Color shieldBack, shieldFront;
    public boolean drawShield = false, damageFalloff = true;
    public float bulletDamage = 12;
    public float bulletPierceCap = 1;

    @Override
    public void init() {
        super.init();
        if(shieldBack == null) shieldBack = new Color(backColor).a(0);
        if(shieldFront == null) shieldFront = frontColor;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        b.data = new IntSeq();
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        Seq<Bullet> bullets = Groups.bullet.intersect(b.x, b.y, b.hitSize, b.hitSize);
        IntSeq collided = (IntSeq) b.data;
        if(bullets.size > 0) bullets.each(bullet -> {
            if(collided.contains(bullet.id) || bullet.team == b.team || !bullet.type.hittable) return;
            collided.add(bullet.id);
            bullet.damage -= bulletDamage;
            hitEffect.at(b.x, b.y);
            if(bullet.damage <= 0){
                bullet.remove();
                return;
            }
        });
        if(collided.size >= bulletPierceCap) b.remove();
        if(damageFalloff){
            b.damage -= damage/lifetime * Time.delta;
            if(b.damage <= 0) b.remove();
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        if(drawShield){
            Fill.light(b.x, b.y, shieldSides, shieldRadius * b.fout(), shieldFront, shieldBack);
        }
    }
}
