package rusting.ai.types;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.UnitCommand;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;
import rusting.Varsr;
import rusting.content.RustingAISwitches;
import rusting.entities.units.CraeUnitType;
import rusting.interfaces.PulseBlockc;
import rusting.interfaces.Pulsec;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;

public class MultiSupportAI extends FlyingAI {

    @Override
    protected void init() {
        super.init();
    }

    protected Interval mineTimer = new Interval(4);
    protected Interval statTimer = new Interval(25);

    protected Vec2 movePos = new Vec2(0, 0);

    public Unit unitAlly = null;
    public Building blockAlly = null;
    boolean mining = true;
    public boolean canTargetAllies = false;
    public boolean canHealAllies = false;
    public float minRepairRange = 45;
    public float pulseAmount = 0;
    public float pulseGenRange = 0;
    public boolean canGenPulse = false;
    public Seq<Item> mineItems = Seq.with();
    Item targetItem;
    Tile ore;

    public MultiSupportAI(){
        super();
    }

    @Override
    public void unit(Unit unit) {
        super.unit(unit);
        if(unit.type instanceof CraeUnitType) {
            CraeUnitType unitType = (CraeUnitType) unit.type;
            minRepairRange = unitType.repairRange;
            pulseAmount = unitType.pulseAmount;
            pulseGenRange = unitType.pulseGenRange;
            if(pulseAmount > 0 && pulseGenRange > 0) canGenPulse = true;
            minRepairRange = Math.min(pulseGenRange, minRepairRange);
        }
        mineItems = Varsr.switches.mineItems.get(unit.type);
    }

