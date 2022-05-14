package rusting.content;


import rusting.ctype.ResearchType;

public class RustingResearchTypes {

    public static ResearchType
    pulse, capsule
    ;

    
    public static void load() {

        pulse = new ResearchType("pulse") {{

        }};

        capsule = new ResearchType("capsule") {{

        }};

    }
}
