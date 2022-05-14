package rusting.type;

import rusting.Varsr;
import rusting.ctype.ERContentType;
import rusting.ctype.UnlockableERContent;

public class Capsule<itemStack, liquidStack> extends UnlockableERContent {

    public float itemPayloadStore;
    public float liquidPayloadStore;
    //durability of the capsule
    public int durability = 100;
    //Insulation of the capsule, used for handling hot liquids 0 means it's heat conductive, and leaks heat everywhere, 1 means that it retains all heat
    public int insulation = 0;
    //Resistance to heat. Used in game, only a stat outside of in game usage.
    public int heatResistance = 0;
    //hidden from ui
    public boolean hidden = false;

    public Capsule(String name){
        super(name);
    }

    public Capsule(String name, float itemPayloadStore, float liquidPayloadStore) {
        super(name);
        this.itemPayloadStore = itemPayloadStore;
        this.liquidPayloadStore = liquidPayloadStore;
        //just for now, till I get researching done
        alwaysUnlocked = true;
    }

    public String name(){
        return localizedName == null ? name : localizedName;
    }

    @Override
    public boolean isDisposed() {
        return super.isDisposed();
    }

    @Override
    public ERContentType getContentType() {
        return Varsr.content.getContentType("capsule");
    }
}