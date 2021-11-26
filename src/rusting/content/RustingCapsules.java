package rusting.content;

import mindustry.ctype.ContentList;
import rusting.type.Capsule;

public class RustingCapsules implements ContentList {

    public static Capsule
    basic, basicLiquid
    ;

    @Override
    public void load() {
        basic = new Capsule("basic-capsule"){{

        }};

        basicLiquid = new Capsule("liquid-capsule"){{

        }};
    }
}
