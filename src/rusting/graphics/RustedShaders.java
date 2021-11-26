package rusting.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.Nullable;
import arc.util.Time;

import static mindustry.Vars.headless;
import static mindustry.Vars.tree;

public class RustedShaders {
    public static @Nullable
    NamedShader sharpeffect;

    public static @Nullable
    PulseShader pulseGradiant;
    protected static boolean loaded;

    public static void load(){
        if(headless) return;
        sharpeffect = new NamedShader("sharpeffect");
        pulseGradiant = new PulseShader("pulseGradiant");
        loaded = true;
    }

    public static void dispose(){
        if(!headless && loaded){
            sharpeffect.dispose();
            pulseGradiant.dispose();
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

}
