package rusting.content;

import mindustry.ctype.ContentList;
import rusting.ai.AISwitches.AISwitch;

public class RustingAISwitches implements ContentList {
    public static AISwitch attackSwitch, mineSwitch, healUnitSwitch, healBlockSwitch;
    @Override
    public void load() {

        attackSwitch = new AISwitch("unit-attack"){{

        }};

        mineSwitch = new AISwitch("unit-mine"){{

        }};

        healUnitSwitch = new AISwitch("unit-heal-unit"){{

        }};

        healBlockSwitch = new AISwitch("unit-heal-block"){{

        }};
    }
}
