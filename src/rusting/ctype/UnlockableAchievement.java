package rusting.ctype;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.game.EventType.Trigger;
import rusting.Varsr;

public class UnlockableAchievement extends UnlockableERContent{

    public static Seq<UnlockableAchievement> achievements = Seq.with();

    public Class triggerClass = Trigger.update.getClass();
    //note: remove from Events.on when done to avoid unescecary lag, if possible put on separate thread
    public Cons runUnlock = (o) -> {

    };

    public UnlockableAchievement(String name) {
        super(name);
        achievements.add(this);
    }

    @Override
    public ERContentType getContentType() {
        return Varsr.content.getContentType("achievement");
    }
}
