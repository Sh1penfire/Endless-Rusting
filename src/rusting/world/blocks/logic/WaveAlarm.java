package rusting.world.blocks.logic;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Block;

public class WaveAlarm extends Block {

    public static Seq<WaveAlarmBuild> alarms = Seq.with();
    public WaveAlarm(String name) {
        super(name);
        update = true;
    }

    public class WaveAlarmBuild extends Building{

        public boolean conditionalValid(){
            return true;
        }

        public void activate(){

        }

        @Override
        public void remove() {
            super.remove();
            alarms.remove(this);
        }

        @Override
        public void created() {
            super.created();
            alarms.add(this);
        }
    }
}
