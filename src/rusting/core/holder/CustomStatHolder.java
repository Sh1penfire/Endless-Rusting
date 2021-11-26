package rusting.core.holder;

import arc.struct.Seq;

public class CustomStatHolder {


    public static class StatHolder {

        public StatHolder(String statname){
            name = statname;
        }

        public StatHolder(String statname, String sufixname){
            name = statname;
            suffix = sufixname;
        }

        public StatHolder(){

        }

        public String name = "null";
        public String str;
        public String suffix = "empty";
        public char c;
        public int i;
        public short s;
        public float f;
        public double d;
        public boolean b;
        private boolean assignedValue = false;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public short getS() {
            return s;
        }

        public void setS(short s) {
            this.s = s;
        }

        public float getF() {
            return f;
        }

        public void setF(float f) {
            this.f = f;
        }

        public boolean hasValue(){
            return assignedValue;
        }

        public void setValue(Object o) {
            if (o.toString() instanceof String) {
                str = o.toString();
                assignedValue = true;
            }
        }

        public String getValue(){
            return str;
        }
    }

    public class StatGroup {
        public Seq<StatHolder> stats;
        public StatHolder conditionalStat;
        public boolean alwaysLoad = false;
        public String header = "defaultheader";

        public StatGroup(Seq<StatHolder> seq){
            stats = seq;
        }

        public StatGroup(Seq<StatHolder> seq, StatHolder condition){
            stats = seq;
            conditionalStat = condition;
        }

        public StatGroup(Seq<StatHolder> seq, StatHolder condition, String input){
            stats = seq;
            conditionalStat = condition;
            header = input;
        }

        public StatGroup(Seq<StatHolder> seq, Boolean load) {
            stats = seq;
            alwaysLoad = load;
        }
        public StatGroup(Seq<StatHolder> seq, Boolean load, String input){
            stats = seq;
            alwaysLoad = load;
            header = input;
        }

        public boolean canLoadStats(){
            return alwaysLoad || (conditionalStat.hasValue() && !conditionalStat.getValue().equals("false"));
        }

        public String header(){
            return header;
        }
    }

    public StatHolder pulseStorage = new StatHolder("pulsestorage"),

    //Generic stats
    resistance = new StatHolder("resistance"),
    powerLoss = new StatHolder("powerloss"),
    connectable = new StatHolder("connectable"),
    canOverload = new StatHolder("canoverload"),

    //Overload related stats
    requiresOverload = new StatHolder("requiresoverload"),
    minRequiredPercent = new StatHolder("overloadpercent", "percent"),
    overloadCapacity = new StatHolder("overloadcapacity"),
    projectileChanceModifier = new StatHolder("projectilechancemodifier"),
    projectileRange = new StatHolder("projectilerange", "blocks"),

    //Transmission related stats
    connections = new StatHolder("connections"),
    energyTransmission = new StatHolder("energytransmission"),
    siphonAmount = new StatHolder("siphonamount"),
    laserRange = new StatHolder("laserrange", "blocks"),
    pulseReloadTime = new StatHolder("pulsereloadtime"),
    pulseBursts = new StatHolder("pulsebursts"),
    pulseBurstSpacing = new StatHolder("pulseburstspacing", "seconds"),

    //utility related stats
    healPercent = new StatHolder("healpercent", "seconds"),
    overdrivePercent = new StatHolder("overdrivepercent"),
    healPercentFalloff = new StatHolder("healpercentfalloff"),

    //generation related stats
    pulseProduced = new StatHolder("pulseproduced"),
    pulseProductionInterval = new StatHolder("pulseproductioninterval", "seconds")
    ;

    public StatGroup allStats = new StatGroup(Seq.with(
            pulseStorage,
            resistance,
            powerLoss,
            connectable,
            canOverload,
            requiresOverload,
            minRequiredPercent,
            overloadCapacity,
            projectileChanceModifier,
            projectileRange,
            laserRange,
            energyTransmission,
            siphonAmount,
            pulseReloadTime,
            pulseBursts,
            pulseBurstSpacing,
            healPercent,
            healPercentFalloff,
            pulseProduced,
            pulseProductionInterval
    ), true);

    //generic stats which are affected by or affect no other stats. Excludes canOverload as overload group comes directly after generic stats
    public Seq<StatHolder> genericStats = Seq.with(
            pulseStorage,
            resistance,
            powerLoss,
            connectable,
            canOverload
    );

    //shown if block can overload
    public Seq<StatHolder> conditionalOverloadStats = Seq.with(
            requiresOverload,
            minRequiredPercent,
            overloadCapacity,
            projectileChanceModifier,
            projectileRange
    );

    //shown if the block can transmit Pulse
    public Seq<StatHolder> conditionalRangeStats = Seq.with(
            connections,
            laserRange,
            energyTransmission,
            siphonAmount,
            pulseReloadTime,
            pulseBursts,
            pulseBurstSpacing
    );

    public Seq<StatHolder> conditionalUtilityStats = Seq.with(
            healPercent,
            overdrivePercent,
            healPercentFalloff
    );

    //shown if a block has been assigned a pulse produced stat
    public Seq<StatHolder> conditionalProductionStats = Seq.with(
            pulseProduced,
            pulseProductionInterval
    );

    //all stats
    public Seq<StatGroup> loadableGroups = Seq.with(
            new StatGroup(
                    genericStats, true, "generalstats"
            ),
            new StatGroup(
                    conditionalOverloadStats, canOverload, "overloadstats"
            ),
            new StatGroup(
                    conditionalRangeStats, energyTransmission, "transmissionstats"
            ),
            new StatGroup(
                    conditionalUtilityStats, healPercent, "utilitystats"
            ),
            new StatGroup(
                    conditionalProductionStats, pulseProduced, "productionstats"
            )
    );
}
