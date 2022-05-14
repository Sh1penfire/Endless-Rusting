package rusting.content;

import rusting.ai.AISwitches.AISwitch;

public class RustingAISwitches {
    public static AISwitch attackSwitch, mineSwitch, healUnitSwitch, healBlockSwitch;
    
    public static void load() {

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
