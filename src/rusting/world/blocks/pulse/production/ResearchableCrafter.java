package rusting.world.blocks.pulse.production;

import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import rusting.Varsr;
import rusting.ctype.ResearchType;
import rusting.interfaces.ResearchableObject;
import rusting.world.modules.ResearchModule;

import static mindustry.Vars.player;

public class ResearchableCrafter extends GenericCrafter implements ResearchableObject {
    //research types for the block
    public Seq<ResearchType> researchTypes = new Seq<ResearchType>();
    //research module with more specific information
    public ResearchModule researchModule;

    public ResearchableCrafter(String name) {
        super(name);
        researchTypes.clear();
    }

    @Override
    public String name() {
        return name;
    }

    //use for ui
    @Override
    public String localizedName() {
        return localizedName;
    }

    @Override
    public TextureRegion researchUIcon() {
        return uiIcon;
    }

    @Override
    public Seq<ResearchType> researchTypes() {
        return researchTypes;
    }

    @Override
    public ResearchModule getResearchModule() {
        if (researchModule == null) researchModule = new ResearchModule(ItemStack.with(), this);
        return researchModule;
    }

    @Override
    public boolean isHidden() {
        return !Varsr.research.researched(player.team(), this, researchTypes) || super.isHidden();
    }

    @Override
    public boolean hidden() {
        return getResearchModule().isHidden || !unlocked && Vars.state.isCampaign();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team) {
        //must have been researched, but for now checks if research center exists
        if (tile == null || !Varsr.research.researched(player.team(), this, researchTypes)) return false;
        return super.canPlaceOn(tile, team);
    }
}
