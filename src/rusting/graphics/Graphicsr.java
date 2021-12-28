package rusting.graphics;

import arc.Core;
import arc.Graphics;
import arc.Graphics.Cursor;
import arc.Graphics.Cursor.SystemCursor;
import arc.graphics.Pixmap;
import arc.graphics.Pixmap.PixmapFilter;
import arc.graphics.Pixmaps;
import arc.struct.ObjectMap;
import arc.util.OS;

import static mindustry.ui.Fonts.cursorScale;
import static rusting.EndlessRusting.modname;
import static rusting.graphics.Graphicsr.ReplacementCursor.*;

public class Graphicsr {

    //whether a cursor is replaced or not
    public static ObjectMap<Boolean, SystemCursor> replaced = ObjectMap.of();

    //clones of the original cursors
    public static ObjectMap<SystemCursor, ReplacementCursor> clones = ObjectMap.of();

    public static void loadReplacementCursors(){
        //setup clones so that the cursors that are set can be returned to normal
        arrowClone.set(SystemCursor.arrow);
        ibeamClone.set(SystemCursor.ibeam);
        corsshairClone.set(SystemCursor.crosshair);
        handClone.set(SystemCursor.hand);
        horizontalResizeClone.set(SystemCursor.horizontalResize);
        verticalResizeClone.set(SystemCursor.verticalResize);

        //load modded cursors
        corsair.set(newCursor("corsair"));

        clones.putAll(
            ObjectMap.of(
                SystemCursor.arrow,arrowClone,
                SystemCursor.arrow, ibeamClone,
                SystemCursor.arrow, corsshairClone,
                SystemCursor.arrow, handClone,
                SystemCursor.arrow, horizontalResizeClone,
                SystemCursor.arrow, verticalResizeClone
            )
        );
    }

    public enum ReplacementCursor implements Graphics.Cursor {
        arrowClone,
        ibeamClone,
        corsshairClone,
        handClone,
        horizontalResizeClone,
        verticalResizeClone,
        corsair
        ;
        protected Graphics.Cursor cursor;

        private ReplacementCursor() {

        }

        public void set(Graphics.Cursor cursor) {
            this.cursor = cursor;
        }

        public void dispose() {
            if (this.cursor != null && !(this.cursor instanceof Graphics.Cursor.SystemCursor)) {
                this.cursor.dispose();
                this.cursor = null;
            }

        }
    }

    //Sets arrow to a corsair.
    public static void corsairCursor(){
        SystemCursor.arrow.set(corsair.cursor);
        Core.graphics.cursor(corsair);
    }

    //Reset all cursors which have been replaced with mod's cursor to normal.
    public static void resetCursors(){
        replaced.each((replaced, cursor) -> {
            if(replaced) cursor.set(clones.get(cursor));
        });
    }

    //Instead of searching the vanilla directory, search the modded one. Scale handled automatically.
    public static Cursor newCursor(String filename){
        int scale = cursorScale();
        Pixmap base = Core.atlas.getPixmap(modname + "-" + filename).crop();
        if(cursorScale() != 1 && !OS.isAndroid && !OS.isIos) {
            Pixmap result = Pixmaps.scale(base, base.getWidth() * scale, base.getHeight() * scale, PixmapFilter.nearestNeighbour);
            base.dispose();
            return Core.graphics.newCursor(result, result.getWidth() / 2, result.getHeight() / 2);
        }
        else return Core.graphics.newCursor(base, base.getWidth()/2, base.getHeight()/2);
    }
}
