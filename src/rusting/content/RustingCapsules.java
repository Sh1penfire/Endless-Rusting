package rusting.content;


import rusting.type.Capsule;

public class RustingCapsules {

    public static Capsule
    basic, basicLiquid
    ;

    
    public static void load() {
        basic = new Capsule("basic-capsule"){{

        }};

        basicLiquid = new Capsule("liquid-capsule"){{

        }};
    }
}
