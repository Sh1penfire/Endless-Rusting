package rusting.ctype;

import arc.Events;
import arc.func.Cons;
import arc.struct.Seq;
import mindustry.game.EventType;
import mindustry.game.EventType.SectorCaptureEvent;
import mindustry.type.*;

public class SectorBasedAchievement extends UnlockableAchievement{

    public static Seq<Sector> uncapturedSectors = Seq.with();

    Sector sector;

    public SectorBasedAchievement(String name, int sector, Planet planet) {
        super(name);
        sector %= planet.sectors.size;
        this.sector = planet.sectors.get(sector);

        if(!unlocked && !uncapturedSectors.contains(this.sector)) uncapturedSectors.add(this.sector);
    }

    @Override
    public void init() {
        super.init();
        Seq<Cons<SectorCaptureEvent>> consSeq = Seq.with();
        Cons<SectorCaptureEvent> unlockCons = new Cons<SectorCaptureEvent>() {
            @Override
            public void get(SectorCaptureEvent e) {
                if(e.sector == sector && uncapturedSectors.contains(e.sector)) {
                    unlock();
                    Events.remove(EventType.SectorCaptureEvent.class, consSeq.get(0));
                }
            }
        };
        consSeq.add(unlockCons);
        Events.on(EventType.SectorCaptureEvent.class, unlockCons);
    }

    @Override
    public void unlock() {
        super.unlock();
    }
}
