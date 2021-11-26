package rusting.type;

import arc.graphics.Color;
import mindustry.type.Item;
import rusting.Varsr;

public class DebugItem extends Item {

    public DebugItem(String name) {
        super(name);
    }

    public DebugItem(String name, Color color) {
        super(name);
        this.color = color;
    }

    @Override
    public boolean isHidden() {
        return !Varsr.debug;
    }
}
