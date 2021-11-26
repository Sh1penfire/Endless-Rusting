package rusting.content;

import arc.graphics.Color;
import mindustry.ctype.ContentList;
import mindustry.type.Item;

public class RustingItems implements ContentList {
    public static Item
        melonaleum, bulastelt, taconite, halsinte, cameoShardling, camaintAmalgam, gelChip, decilita;
    ;
    @Override
    public void load() {
        melonaleum = new Item("melonaleum", Color.valueOf("#6572ca")){{
            flammability = 0.1f;
            explosiveness = 3.75f;
            radioactivity = 0.25f;
            charge = 2.25f;
            hardness = 3;
            cost = 1.35f;
        }};

        taconite = new Item("taconite", Color.valueOf("#f6cccc")){{
            hardness = 1;
            cost = 0.86f;
        }};

        bulastelt = new Item("bulastelt", Color.valueOf("#bcbcbc")){{
            hardness = 2;
            cost = 1.05f;
        }};

        halsinte = new Item("halsinte", Palr.lightstriken){{
            hardness = 0;
            cost = 1.35f;
        }};

        cameoShardling = new Item("cameo-shardling", Color.valueOf("#576561")){{
            hardness = 1;
            cost = 0.86f;
            charge = 0.35f;
            flammability = -0.15f;
        }};

        camaintAmalgam = new Item("camaint-amalgamate", Color.valueOf("#63726e")){{
            hardness = 1;
            cost = 0.86f;
            charge = 0.35f;
            flammability = -0.15f;
        }};

        gelChip = new Item("gel-chip", Palr.pulseChargeStart){{
            cost = 0.15f;
        }};

        decilita = new Item("decilita", Color.valueOf("#dab687")){{
            hardness = 2;
            cost = 0.56f;
        }};
    }
}