    @Override
    public void updateMovement(){
        boolean attack = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.attackSwitch).isOn;
        boolean mine = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.mineSwitch).isOn;
        boolean healUnit = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.healUnitSwitch).isOn;
        boolean healBlock = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.healBlockSwitch).isOn;
        boolean heal = healUnit && healBlock;
        Building core = state.teams.closestCore(unit.x, unit.y, unit.team);

        boolean continueFunction = true;
        boolean move = true;

        if(!mine) unit.mineTile = null;

        Building repairPoint = (Building) targetFlag(unit.x, unit.y, BlockFlag.repair, false);
        if((unit.healthf() < 0.45f && target != null || unit.healthf() < 0.75) && (repairPoint != null || core != null)){
            Building targetBuilding = repairPoint != null ? repairPoint : core;
            if(unit.within(targetBuilding, unit.healthf() * 10 * 7)) continueFunction = true;
            else{
                moveTo(targetBuilding, unit.healthf() * 10 * 7, 25f);
                move = false;
                unit.lookAt(targetBuilding);
            }
        }

        if(continueFunction && invalid(target) && command() != UnitCommand.idle && (healUnit || healBlock)){
            boolean foundAlly = false;
            blockAlly = null;
            unitAlly = null;
            float findRange = unit.type.range;

            if(healUnit){
                Unit targetAlly = Units.closest(unit.team, unit.x, unit.y, unit.type.range * 4, u -> u.damaged() && u != this.unit);
                if(targetAlly != null){
                    movePos.set(targetAlly.x, targetAlly.y);
                    findRange += targetAlly.hitSize/2 * 1.1f;
                    foundAlly = true;
                }
                else if(!healBlock) continueFunction = true;
            }
            if(foundAlly == false){
                if(target instanceof Building && ((Building) target).team == unit.team) blockAlly = (Building) target;
                else indexer.eachBlock(unit, unit.type.range * 2,
                    other ->
                        other.damaged() ||
                        canGenPulse && other instanceof PulseBlockc && ((PulseBlockc) other).canReceivePulse(pulseAmount, (Pulsec) unit),
                    other -> {
                        if(blockAlly == null) blockAlly = other;
                    }
                );
                if(blockAlly != null) {
                    movePos.set(blockAlly.x, blockAlly.y);
                    findRange += blockAlly.block.size * 4;
                    foundAlly = true;
                }
            }

            if(!foundAlly || Mathf.dst(unit.x, unit.y, movePos.x, movePos.y) <= minRepairRange) {
                continueFunction = true;
            }
            else{
                moveTo(movePos, minRepairRange, 5f);
                unit.lookAt(movePos);
                continueFunction = false;
            }
        }
        else continueFunction = true;

        //if you can attack, then face the target and/or travel to it
        if(unit.hasWeapons() && attack && continueFunction) {
            //if not mining unit, act like a normal attack unit with enhanced ai
            if (!mine && invalid(target) && move) {

                //move to enemy spawn, but because this ai is often used on support units, stay further away
                if(command() == UnitCommand.attack && state.rules.waves && unit.team == state.rules.defaultTeam){

                    //go towards the enemy spawn
                    moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 150f);
                    //no further actions required
                    return;
                }

                //check if you can move to closest command center
                else {
                    Teamc build = targetFlag(unit.x, unit.y, BlockFlag.rally, false);
                    if (command() == UnitCommand.rally && build != null && !unit.within(build, 90)) {
                        moveTo(build, 60f);
                        //no further actions required
                        return;
                    }
                }
            }
            else if(!invalid(target)){
                //stop mining to allow for attacking with non rotating weapons
                unit.mineTile = null;

                //if the unit is fleeing to a repair point then let it stay in range
                if (move) moveTo(target, Math.max(unit.type.range / (1 + 1 * unit.healthf()), unit.hitSize / 2), 20f);
                unit.lookAt(target);

                //no further actions required
                return;
            }
        }

        if(continueFunction && mine) {
            continueFunction = moveAndMine();
        }
    }

    //mine blocks, return true if unit has done nothing
    public boolean moveAndMine(){
        boolean attack = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.attackSwitch).isOn;
        Building core = state.teams.closestCore(unit.x, unit.y, unit.team);
        boolean returnBool = false;

        if(!unit.canMine() || core == null || !invalid(target) && attack || (!unit.hasWeapons() || unit.disarmed())) {
            unit.mineTile(null);
            return true;
        }

        if (unit.mineTile != null && !unit.mineTile.within(unit, Vars.miningRange)) {
            unit.mineTile(null);
        }

        if (mining) {
            if (mineTimer.get(timerTarget2, 60 * 4) || targetItem == null) {
                if(core != null) targetItem = mineItems.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
            }

            //core full of the target item, do nothing
            if (targetItem != null && core != null && core.acceptStack(targetItem, 1, unit) == 0) {
                unit.clearItem();
                unit.mineTile(null);
                returnBool = true;
            }

            //if inventory is full, drop it off.
            if (unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))) {
                mining = false;
            } else {
                if (mineTimer.get(timerTarget, 60) && targetItem != null) {
                    ore = indexer.findClosestOre(unit, targetItem);
                }

                if (ore != null) {
                    moveTo(ore, Math.min(unit.range(), Vars.miningRange / 2f), 20f);

                    if (unit.within(ore, Vars.miningRange)) {
                        unit.mineTile = ore;
                    }

                    if (ore.block() != Blocks.air) {
                        mining = false;
                    }
                }
            }
        } else {
            unit.mineTile = null;

            if (unit.stack.amount == 0) {
                mining = true;
                returnBool = false;
            }

            if (unit.within(core, Math.min(unit.type.range, Vars.mineTransferRange))) {
                if (core != null && core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0) {
                    Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                }

                unit.clearItem();
                mining = true;
            }

            circle(core, Vars.mineTransferRange / 1.8f);
        }

        return returnBool;
    }

    @Override
    protected boolean invalid(Teamc target) {
        return !(target instanceof Building && ((Building) target).damaged()) && super.invalid(target);
    }

    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        boolean attack = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.attackSwitch).isOn;
        boolean mine = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.mineSwitch).isOn;
        boolean healBlock = Varsr.switches.getSwitchHolder(unit.type, unit.team, RustingAISwitches.healBlockSwitch).isOn;

        if(!attack && !mine && healBlock){
            return Units.findDamagedTile(unit.team, unit.x, unit.y);
        }

        Teamc t = null;
        Teamc targ1 = null;
        Unit[] closestAllly = {null};
        Units.nearby(this.unit.team, x, y, range * 6, u -> {
            if(Units.closestTarget(u.team, u.x, u.y, range * 2) != null){
                if(closestAllly[0] == null) closestAllly[0] = u;
                else if(u.damaged()) closestAllly[0] = u;
            }
        });

        Unit targetAlly = closestAllly[0];

        if (targetAlly != null)
            targ1 = Units.closestTarget(this.unit.team, targetAlly.x, targetAlly.y, range * 2, u -> u.checkTarget(air, ground));

        Teamc targ2 = Units.closestTarget(this.unit.team, x, y, range * 2, u -> u.checkTarget(air, ground), b -> ground);
        if (targ1 != null && targ2 != null) {
            if (targ1 == targ2) t = targ1;
            else{
                Vec2 pos1 = Tmp.v1.set(targ1.x(), targ1.y());
                Vec2 pos2 = Tmp.v2.set(targ2.x(), targ2.y());
                if(Mathf.dst(pos1.x, pos1.y, this.unit.x, this.unit.y) < Mathf.dst(pos2.x, pos2.y, this.unit.x, this.unit.y) * 5){
                    t = targ1;
                }
                else {
                    t = targ2;
                }
            }
        }
        else if(healBlock && indexer.findTile(unit.team, x, y, unit.type.range, b -> b.damaged()) != null){
            return indexer.findTile(unit.team, x, y, unit.type.range, b -> b.damaged());
        }
        else if(this.targetFlag(x, y, BlockFlag.core, false) == null){
            Teamc result = null;
            if(ground) result = targetFlag(x, y, BlockFlag.generator, true);
            if(result != null) return result;

            if(ground) result = this.targetFlag(x, y, BlockFlag.core, true);
            if(result != null) return result;
        }
        return t;
    }
}
