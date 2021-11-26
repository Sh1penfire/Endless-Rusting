package rusting.content;

import mindustry.ctype.ContentList;
import rusting.ctype.ResearchType;

public class RustingResearchTypes implements ContentList {

    public static ResearchType
    pulse, capsule
    ;

    @Override
    public void load() {

        pulse = new ResearchType("pulse") {{

        }};

        capsule = new ResearchType("capsule") {{

        }};

    }
}
