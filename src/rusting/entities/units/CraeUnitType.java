package rusting.entities.units;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import rusting.content.Palr;
import rusting.content.RustingStatusEffects;
import rusting.entities.units.flying.CraeUnitEntity;
import rusting.interfaces.Pulsec;

import static mindustry.Vars.state;

public class CraeUnitType extends BaseUnitType {

    public float pulseCapacity = 0;
    public float overloadCapacity = 0;
    public float overloadedOpacity = 0.3f;
    public float repairRange = 0;
    public float pulseAmount = 0;
    public float pulseGenRange = 0;

    public int projectileDeathSpawnInterval = 10;

    public Color chargeColourStart, chargeColourEnd;

    public TextureRegion pulseRegion, shakeRegion;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void load() {
        super.load();
        pulseRegion = Core.atlas.find(name + "-pulse");
        if(!Core.atlas.isFound(pulseRegion)) pulseRegion = region;
        shakeRegion = Core.atlas.find(name + "-shake");
        if(!Core.atlas.isFound(shakeRegion)) shakeRegion = pulseRegion;
    }

    @Override
    public void display(Unit unit, Table table){

        try {
            CraeUnitEntity barGuillotine = (CraeUnitEntity) unit;
        }
        catch (ClassCastException err) {
            try {
                Tmp.v1.set(unit.x, unit.y);
                unit.kill();
                CraeUnitEntity unitReplacement = (CraeUnitEntity) unit.type.create(unit.team);
                unitReplacement.x = Tmp.v1.x;
                unitReplacement.y = Tmp.v1.y;
            } catch (Error error) {
                Log.err(error);
            }
            return;
        }

        table.table(t -> {
            t.left();
            t.add(new Image(uiIcon)).size(8 * 4).scaling(Scaling.fit);
            t.labelWrap(localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> {

            bars.defaults().growX().height(20f).pad(4);

            bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
            bars.row();

            Pulsec craeUnit = ((Pulsec) unit);
            bars.add(new Bar(Core.bundle.get("pulsestat.pulse"), Palr.pulseChargeEnd, craeUnit::chargef).blink(Color.cyan));
            bars.row();

            if(state.rules.unitAmmo){
                bars.add(new Bar(ammoType.icon() + " " + Core.bundle.get("stat.ammo"), ammoType.barColor(), () -> unit.ammo / ammoCapacity));
                bars.row();
            }

            for(Ability ability : unit.abilities){
                ability.displayBars(unit, bars);
            }

            if(unit instanceof Payloadc){
                Payloadc payload = (Payloadc) unit;
                bars.add(new Bar("stat.payloadcapacity", Pal.items, () -> payload.payloadUsed() / unit.type().payloadCapacity));
                bars.row();

                float[] count = new float[]{-1};
                bars.table().update(t -> {
                    if(count[0] != payload.payloadUsed()){
                        payload.contentInfo(t, 8 * 2, 270);
                        count[0] = payload.payloadUsed();
                    }
                }).growX().left().height(0f).pad(0f);
            }
        }).growX();

        if(unit.controller() instanceof LogicAI){
            table.row();
            table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().wrap().left();
            table.row();
            table.label(() -> Iconc.settings + " " + (long)unit.flag + "").color(Color.lightGray).growX().wrap().left();
        }

        table.row();
    }

    public CraeUnitType(String name) {
        super(name);
        chargeColourStart = Palr.pulseChargeStart;
        chargeColourEnd = Palr.pulseChargeStart;
        immunities.add(RustingStatusEffects.macrosis);
        immunities.add(RustingStatusEffects.macotagus);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }

}
