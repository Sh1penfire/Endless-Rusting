package rusting.ctype;

import arc.Core;
import mindustry.Vars;
import rusting.Varsr;

public abstract class MappableERContent extends ERContent{

    public final String name;
    public String localizedName;

    public MappableERContent(String name){
        this.name = Vars.content.transformName(name);
        Varsr.content.handleContent(this);
    }

    @Override
    public void load() {
        super.load();
        localizedName = Core.bundle.get(getContentType().name + "." + name + ".name", name);
    }
}
