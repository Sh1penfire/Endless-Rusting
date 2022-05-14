package rusting.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Time;

import static mindustry.Vars.headless;
import static mindustry.Vars.tree;

public class RustedShaders {

    protected static boolean loaded;

    public static void load(){
        Log.info("loading shaders!");
        if(headless) return;
        loaded = true;
        try {
        }
        catch (IllegalArgumentException error){
        }
    }

    public static void dispose(){
        if(!headless && loaded){

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
}
