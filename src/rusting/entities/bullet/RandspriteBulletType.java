package rusting.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.gen.Bullet;

public class RandspriteBulletType extends ConsBulletType{

    int variants = 9;
    Seq<TextureRegion> frontRegions = Seq.with(), backRegions = Seq.with();

    public RandspriteBulletType(float speed, float damage, String sprite, int variants) {
        super(speed, damage, sprite);
    }

    @Override
    public void load() {
        super.load();
        if(variants != 0){
            for (int i = 0; i < variants; i++) {
                frontRegions.add(Core.atlas.find(sprite + i, frontRegion));
                backRegions.add(Core.atlas.find(sprite + "-back" + i, backRegion));
            }
        }
        else {
            frontRegions.add(frontRegion);
            backRegions.add(backRegion);
        }
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(b == null) return;
        int v = frontRegions.indexOf(frontRegions.random());
        b.data = Seq.with(frontRegions.get(v), backRegions.get(v));
    }

    @Override
    public void draw(Bullet b) {
        float height = this.height * ((1f - shrinkY) + shrinkY * b.fout());
        float width = this.width * ((1f - shrinkX) + shrinkX * b.fout());
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f);

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        Draw.color(backColor);
        Draw.rect(((Seq<TextureRegion>) b.data).get(0), b.x, b.y, width, height, b.rotation() + offset);
        Draw.color(frontColor);
        Draw.rect(((Seq<TextureRegion>) b.data).get(1), b.x, b.y, width, height, b.rotation() + offset);

        Draw.reset();
    }
}
