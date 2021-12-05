package rusting.graphics;

import arc.math.geom.Rect;
import arc.struct.Seq;
import rusting.game.PulseFieldManager;
import rusting.game.PulseFieldManager.SaddlePointSquare;

public class PulseFieldRenderer {

    public PulseFieldRenderer(PulseFieldManager owner){
        this.owner = owner;
    }

    public PulseFieldManager owner;
    //rects to render
    public Seq<Rect> rects = new Seq();

    //call whenever your updating the squaresXSZCADWEFQaszxzxscawdzxscweadfqASZXSXZCADWEFr
    public void bisectRects(Seq<SaddlePointSquare> squares){

    }

    public void draw(){

    };
}
