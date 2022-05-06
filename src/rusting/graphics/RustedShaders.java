package rusting.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.*;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Shaders;

import static mindustry.Vars.headless;
import static mindustry.Vars.tree;

public class RustedShaders {

    public static @Nullable PulseShader
            pulseGradiant;
    public static @Nullable NamedShader
            staticShader;
    public static @Nullable GlitchEffectShader
            glitchShader, testShader;
    protected static boolean loaded;

    public static FrameBuffer buffer,
        //captures everything before ui starts rendering
        bufferScreen;

    public static void load(){
        if(headless) return;
        loaded = true;

        buffer = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        bufferScreen = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        try {
            pulseGradiant = new PulseShader("pulseGradiant");
            staticShader = new NamedShader("static");
            testShader = new GlitchEffectShader("test");
        }
        catch (IllegalArgumentException error){
            loaded = false;
            Log.err("Failed to load ER's shaders: " + error);
        }

        Events.run(Trigger.draw, () -> {
            bufferScreen.begin(Color.clear);
        });
        Events.run(Trigger.postDraw, () -> {
            bufferScreen.end();
            Blending.disabled.apply();
            bufferScreen.blit(Shaders.screenspace);
        });

    }

    public static void dispose(){
        if(!headless && loaded){
            pulseGradiant.dispose();
            staticShader.dispose();
            testShader.dispose();
        }
    }

    /** Shaders that the the*/
    public static class NamedShader extends Shader {
        public NamedShader(String name) {
            super(Core.files.internal("shaders/default.vert"),
                    tree.get("shaders/" + name + ".frag"));
        }

        @Override
        public void apply() {
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_drawCol", Draw.getColor().r,  Draw.getColor().g,  Draw.getColor().b,  Draw.getColor().a);
        }
    }

    public static class PulseShader extends NamedShader{
        public Color startColor = Color.white;
        public float alpha = 1;

        public PulseShader(String name) {
            super(name);
        }

        @Override
        public void apply() {
            setUniformf("u_campos", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_startC", startColor.r, startColor.g, startColor.b, startColor.a);
            setUniformf("u_alpha", alpha);
        }
    }

    public static class GlitchEffectShader extends NamedShader{
        public Texture screenTex;

        public GlitchEffectShader(String name) {
            super(name);
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_texDimensions", screenTex.height, screenTex.width);
            if(hasUniform("u_texture")){
                screenTex.bind(0);
                setUniformi("u_texture", 0);
            }
            if(hasUniform("u_screenspace")){
                buffer.getTexture().bind(1);
                setUniformi("u_screenspace", 1);
                buffer.getTexture().bind();
            }
        }
    }
}
