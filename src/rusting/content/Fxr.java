package rusting.content;

import arc.Core;
import arc.func.Floatp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import rusting.entities.units.CraeUnitType;
import rusting.entities.units.flying.CraeUnitEntity;
import rusting.math.Mathr;
import rusting.world.blocks.defense.turret.power.LightningTurret.VisualLightningHolder;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.angle;
import static arc.math.Angles.randLenVectors;

public class Fxr{
    private static Rand rand = new Rand();
    private static float precent = 0;

    public static final Effect

        none = new Effect(0, 0f, e -> {}),

        corrodedEffect = new Effect(75, e -> {
            Draw.color(Color.white, Pal.plastanium, Pal.darkMetal, e.fin());
            Fill.circle(e.x, e.y, e.fout() * 1.6f);
            Draw.color(Color.white, Pal.plastanium, Pal.darkMetal, e.fin());
            Fill.circle(e.x, e.y, e.fout() * 1.25f);
        }),

        blackened = new Effect(35, 0f, e -> {
            color(Color.black, Color.black, e.fin());
            randLenVectors(e.id, 2, e.finpow() * 3, e.rotation, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1 + Math.sin(e.fin() * 2 * Math.PI)));
                Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1.2 + Math.sin(e.fin() * 2 * Math.PI)));
            });
            Draw.reset();
            color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), e.fin());
            Draw.alpha(0.35F * e.fout());
            randLenVectors(e.id, 2, e.finpow() * 5, e.rotation, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1 + Math.sin(e.fin() * 2 * Math.PI)));
            });
        }),

        blackenedShotgun = new Effect(45, 0f, e -> {
            color(Color.black, Color.black, e.fin());
            randLenVectors(e.id, 12, e.finpow() * 45, e.rotation, 25, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 1 + Mathf.sin(e.fin() * 6));
                Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f + Mathf.sin(e.fin() * 6));
            });
            Draw.reset();

            e.scaled(25, e2 -> {
                color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), e.fin());
                Draw.alpha(0.85f * e2.fout());
                randLenVectors(e.id, 7, e2.finpow() * 55, e.rotation, 35, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e2.fout() * 1 + Mathf.sin(e2.fin() * 6));
                });
            });
        }),

        blackenedShotgunCrit = new Effect(55, 0f, e -> {
            e.scaled(15, c -> {});
            e.scaled(45, e1 -> {
                color(Color.black, Color.black, e.fin());
                randLenVectors(e.id, 35, e1.finpow() * 156, e.rotation, 35, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1 + Mathf.sin(e1.fin() * 6));
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f + Mathf.sin(e1.fin() * 6));
                });
                Draw.reset();
            });

            e.scaled(25, e2 -> {
                color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), e.fin());
                Draw.alpha(0.85f * e2.fout() * e2.fout());
                randLenVectors(e.id, 14, e2.finpow() * 126, e.rotation, 45, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1 + Mathf.sin(e2.fin() * 6));
                });
            });

            color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), e.fin());
            Draw.alpha(Mathf.clamp(e.fout() * 3, 0, 1));
            randLenVectors(e.id, 11, e.finpow() * e.finpow() * 156, e.rotation, 55, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 0.75f + Mathf.sin(e.fin() * 6));
            });
            randLenVectors(e.id, 15, e.finpow() * e.finpow() * 126, e.rotation, 45, (x, y) -> {
                Fill.square(e.x + x, e.y + y, e.fout() * 0.25f + Mathf.sin(e.fin() * 6));
            });
        }),

        voidExplosion = new Effect(65, e -> {
            Draw.color(Color.black, Color.black, e.fout());
            Lines.stroke(e.fout() * 6);
            float alpha = 1 - Mathf.sin(e.fout() * Mathf.PI + Mathf.PI/3);
            Draw.alpha(alpha);
            Lines.stroke(e.fout() * 2 + Mathf.sin(e.fin() * 4 * Mathf.PI));
            float scaling = -Mathf.sin(e.fout() * e.fout() * Mathf.PI + Mathf.PI/3);
            float radius = e.rotation;
            Color fromColor = Color.valueOf("#9c7ae1"), toColor = Color.valueOf("#231841");
            fromColor.a = alpha;
            toColor.a = alpha;
            Fill.light(e.x, e.y, 15, scaling * radius, fromColor, toColor);
            Lines.circle(e.x, e.y, scaling * radius);
            Angles.randLenVectors(e.id, (int) (radius * 0.35f) , -scaling *  radius, e.rotation, 360, (x, y) -> {
                Draw.color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), Math.abs(x/30) * Math.abs(y/30) * e.fout());
                Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f + Mathf.sin(e.fin() * 4 * Mathf.PI));
            });
            Angles.randLenVectors(e.id, (int) (radius * 0.25f) , -scaling * radius, e.rotation, 360, (x, y) -> {
                Draw.color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), Math.abs(x/30) * Math.abs(y/30) * e.fout());
                Fill.square(e.x + x, e.y + y, e.fout() * 2 + Mathf.sin(e.fin() * 4 * Mathf.PI));
            });
        }),

        salty = new Effect(35f, e -> {
            color(Color.white, Palr.dustriken, e.fin());

            randLenVectors(e.id, 3, 2f + e.fin() * 8f, (x, y) -> {
                Fill.square(e.x + x, e.y + y, 0.2f + e.fout() * 1.3f);
            });
        }),

        shootLongThorFlame = new Effect(123f, 80f, e -> {
            color(Pal.lightTrail, Pal.spore, Color.gray, e.finpow());
            Draw.alpha(e.fout() * 0.25f + 0.75f);

            randLenVectors(e.id, 15, e.finpow() * 172f, e.rotation, 10f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.9f, 0)/9 * 10 * 9 * Tmp.v1.set(x, y).len()/172 * 9, e.fout() * 2.6f);
            });
        }),

        shootShortPyraFlame = new Effect(63f, 80f, e -> {
            color(Pal.lightTrail, Pal.darkPyraFlame, Color.gray, e.finpow());
            Draw.alpha(e.fout() * 0.25f + 0.75f);

            randLenVectors(e.id, 7, e.finpow() * 45f, e.rotation, 21f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.9f, 0)/9 * 10 * 9 * Tmp.v1.set(x, y).len()/172 * 9, e.fout() * 2.6f);
            });

            color(Pal.lightTrail, Pal.darkPyraFlame, Color.gray, e.finpow());
            Draw.alpha(e.fout() * 0.25f + 0.75f);

            randLenVectors(e.id, 3, e.finpow() * 80f, e.rotation, 13f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.9f, 0)/9 * 10 * 9 * Tmp.v1.set(x, y).len()/172 * 9, e.fout() * 2.6f);
            });
        }),

        shootLongPyraFlame = new Effect(123f, 80f, e -> {
            color(Pal.lightTrail, Pal.darkPyraFlame, Color.gray, e.finpow());
            Draw.alpha(e.fout() * 0.25f + 0.75f);
    
            randLenVectors(e.id, 15, e.finpow() * 172f, e.rotation, 10f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.9f, 0)/9 * 10 * 9 * Tmp.v1.set(x, y).len()/172 * 9, e.fout() * 2.6f);
            });
        }),

        healingWaterSmoke = new Effect(323f, 80f, e -> {
            color(Liquids.water.color, Color.gray, e.finpow());
            Draw.alpha(e.finpow() * 0.75f + 0.25f);

            Draw.z(Mathf.lerp(Layer.groundUnit + 1, Layer.flyingUnit + 4, Math.min(e.fin() * 2, 1)));

            randLenVectors(e.id, 2, e.finpow() * 12, e.rotation, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.3f, 0)/7 * 10 * 9 * Tmp.v1.set(x, y).len()/10f * 9, e.fout() * 1.6f);
            });
        }),

        healingColdWaterSmoke = new Effect(423f, 80f, e -> {
            color(RustingLiquids.melomae.color, Color.gray, e.finpow());
            Draw.alpha(e.finpow() * 0.75f + 0.25f);

            Draw.z(Mathf.lerp(Layer.groundUnit + 1, Layer.flyingUnit + 4, Math.min(e.fin() * 2, 1)));

            randLenVectors(e.id, 2, e.finpow() * 12, e.rotation, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.3f, 0)/7 * 10 * 9 * Tmp.v1.set(x, y).len()/10f * 9, e.fout() * 1.6f);
            });
        }),

        magmaVentWater = new Effect(323f, 80f, e -> {
            color(Liquids.water.color, Color.lightGray, e.finpow());
            Draw.alpha(e.finpow() * 0.75f + 0.25f);

            Draw.z(Mathf.lerp(Layer.groundUnit + 1, Layer.flyingUnit + 4, Math.min(e.fin() * 2, 1)));

            randLenVectors(e.id, 2, e.finpow() * 12, e.rotation, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y + Math.max(e.finpow() - 0.3f, 0)/7 * 10 * 9 * Tmp.v1.set(x, y).len()/10f * 9, e.fout() * 1.6f);
            });
        }),

        singingFlame = new Effect(18, e ->{
            color(Pal.lightPyraFlame, Pal.darkPyraFlame, e.fin() * e.fin());
            float vx = e.x, vy = e.y;
            float finalVx = vx;
            float finalVy = vy;
            randLenVectors(e.id, 3, 2f + e.fin() * 16f, e.rotation + 180, 15, (x, y) -> {
                Fill.circle(finalVx + x, finalVy + y, 0.2f + e.fout() * 1.5f);
            });
        }),

        burningFlame = new Effect(15, e ->{
            color(Palr.lightstriken, Pal.lightPyraFlame, Palr.darkPyraBloom, e.fin() * e.fin());
            randLenVectors(e.id, 4, 2f + e.fin() * 21f, e.rotation + 180, 17, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.5f);
            });
        }),

        paveFlame = new Effect(45, e ->{
            randLenVectors(e.id, 5, 7f + e.fin() * 16f, e.rotation + 180, 15, (x, y) -> {
                color(Pal.lighterOrange, Pal.lightFlame, Math.abs(x * y/4));
                Fill.circle(e.x + x, e.y + y, e.fout() * e.fout() * 2.3f);
            });
        }),

        shootMhemFlame = new Effect(25f, 80f, e -> {
            color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin() * e.fin());
            randLenVectors(e.id, 6, e.finpow() * 45f, e.rotation, 10f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.6f);
            });
        }),

        launchCraeWeavers = new Effect(85f, 80f, e -> {
            color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
            Draw.alpha(e.fout());
            randLenVectors(e.id, 4, e.fin() * 55f, e.rotation, 6, (x, y) -> {
                float rotx =  Angles.trnsx(e.fslope() * e.fslope(), e.fslope() * e.fslope() * 8, e.fin() * 360 - 90), roty = Angles.trnsx(e.fslope() * e.fslope(), e.fslope() * e.fslope() * 9, e.fin() * 360 - 90);
                Fill.circle(e.x + x + rotx, e.y + y + roty, e.fin() * 3f);
            });


            randLenVectors(e.id, 10, e.fin() * 55f, e.rotation, 6, (x, y) -> {
                Lines.stroke(e.fout());
                float rotx =  Angles.trnsx(e.fout() * e.fout(), e.fout() * e.fout() * 8, e.fout() * 360 - 90), roty = Angles.trnsx(e.fout() * e.fout(), e.fout() * e.fout() * 9, e.fin() * 360 - 90);
                lineAngle(e.x + x + rotx, e.y + y + roty, Mathf.angle(x, y), 1f + e.fout() * 3f);
            });

            Fill.light(e.x, e.y, 15,  42 + 3 * e.fout(), Tmp.c1.set(Palr.pulseChargeStart).a(e.fout() * 0.45f), Tmp.c2.set(Palr.pulseChargeEnd).a(e.fout() * 0.25f));
        }),

        craeCorsair = new Effect(135f, 80f, e -> {
            color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
            alpha(e.fout() * e.fout() * 0.5f);
            if(e.data instanceof TextureRegion) Draw.rect((TextureRegion) e.data, e.x, e.y, e.rotation);
            alpha(e.fout() * e.fout());
            for(int i = 0; i < 4; i++){
                float tnx = Angles.trnsx(i * 90 + e.fin() * 360, 0, 3), tny = Angles.trnsy(i * 90 + e.fin() * 360, 0, 3);
                randLenVectors(e.id, 3, e.fin() * 6 + 5, i * 90 - 90, 2, (x, y) -> {
                    Lines.lineAngle(e.x + tnx + x, e.y + tny + y, Mathf.angle(x, y), e.fout());
                });
            }
        }),

        craeWeaversResidue = new Effect(32f, 80f, e -> {
            color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
            Draw.alpha(e.fout() * 0.65f);
            randLenVectors(e.id, 1, e.fin() * 55f, e.rotation, 3, (x, y) -> {
                float rotx =  Angles.trnsx(e.fslope(), e.fslope() * 8, e.fin()), roty = Angles.trnsx(e.fslope() * e.fslope() * 360, e.fslope() * 9, e.fin());
                Fill.circle(e.x + x + rotx, e.y + y + roty, e.fin() * 3f);
            });


            randLenVectors(e.id, 3, e.fin() * 55f, e.rotation, 3, (x, y) -> {
                Lines.stroke(e.fout());
                float rotx =  Angles.trnsx(e.fslope(), e.fslope() * 8, e.fin() * 360 - 90), roty = Angles.trnsx(e.fslope(), e.fslope() * 9, e.fin() * 360 - 90);
                lineAngle(e.x + x + rotx, e.y + y + roty, Mathf.angle(x, y), 1f + e.fout() * 3f);
            });
        }),

        craeWeaverShards = new Effect(125f, e -> {
            color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
            Draw.alpha(e.fout() * e.fout());
            for(int i = 0; i < 3; i++){
                float tnx = Angles.trnsx(i * 120 + e.finpow() * 360 + e.rotation - 90, 0, 5), tny = Angles.trnsy(i * 120 + e.finpow() * 360 + e.rotation - 90, 0, 5);
                randLenVectors(e.id, 2, e.fin() * 6 + 5, i * 120 + e.rotation - 90, 2, (x, y) -> {
                    Lines.lineAngle(e.x + tnx + x, e.y + tny + y, angle(x, y) - 90, e.fout() * e.fout());
                });
            }
        }),

        craeBeamHit = new Effect(45, e -> {
            color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
            Draw.alpha(e.fout() * e.fout());
            Lines.stroke(e.finpow() * 1.75f);

            randLenVectors(e.id, 3, e.fin() * 16 + 3, 0, 360, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, angle(x, y), e.fout() * e.fout() * 5);
            });

            randLenVectors(e.id, 2, e.fin() * 17 + 4, 0, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * e.fout() * 1.5f);
            });
        }),

        whoosh = new Effect(15, e -> {
            if(!(e.data instanceof Point2)) return;
            Point2 endPos = (Point2) e.data;
            Draw.color(e.color, e.color, 1);
            Draw.alpha(e.finpow());
            randLenVectors(e.id, 2, e.fin() * 6 + 5, e.rotation - 90, 2, (x, y) -> {
                float x2 = Mathf.lerp(e.x + x, endPos.x, Interp.fastSlow.apply(e.fin()));
                float y2 = Mathf.lerp(e.y + y, endPos.y, Interp.fastSlow.apply(e.fin()));
                Lines.lineAngle(x2, y2, angle(x2 - x, y2 - y) - 90, e.fout() * e.fout() * 5);
            });
        }),

        spawnerBulatExplosion = new Effect(145, e -> {

            color(Color.gray);

            randLenVectors(e.id, 9, 3f + 26f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * e.fout() * 4f + 0.5f);
            });

            color(Pal.plastaniumBack);
            stroke(e.fout());

            randLenVectors(e.id + 1, 4, 1f + 30f * e.finpow(), (x, y) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
            });

            Draw.color(Pal.plasticSmoke);
            e.scaled(85, h -> {
                Draw.alpha(h.fout() * 0.15f);
                Angles.randLenVectors(e.id, 6, 3 + 25 * h.fout(), (x, y) -> {
                    Fill.circle(h.x + x, h.y + y, (28 - Mathf.dst(x, y))/ 2 * (1 - h.fslope() * h.fslope()));
                });
            });

        }),

        pulseExplosion = new Effect(85f, e -> {
            float nonfinalSplosionRadius = 42 + 3 * e.fout();
            int clouds = 5;
            if(e.data instanceof CraeUnitType) {
                nonfinalSplosionRadius = ((CraeUnitType) e.data).hitSize * 4 + 16 + ((CraeUnitType) e.data).hitSize * 2 * e.fout();
                clouds = (int) ((CraeUnitType) e.data).hitSize/3 + 3;
            }
            else if(e.data instanceof float[]){
                float[] args = (float[]) e.data;
                if(args.length > 0){
                    nonfinalSplosionRadius = args[0];
                    clouds = (int) args[1];
                }
            }
            final float splosionRadius = nonfinalSplosionRadius;

            color(Palr.pulseChargeStart);

            e.scaled(clouds + 2, i -> {
                stroke(3f * i.fout());
                Lines.circle(e.x, e.y, 3f + i.fin() * splosionRadius);
            });

            randLenVectors(e.id, clouds, 2f + 23f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * splosionRadius/2 + 0.5f);
            });
            stroke(e.fout());


            color(Palr.pulseChargeEnd);
            randLenVectors(e.id + 1, clouds - 1, 1f + 23f * e.finpow(), (x, y) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * splosionRadius/2.5f);
            });
        }),

        chainLightning = new Effect(25f, 300f, e -> {
            if(!(e.data instanceof VisualLightningHolder)) return;
            VisualLightningHolder p = (VisualLightningHolder) e.data;

            //get the start and ends of the lightning, then the distance between them
            float tx = Tmp.v1.set(p.start()).x, ty = Tmp.v1.y, dst = Mathf.dst(Tmp.v2.set(p.end()).x, Tmp.v2.y, tx, ty);

            Tmp.v3.set(p.end()).sub(p.start()).nor();
            float normx = Tmp.v3.x, normy = Tmp.v3.y;

            rand.setSeed(e.id + ((int) e.fin() * 3));

            float arcWidth = rand.range(dst * (p.owner() != null ? p.owner().arc : 0.85f));

            float angle = Tmp.v1.angleTo(Tmp.v2);

            Floatp arcX = () -> {
                return Mathf.sinDeg(precent * 180) * arcWidth;
            };

            //range of lightning strike's vary depending on turret
            float range = p.owner() != null ? p.owner().width : 15f;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / links;
    
            Lines.stroke(2.5f * e.fout());
            Draw.color(Color.white, e.color, e.fin());

            //begin the line
            Lines.beginLine();
    
            Lines.linePoint(Tmp.v1.x, Tmp.v1.y);
            float lastx = Tmp.v1.x, lasty = Tmp.v1.y;
            rand.setSeed(e.id);
    
            for(int i = 0; i < links; i++){
                float nx, ny;
                if(i == links - 1){
                    //line at end
                    nx = Tmp.v2.x;
                    ny = Tmp.v2.y;
                }else{
                    float len = (i + 1) * spacing;
                    rand.setSeed(e.id + i);
                    Tmp.v3.setToRandomDirection().scl(range/2);
                    precent = ((float) (i + 1))/links;

                    nx = tx + normx * len + Tmp.v3.x + Tmp.v4.set(0, arcX.get()).rotate(angle).x;
                    ny = ty + normy * len + Tmp.v3.y + Tmp.v4.y;
                }

                Drawf.light(lastx, lasty, nx, ny);
                lastx = nx;
                lasty = ny;
                Lines.linePoint(nx, ny);
            }
    
            Lines.endLine();
        }),
    
        pulseSmoke = new Effect(315f, e -> {
            float nonfinalSplosionRadius = 42 + 3 * e.fout();
            int clouds = 5;
            float nonfinalAlphaPercent = 1;
            if(e.data instanceof CraeUnitType) {
                nonfinalSplosionRadius = ((CraeUnitType) e.data).hitSize * 4 + 16 + ((CraeUnitType) e.data).hitSize * 2 * e.fout();
                clouds = (int) ((CraeUnitType) e.data).hitSize/3 + 3;
            }
            else if(e.data instanceof float[]){
                float[] args = (float[]) e.data;
                if(args.length > 0){
                    nonfinalSplosionRadius = args[0];
                    clouds = (int) args[1];
                    nonfinalAlphaPercent = args[2];
                }
            }
            final float splosionRadius = nonfinalSplosionRadius;
            final float alphaPercent = Math.min(e.fin() * 315, 60)/60 * nonfinalAlphaPercent;

            Draw.color(Pal.plasticSmoke, Pal.darkestGray, e.fslope() * e.fslope());

            randLenVectors(e.id, clouds * 2, splosionRadius * e.finpow() + 5, e.rotation,  360, (x, y) -> {
                float distance = Mathf.dst(x, y);
                Fill.circle(e.x + x,e.y + y, (1 - distance/(splosionRadius/7 + 5)) * e.fout() * e.fout() * 2);
            });

            if(e.data instanceof CraeUnitEntity) Draw.color(((CraeUnitType) e.data).chargeColourStart, ((CraeUnitType) e.data).chargeColourEnd, e.fin());
            else Draw.color(Palr.pulseChargeStart, Palr.pulseChargeEnd, e.fin());
            Draw.alpha(alphaPercent * e.fout() * 8/10);

            randLenVectors(e.id, clouds * 3, splosionRadius * e.finpow(), e.rotation,  360, (x, y) -> {
                float distance = Mathf.dst(x, y);
                Draw.alpha((1 - distance/(splosionRadius/9.5f - 5)) * e.fout() * e.fout() * 0.15f + 0.85f * e.fout() * alphaPercent);
                Fill.circle(e.x + x,e.y + y, splosionRadius/3.5f);
                Drawf.light(e.x + x, e.y + y, splosionRadius/3.5f, Palr.pulseChargeStart, Draw.getColor().a);
            });

            randLenVectors(e.id, clouds, splosionRadius/1.5f * e.finpow(), e.rotation,  360, (x, y) -> {
                float distance = Mathf.dst(x, y);
                Draw.alpha((1 - distance/(splosionRadius/7.5f - 5)) * e.fout() * e.fout() * 0.25f + 0.75f * e.fout() * e.fout() * alphaPercent);
                Fill.circle(e.x + x,e.y + y, splosionRadius/7.25f);
                Drawf.light(e.x + x, e.y + y, splosionRadius/5.25f, Palr.pulseChargeEnd, e.fout() * 0.65f);
            });
        }),

        lineCircles = new Effect(335f, e -> {
            Draw.color(e.color);
            float[][][] arrays = ((float[][][]) e.data);
            float[][] params = arrays[0];
            //first array contains params, second array contains points
            float width = params[0][0];
            float circleRadius = params[0][1];
            float alpha = params[1][0];
            float tickThresholdOut = params[1][1];
            float tickThreshold = params[1][2];
            float[][] points = arrays[1];

            for (int i = 0; i < points[0].length - 1; i++) {
                int ic = i + 1;
                float alphaDraw = Mathf.clamp(e.fin() * 335f/tickThreshold * ic, 0, 1);
                float alphaDrawOut = Mathf.clamp(tickThresholdOut * (points[0].length - ic)/(e.fin() * 335f), 0, 1);
                Draw.alpha(Math.min(alphaDraw, alphaDrawOut) * alpha);
                Lines.stroke(width);
                Lines.line(points[0][i], points[1][i], points[0][i+ 1], points[1][i + 1]);
                Fill.circle(points[0][i + 1], points[1][i + 1], circleRadius);
            }
        }),

        instaltSummoner = new Effect(240, e -> {
            Draw.color(Palr.lightstriken, Palr.dustriken, e.fin());
            float initialScaling = Math.min(e.fout() * 4, Math.min(e.fin() * 4, 1));
            float scaling = e.fout() * 4;
            Draw.alpha(initialScaling);
            Fill.circle(e.x, e.y, initialScaling * 8);
            float spacing = 2;
            for(float i = 0; i < scaling; i++){
                Lines.stroke((scaling - i) * spacing/3);
                Lines.circle(e.x, e.y, (scaling - i) * spacing + i + 8);
            }
        }),

        stingrayShieldPop = new Effect(115, e -> {
            Tmp.v2.set(e.x, e.y);
            if(!(e.data instanceof Unit)) Tmp.v2.set(((Unit) e.data).x, ((Unit) e.data).y);
            Draw.color(Palr.pulseChargeStart, Palr.pulseBullet, e.fout());
            e.scaled(25, l -> {
                Lines.stroke(1 - l.finpow());
                Lines.circle(Tmp.v2.x, Tmp.v2.y, 65 * e.fin());
            });
            e.scaled(55, l -> {
                Lines.stroke(l.fslope() * 2);
                Lines.circle(Tmp.v2.x, Tmp.v2.y, 85);
            });
            Lines.stroke(4 - e.finpow() * 4);
            Lines.circle(e.x, e.y, 48 * e.fin());
            Lines.stroke(2 - e.finpow() * 2);
            Lines.square(Tmp.v2.x, Tmp.v2.y, 42 * e.fin(), Mathf.absin(Time.delta/5, 360));
            Lines.square(Tmp.v2.x, Tmp.v2.y, 42 * e.fin(), Mathf.absin(Time.delta/5, 360) + 180);
        }),

        craeNukeHit = new Effect(125, 125, e -> {
            e.scaled(10f, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fout() * b.fslope() * 3f + 0.2f);
            Lines.circle(b.x, b.y, (1 - b.finpow()) * 60f);
        });

        e.scaled(56f, b -> {
            color(Color.white, Palr.pulseBullet, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Lines.circle(b.x, b.y, (1 - b.finpow()) * 75f);
        });

        Drawf.light(e.x, e.y, e.finpow() * 125, Palr.pulseBullet, e.fslope() * e.fslope());

        for(int i : Mathf.signs){
            color(Palr.pulseBullet);
            Drawf.tri(e.x, e.y, 17f * e.fout(), 85f - e.finpow() * 23, Angles.moveToward(e.rotation - 65f * i, e.rotation, e.finpow() * 65));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 15f * (1 - e.finpow()), 50f - e.finpow() * 9, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
        }

        for(int i : Mathf.signs){
            color(Color.white);
            Draw.alpha(0.25f * e.fin() + 0.55f * e.fslope());
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 150f - e.fout() * 75);
            Drawf.light(Tmp.v1.x + e.x, Tmp.v1.y + e.y, e.finpow() * 5, Palr.pulseBullet, e.fslope() * e.fslope());
            Drawf.tri(e.x, e.y, 17f * e.fout(), 95f - e.fout() * 45, Angles.moveToward(e.rotation - 65f * i, 35, e.finpow() * 65));
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 95f - e.fslope() * 45);
            Drawf.tri(e.x, e.y, 15f * (1 - e.finpow()), 150f - e.fslope() * 75, Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95));
            Drawf.light(Tmp.v1.x + e.x, Tmp.v1.y + e.y, e.finpow() * 6, Palr.pulseBullet, e.fslope() * e.fslope());
        }

        Draw.alpha(1);

        Fill.circle(e.x, e.y, e.fout()  * e.fout() * 8);
    }),

    craeBigNukeHit = new Effect(125, 225, e -> {
        e.scaled(25f, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fout() * b.fslope() * 3f + 0.2f);
            Lines.circle(b.x, b.y, (1 - b.finpow()) * 155);
        });

        e.scaled(34f, b -> {
            color(Color.white, Palr.pulseBullet, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Lines.circle(b.x, b.y, (1 - b.finpow()) * 75f);
        });

        e.scaled(96, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Lines.circle(b.x, b.y, 1 - b.finpow() * 225f);
            color(Color.white, Palr.lightstriken, b.fin());
            Lines.circle(b.x, b.y, 1 - b.finpow() * 257f);
        });

        e.scaled(96, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Angles.randLenVectors(e.id, 16, Mathf.absin(b.finpow() * 4, 95), e.rotation, 25, (x, y) -> {
                Lines.circle(e.x + x, e.y + y, b.fout() * b.fout());
            });
        });

        Drawf.light(e.x, e.y, e.finpow() * 125, Palr.pulseBullet, e.fslope() * e.fslope());

        for(int i : Mathf.signs){
            color(Palr.pulseBullet);
            Drawf.tri(e.x, e.y, 21f * e.fout(), 215f - e.finpow() * 175, Angles.moveToward(e.rotation - 65f * i, e.rotation, e.finpow() * 65));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 27 * (1 - e.finpow()), 180f - e.finpow() * 65, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 35 * (1 - e.finpow()), 190f - e.finpow() * 70, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
        }

        for(int i : Mathf.signs){
            color(Color.white);
            Draw.alpha(0.35f * e.fin() + 0.55f * e.fslope());
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 150f - e.fout() * 75);
            Drawf.light(Tmp.v1.x + e.x, Tmp.v1.y + e.y, e.finpow() * 5, Palr.pulseBullet, e.fslope() * e.fslope());
            Drawf.tri(e.x, e.y, 17f * e.fout(), 95f - e.fout() * 45, Angles.moveToward(e.rotation - 65f * i, 35, e.finpow() * 65));
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 95f - e.fslope() * 45);
            Drawf.tri(e.x, e.y, 15f * (1 - e.finpow()), 150f - e.fslope() * 75, Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95));
            Drawf.light(Tmp.v1.x + e.x, Tmp.v1.y + e.y, e.finpow() * 6, Palr.pulseBullet, e.fslope() * e.fslope());
        }

        Draw.alpha(1);

        Fill.circle(e.x, e.y, e.fout()  * e.fout() * 45);
    }),

    craeNukebuthehe = new Effect(265, 225, e -> {
        e.scaled(25f, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fout() * b.fslope() * 3f);
            Lines.circle(b.x, b.y, (b.finpow()) * 155);
        });

        e.scaled(34f, b -> {
            color(Color.white, Palr.pulseBullet, b.fin());
            stroke(b.fslope() * b.fslope() * 4f);
            Lines.circle(b.x, b.y, (b.finpow()) * 75f);
        });

        e.scaled(45, b -> {
            Draw.alpha(Math.min(e.fout() * 2, 1));
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f);
            Lines.circle(b.x, b.y, b.finpow() * 225f);
            color(Color.white, Palr.lightstriken, b.fin());
            Lines.circle(b.x, b.y, b.finpow() * 197f);
        });

        e.scaled(125, b -> {
            Draw.alpha(Math.min(e.fout() * 3, 1));
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Angles.randLenVectors(e.id, 35, Mathf.absin(b.finpow() * 4, 155), e.rotation, 360, (x, y) -> {
                Lines.circle(e.x + x, e.y + y, b.fout() * b.fout());
            });
        });

        Drawf.light(e.x, e.y, e.finpow() * 125, Palr.pulseBullet, e.fslope() * e.fslope());

        for(int i : Mathf.signs){
            color(Palr.pulseBullet);
            Drawf.tri(e.x, e.y, 21f * e.fout(), 215f - e.finpow() * 175, Angles.moveToward(e.rotation - 65f * i, e.rotation, e.finpow() * 65));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 27 * (1 - e.finpow()), 180f - e.finpow() * 65, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 35 * (1 - e.finpow()), 190f - e.finpow() * 70, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
        }

        for(int i : Mathf.signs){
            color(Color.white);
            Draw.alpha(0.35f * e.fin() + 0.55f * e.fslope());
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 150f - e.fout() * 167);
            Drawf.tri(e.x, e.y, 17f * e.fout(), 95f - e.fout() * 45, Angles.moveToward(e.rotation - 65f * i, 35, e.finpow() * 65));
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 95f - e.fslope() * 45);
            Drawf.tri(e.x, e.y, 15f * (1 - e.finpow()), 150f - e.fslope() * 75, Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 135));
        }
        color(Palr.pulseChargeStart);

        Draw.alpha(Math.min(e.finpow() * 5, 0.45f));

        Fill.circle(e.x, e.y, e.fout() * e.fout() * 165);

        Draw.alpha(Math.min(e.finpow() * 2, 1f));
        Fill.circle(e.x, e.y, e.fout() * e.fout() * 55);
        Drawf.light(e.x, e.y, e.finpow() * 150, Palr.pulseBullet, e.fslope() * e.fslope());
    }),

    spontaniumCOMBUSTOMTHATSTHESPELLWHICHMAKESANYONEWHOSAYSITEXPLO = new Effect(1700, 1200, e -> {

        e.scaled(300, b -> {
            Drawf.light(e.x, e.y, b.finpow() * 185, Palr.pulseBullet, b.fout());
        });

        e.scaled(1100, b -> {
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fout() * b.fslope() * 6f);
            Lines.circle(b.x, b.y, (b.finpow()) * 975);
            Draw.z(Layer.flyingUnit + 1);
            Fill.light(e.x, e.y, 60, e.finpow() * 875, Tmp.c1.set(Palr.darkerPulseChargeStart).a(b.fslope()/4), Tmp.c2.set(Palr.pulseChargeStart).a(b.fslope()));
            Draw.blend(Blending.additive);
            Fill.light(e.x, e.y, 60, e.finpow() * 875, Color.clear, Tmp.c2.set(Color.white).a(b.fslope() /3));
            Draw.blend();
            Drawf.light(e.x, e.y, e.finpow() * 875, Palr.pulseBullet, b.fslope() * b.fslope());
            Draw.z(Layer.effect);
        });

        e.scaled(1300, b -> {
            color(Color.white, Palr.pulseBullet, b.fin());
            stroke(b.fslope() * b.fslope() * 4f);
            Lines.circle(b.x, b.y, (b.finpow()) * 675);
        });

        e.scaled(900, b -> {
            Draw.alpha(Math.min(e.fout() * 2, 1));
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f);
            Lines.circle(b.x, b.y, b.finpow() * 225f);
            color(Color.white, Palr.lightstriken, b.fin());
            Lines.circle(b.x, b.y, b.finpow() * 197f);
        });

        e.scaled(250, b -> {
            Draw.alpha(Math.min(e.fout() * 3, 1));
            color(Color.white, Palr.pulseShieldEnd, b.fin());
            stroke(b.fslope() * b.fslope() * 4f + 0.65f);
            Angles.randLenVectors(e.id, 35, Mathf.absin(b.finpow() * 4, 155), e.rotation, 360, (x, y) -> {
                Lines.circle(e.x + x, e.y + y, b.fout() * b.fout());
            });
        });

        Drawf.light(e.x, e.y, e.finpow() * 850, Palr.pulseBullet, e.fslope() * e.fslope());

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 21f * e.fout(), 550f - e.finpow() * 350f, Angles.moveToward(e.rotation - 65f * i, e.rotation, e.finpow() * 65));
            color(Palr.pulseChargeEnd);
            Drawf.tri(e.x, e.y, 27 * (1 - e.finpow()), 420 - e.finpow() * 340, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
            Drawf.tri(e.x, e.y, 35 * (1 - e.finpow()), 360 - e.finpow() * 295, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
            Drawf.tri(e.x, e.y, 35 * (1 - e.finpow()), 260 - e.finpow() * 195, Angles.moveToward(e.rotation + 85f * i, e.rotation, e.finpow() * 95));
        }

        for(int i : Mathf.signs){
            color(Color.white);
            Draw.alpha(0.35f * e.fin() + 0.55f * e.fslope());
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 150f - e.fout() * 167);
            Drawf.tri(e.x, e.y, 17f * e.fout(), 95f - e.fout() * 45, Angles.moveToward(e.rotation - 65f * i, 35, e.finpow() * 65));
            Tmp.v1.trns(Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 95), 95f - e.fslope() * 45);
            Drawf.tri(e.x, e.y, 15f * (1 - e.finpow()), 150f - e.fslope() * 75, Angles.moveToward(e.rotation + 85f * i, 35, e.finpow() * 135));
        }
        color(Palr.pulseChargeStart);

        Draw.alpha(Math.max(e.fout() * 5, 1));

        Draw.blend(Blending.additive);
        Draw.z(Layer.flyingUnit + 1);
        Fill.light(e.x, e.y, 45, e.fout() * e.fout() * 150, Tmp.c1.set(Color.white), Tmp.c2.set(Color.clear).a(Math.min(e.fout() * 2, 1)));
        Draw.z(Layer.effect);
        Draw.blend();

        Fill.circle(e.x, e.y, e.fout() * e.fout() * 165);
        Lines.stroke(e.fout() * 5);
        Lines.circle(e.x, e.y, e.fout() * e.fout() * 175 + Mathr.helix(35, 15 * e.fout(), e.fout()));
        Lines.stroke(e.fout() * 10);
        Lines.circle(e.x, e.y, e.fout() * e.fout() * 175 + Mathr.helix(23, 34 * e.fout(), e.fout()));

        Draw.alpha(Math.min(e.fslope() * 10, 1f));
        Fill.circle(e.x, e.y, Math.max(e.fout() * e.fout() * 55, 15));
        Drawf.light(e.x, e.y, e.fout() * e.fout() * 150, Palr.pulseBullet, e.fslope() * e.fslope());
    }),


    //modified flak explosiongradlew clean build
    instaltSummonerExplosion = new Effect(45, e -> {
        color(Pal.bulletYellowBack);

        e.scaled(15, i -> {
            stroke(3f * i.fout());
            Lines.circle(e.x, e.y, 5f + i.fin() * 25f);

        });

        e.scaled(6, i -> {
            stroke(4f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 35f);
        });

        color(Color.gray);

        randLenVectors(e.id, 7, 2f + 33f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 4.5f + 0.55f);
        });

        color(Pal.bulletYellow);
        stroke(e.fout());

        randLenVectors(e.id + 1, 6, 1f + 23f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 5f);
        });
    }),

    instaltSummonerExplosionLarge = new Effect(105, e -> {
        color(Pal.bulletYellowBack);

        e.scaled(25, i -> {
            stroke(3f * i.fout());
            Lines.circle(e.x, e.y, 5f + i.fin() * 75f);

        });

        e.scaled(9, i -> {
            stroke(5f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 105f);
        });

        color(Color.gray);

        randLenVectors(e.id, 15, 2f + 43f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5.5f + 0.55f);
        });

        color(Pal.bulletYellow);
        stroke(e.fout());

        randLenVectors(e.id + 1, 12, 1f + 43f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 5f);
        });
    }),

    powderyExplosion = new Effect(65, e -> {
        e.scaled(25, e2 -> {
            stroke(3f * e2.fout());
            Lines.circle(e.x, e.y, 5f + e2.fin() * 75f);

            color(Pal.stoneGray);
            randLenVectors(e.id, 12, 3f + e2.finpow() * 45f, (x, y) -> {
                Fill.square(e.x + x, e.y + y, e2.fout() * 1.25f + 0.5f, 45);
            });
        });

        randLenVectors(e.id, 7, 3f + e.fin() * 13f, (x, y) -> {
            color(Pal.stoneGray);
            Fill.square(e.x + x, e.y + y, e.fout() * 3f + 0.5f, 45);
        });
    }),

    earthpoundShockwave = new Effect(225, 150, e -> {
        for (int i = 0; i < 5; i++) {
            int[] l = {i};
            Draw.z(Layer.groundUnit + 1);
            e.scaled(65 + 30 * (i + 1)/5 * (i + 1)/5 * 5, h -> {
                Tmp.v1.trns(e.rotation, h.finpow() * (60 + (4 - l[0] + 1) * 18));
                Draw.color(Palr.lightstriken, Palr.dustriken, h.fin());
                Lines.stroke((3 + (5 - l[0]) * 0.25f) * h.fout() * h.fout());
                Lines.arc(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2 + 39 * h.finpow(), (115 +55 * h.fout())/360, e.rotation - 57.5f - 27.5f * h.fout());
            });
        }
        e.scaled(35, h -> {
            Angles.randLenVectors(e.id, 4, 15, (ex, ey) -> {
                color(Pal.orangeSpark, Color.gray, h.fin());
                Lines.stroke(h.fslope() * 5);
                Lines.circle(e.x + ex, e.y + ey, e.fout() * 2);
                Angles.randLenVectors(e.id + 1, 3, 1, (x, y) -> {
                    color(Pal.plasticSmoke, Pal.bulletYellow, h.fin());
                    Lines.stroke(0.5f);
                    Lines.lineAngle(e.x + x + ex, e.y + y + ey, Mathf.angle(x, y), h.finpow() * 3.5f);
                });
            });
        });
    }),

    //used by the particle spawner
    blueSpark = new Effect(185, e -> {
        Draw.color(Palr.pulseBullet, Items.graphite.color, e.fout());
        Draw.alpha(e.fout() * 0.8f);
        e.scaled(75, h -> {
            Angles.randLenVectors(e.id, 5, 35, 0, 360, (x, y) -> {
                Lines.lineAngle(e.x + x * e.finpow() * 0.25f, e.y + y * e.finpow() * 0.25f, Mathf.angle(x, y), e.finpow() * 3.5f);
            });
            Angles.randLenVectors(e.id, 2, 85, 0, 360, (x, y) -> {
                Lines.lineAngle(e.x + x * e.finpow() * 4.25f, e.y + y * e.finpow() * 4.25f, Mathf.angle(x, y), e.finpow() * 5.5f);
            });
        });

        Draw.alpha(Mathf.clamp(e.fout() * 0.3f * 3, 0, 0.8f));
        Angles.randLenVectors(e.id, 2, 145, 0, 360, (x, y) -> {
            Angles.randLenVectors(e.id, 1, 145, 0, 360, (x2, y2) -> {
                Fill.circle(e.x + x + x2 * e.fin(), e.y + y + y2 * e.fin(), Mathf.clamp(e.fslope() * 2.5f * 3, 0, 2.5f));
            });
        });
    }),

    skyractureShot = new Effect(25, e -> {
        Draw.alpha(0.75f * e.fout());
        Draw.color(Palr.pulseBullet, Items.titanium.color, e.fout());
        Lines.stroke(1.25f * e.fout());
        Lines.circle(e.x, e.y, e.finpow() * 16);
        Lines.stroke(3 * e.fout());
        Lines.circle(e.x, e.y, e.finpow() * 6);
        for (int i = 0; i < 9; i++) {
            Tmp.v1.trns(i * 40 + e.rotation, e.finpow() * 3 + e.fin() * 3);
            Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 3.35f, i * 40);
        }
    }),

    skyfractureDespawn = new Effect(15, e -> {
        Draw.alpha(e.finpow());
        Draw.color(Palr.pulseBullet, Items.titanium.color, e.fout());
        Lines.stroke(1.25f * e.finpow());
        Lines.circle(e.x, e.y, (1 - e.fslope() * e.fslope()) * 12);
        for (int i = 0; i < 9; i++) {
            Tmp.v1.trns(i * 40 + e.rotation, e.fout() * 6);
            Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 1.35f, i * 40);
        }
    }),

    skyractureTrail = new Effect(22, e -> {
        Draw.color(Palr.pulseBullet, Items.titanium.color, e.fout());
        Draw.alpha(0.25f * e.fout());
        Fill.circle(e.x, e.y, e.finpow() * 8);
        Draw.alpha(0.75f * e.fout());
        randLenVectors(e.id, 2, e.finpow() * 5, e.rotation, 360, (x, y) -> {
            Tmp.v1.trns(120 + Mathf.angle(x, y), e.finpow() * 3 + e.fin() * 3);
            Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y - e.finpow() * 9, e.fout() * 1.35f, e.fout() * 360);
        });
    }),

    skyractureBurst = new Effect(35, e -> {
        randLenVectors(e.id, 3, e.finpow() * 95, e.rotation, 360, (x, y) -> {
            Draw.color(Palr.pulseBullet, Items.titanium.color, (x + y)/23 % 1);
            Fill.square(e.x + x, e.y + y - e.fin() * 24, e.fout() * 3.35f, e.fout() * 360);
        });
        randLenVectors(e.id, 5, e.finpow() * 125, e.rotation, 360, (x, y) -> {
            Draw.color(Palr.pulseBullet, Items.titanium.color, (x + y)/39 % 1);
            Fill.square(e.x + x, e.y + y - e.fin() * 36, e.fout() * 2.35f, e.fout() * 360);
        });
    }),

    motionBlurBullet = new Effect(110, e -> {
        if(e.data instanceof BasicBulletType){
            for (int i : Mathf.signs) {
                BasicBulletType type = (BasicBulletType) e.data;

                Tmp.v1.trns(e.rotation - 90 * i, Mathr.helix(3, type.width * 3, Math.max(0, (e.finpow() - 0.5f) * 2)));


                Draw.color(type.backColor);
                Draw.alpha(0.8f - e.finpow() * 0.8f);
                Draw.rect(type.backRegion, e.x + Tmp.v1.x, e.y + Tmp.v1.y, type.width, type.height, e.rotation - 90);
                Draw.color(type.frontColor);
                Draw.alpha(0.8f - e.finpow() * 0.8f);
                Draw.rect(type.frontRegion, e.x + Tmp.v1.x, e.y + Tmp.v1.y, type.width, type.height, e.rotation - 90);
            }
        }
    }),

    regionDrop = new Effect(125, e -> {
        if(!(e.data instanceof TextureRegion)) return;
        TextureRegion region = ((TextureRegion) e.data);
        Draw.alpha(Math.min(e.fout() * 10, 1));
        randLenVectors(e.id, 1, e.finpow() * 16, e.rotation, 360, (x, y) -> {
            Draw.rect(region, e.x + x, e.y + Mathf.clamp(10 - e.fslope() * e.fslope() * 10, -9, 0), e.rotation);
        });
    }),

    regionDropERr = new Effect(35, e -> {
        TextureRegion region = Core.atlas.find("endless-rusting-PLACEHOLDER1");
        Draw.alpha(Math.min(e.fout() * 10, 1));
        randLenVectors(e.id, 1, e.finpow() * 16, e.rotation, 360, (x, y) -> {
            Draw.rect(region, e.x + x, e.y + Mathf.clamp(10 - e.fslope() * e.fslope() * 10, -9, 1), e.rotation);
        });
    })

    ;
}