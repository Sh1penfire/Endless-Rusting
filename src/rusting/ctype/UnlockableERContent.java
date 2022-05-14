package rusting.ctype;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.util.Nullable;
import mindustry.Vars;

public abstract class UnlockableERContent extends MappableERContent{

    /** Localized, formal name. Never null. Set to internal name if not found in bundle. */
    public String localizedName;
    /** Localized description & details. May be null. */
    public @Nullable
    String description, details;
    /** Whether this content is always unlocked. */
    public boolean alwaysUnlocked = false;
    /** Icon of the content to use in UI. */
    public Image uiImage;
    /** Icon of the content to use in UI. */
    public TextureRegion uiIcon;
    /** Icon of the full content. Unscaled.*/
    public TextureRegion fullIcon;
    /** Unlock state. Loaded from settings. Do not modify outside of the constructor. */
    protected boolean unlocked;

    public UnlockableERContent(String name){
        super(name);
        this.localizedName = Core.bundle.get(getContentType().name + "." + this.name + ".name", this.name);
        this.description = Core.bundle.getOrNull(getContentType().name + "." + this.name + ".description");
        this.details = Core.bundle.getOrNull(getContentType().name + "." + this.name + ".details");
        this.unlocked = Core.settings != null && Core.settings.getBool(this.name + "-unlocked", alwaysUnlocked);
    }


    public void loadIcon(){
        fullIcon =
            Core.atlas.find(getContentType().name() + "-" + name + "-full",
                Core.atlas.find(name + "-full",
                    Core.atlas.find(name,
                        Core.atlas.find(getContentType().name() + "-" + name,
                            Core.atlas.find(name + "1")))));

        uiIcon = Core.atlas.find(getContentType().name() + "-" + name + "-ui", fullIcon);
        uiImage = new Image(uiIcon);
    }

    //unlock the piece of content
    public void unlock(){
        if(!unlocked){
            Core.settings.put(this.name + "-unlocked", true);
            unlocked = true;
        }
    }

    //lock the piece of content
    public void lock(){
        if(unlocked){
            Core.settings.put(this.name + "-unlocked", false);
            unlocked = false;
        }
    }

    /** @return whether this content is unlocked, or the player is in a custom (non-campaign) game. */
    public boolean unlockedNow(){
        return unlocked() || !Vars.state.isCampaign();
    }

    public boolean locked(){
        return !unlocked();
    }

    public boolean unlocked(){
        if(Vars.net != null && Vars.net.client()) return alwaysUnlocked || Vars.state.rules.researched.contains(name);
        return unlocked || alwaysUnlocked;
    }

}
