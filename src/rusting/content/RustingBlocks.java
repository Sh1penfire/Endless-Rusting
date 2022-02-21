package rusting.content;

import arc.graphics.Color;
import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.*;
import rusting.core.holder.PanelHolder;
import rusting.core.holder.ShootingPanelHolder;
import rusting.entities.bullet.BounceBulletType;
import rusting.entities.bullet.BulletSpawnBulletType;
import rusting.world.blocks.OverrideColourStaticWall;
import rusting.world.blocks.PlayerCore;
import rusting.world.blocks.capsules.CapsuleBlockResearchCenter;
import rusting.world.blocks.defense.ProjectileAttackWall;
import rusting.world.blocks.defense.turret.*;
import rusting.world.blocks.defense.turret.healer.*;
import rusting.world.blocks.defense.turret.power.LightningTurret;
import rusting.world.blocks.defense.turret.power.PanelTurret;
import rusting.world.blocks.environment.*;
import rusting.world.blocks.logic.UnbreakableMessageBlock;
import rusting.world.blocks.power.AttributeBurnerGenerator;
import rusting.world.blocks.production.ConditionalDrill;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.PulseBoulder;
import rusting.world.blocks.pulse.crafting.PulseCondensary;
import rusting.world.blocks.pulse.crafting.PulseGenericCrafter;
import rusting.world.blocks.pulse.defense.*;
import rusting.world.blocks.pulse.distribution.*;
import rusting.world.blocks.pulse.production.*;
import rusting.world.blocks.pulse.unit.*;
import rusting.world.blocks.pulse.utility.*;
import rusting.world.draw.*;

import static mindustry.type.ItemStack.with;

public class RustingBlocks implements ContentList{

    public static Block
        capsuleCenterTest,
        //environment
        //liquids
        melainLiquae, coroLiquae, impurenBurneLiquae, impurenBurneLiquaeDeep, classemLiquae,
        //sunken metal floor
        sunkenMetalFloor, sunkenMetalFloor2, sunkenMetalFloor3, sunkenBasalt, sunkenHotrock, sunkenMagmarock,
        //floor
        //frae plating
        fraePlating, fraePlating2, fraePlating3, fraePlating4, fraePlating5, fraeAgedMetal, fraePulseCapedWall,
        //mhem plating
        mhemPlating, mhemPlating3, mhemPlating4, mhemPlating5, mhemAgedFraeBlock, mhemAgedMetal,
        //damaged frae plating
        damagedFraePlating, damagedFraePlating2,
        //pailean
        paileanStolnen, paileanSanden, paileanPathen, paileanWallen, paileanBarreren,
        //ebrin, drier pailean blocks
        ebrinDrylon,
        //saline, slaty blocks
        salineStolnene, salineBarreren,
        //classem
        classemStolnene, classemSanden, classemPathen, classemPulsen, classemWallen, classemBarrreren,
        classemTree, classemTreeDead, classemTreeCrystalline, melonaleumGeodeSmall, melonaleumGeodeLarge,
        //impuren
        impurenSanden,
        //moisten
        moistenStolnene, moistenLushen, moistenWallen, moistenVinen,
        //dripive
        dripiveGrassen, dripiveWallen,
        //volen, drier variants of normal stone, could be used for warmer looking maps. Not resprited stone floors, I promise
        volenStolnene, volenWallen,
        //ore blocks
        melonaleum, taconite, cameoShardling,
        //crafting
        bulasteltForgery, desalinationMixer, cameoCrystallisingBasin, cameoPaintMixer, camaintAmalgamator, cameoHarvestingBasin,
        //defense
        terraMound, terraMoundLarge, hailsiteBarrier, hailsiteBarrierLarge, decilitaCyst, decilitaCystLarge, wol,
        //power
        waterBoilerGenerator,
        //drill
        terraPulveriser,
        //distribution
        terraConveyor,
        //pulse
        //Natural Sources
        melonaleumGeode, largeMelonaleumGeode, hugeMelonaleumGeode, giganticMelonaleumGeode, humungousMelonaleumGeond, amogusMelonaledumDio,
        //Pulse collection
        pulseInceptionPoint, pulseCollector, pulseGenerator, infectedsGeneratorCore,
        //Nodes
        pulseNode, pulseTesla,
        //Storage
        pulseResonator,
        //Siphon
        pulseSiphon,
        //crafting
        pulseGraphiteForge, pulseCondensary, pulseMelomaeMixer, pulseGelPress,
        //Walls
        pulseBarrier, pulseBarrierLarge,
        //Research
        pulseResearchCenter,
        //Suport
        pulseUpkeeper,
        //teleporter multiblock structure
        pulseTeleporterController, pulseFlowSplitter, pulseCanal, pulseInputTerminal, pulseCanalTunnel,
        //particle spawning
        smallParticleSpawner,
        //storage
        fraeCore, craeCore,
        //endregion storage
        //turrets
        //environment/turrets
        archangel, contingent, pulseMotar, pulseFieldGen,
        //landmines
        pulseLandmine,
        //units
        pulseCommandCenter, pulseFactory, enlightenmentReconstructor, ascendanceReconstructor, pulseDistributor,
        //controll
        pulseDirectionalController, pulseContactSender,
        //sandbox
        pulseSource, pulseVoid,
        //frae
        fraeResarchCenter,
        //healer turrets
        thrum, spikent, conserve,
        //flamethrower
        kindle, plasmae, photosphere,
        //harpoons
        pilink, tether,
        //pannel turrets
        prikend, prsimdeome, prefraecon, rangi, pafleaver,
        //drylon
        spraien, glare,
        //platonic elements represented by four turrets.
        octain, triagon, cuin, icosahen,
        //other elemental turrets, wiht names relating to gemstones
        pulver,
        //turrets relating almost directly to Pixelcraft with their name but change things up a bit. Classified under elemental in the turret's sprite folder
        horaNoctis, holocaust,
        //bomerang related turrets
        refract, diffract, reflect,
        //region unit
        hotSpringSprayer, coldSpringSprayer, fraeFactory, antiquaeGuardianBuilder, absentReconstructor, dwindlingReconstructor,
        //logic
        halsinteLamp, gelBasin, mhemLog, raehLog, fraeLog;

    public static void addLiquidAmmo(Block turret, Liquid liquid, BulletType bullet){
        ((LiquidTurret) turret).ammoTypes.put(liquid, bullet);
    }

    public static void addLiquidAmmoes(Block turret, ObjectMap<Liquid, BulletType> map){
        map.each((liquid, bullet) -> {
            ((LiquidTurret) turret).ammoTypes.put(liquid, bullet);
        });
    }

    public void load(){
        //region environment

        melainLiquae = new Floor("melain-liquae"){{
            speedMultiplier = 0.5f;
            variants = 0;
            status = RustingStatusEffects.macotagus;
            statusDuration = 350f;
            liquidDrop = RustingLiquids.melomae;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
            drawLiquidLight = true;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.15f);
            lightRadius = 16;
        }};

        coroLiquae = new DamagingFloor("coro-liquae"){{
            speedMultiplier = 0.86f;
            variants = 0;
            status = StatusEffects.corroded;
            statusDuration = 1250;
            liquidDrop = RustingLiquids.cameaint;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.35f;
        }};

        impurenBurneLiquae = new DamagingFloor("impuren-burnen-liquae"){{
            speedMultiplier = 1.34f;
            variants = 0;
            status = StatusEffects.burning;
            statusDuration = 15;
            liquidDrop = null;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.65f;
            damage = 0.055f;
        }};

        impurenBurneLiquaeDeep = new DamagingFloor("impuren-burnen-liquae-deep"){{
            speedMultiplier = 1.34f;
            variants = 0;
            status = StatusEffects.burning;
            statusDuration = 15;
            liquidDrop = null;
            isLiquid = true;
            drownTime = 350;
            cacheLayer = CacheLayer.water;
            albedo = 0.65f;
            damage = 0.075f;
        }};

        classemLiquae = new Floor("classem-liquae"){{
            speedMultiplier = 1.16f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 1250;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.25f;
        }};

        sunkenMetalFloor = new Floor("sunken-metal-floor"){{
            speedMultiplier = 0.85f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
        }};

        sunkenMetalFloor2 = new Floor("sunken-metal-floor2"){{
            speedMultiplier = 0.85f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
        }};

        sunkenMetalFloor3 = new Floor("sunken-metal-floor3"){{
            speedMultiplier = 0.85f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
        }};

        sunkenBasalt = new Floor("sunken-basalt"){{
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
        }};

        sunkenHotrock = new EffectFloor("sunken-hotrock"){{
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;

            attributes.set(Attribute.heat, 0.15f);
        }};

        sunkenMagmarock = new EffectFloor("sunken-magmarock"){{
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;

            attributes.set(Attribute.heat, 0.35f);
        }};

        fraePlating = new Floor("frae-aged-plating-horizontalin"){{
            variants = 0;
        }};

        damagedFraePlating = new Floor("frae-damaged-aged-plating-horizontal"){{
            variants = 2;
        }};

        fraePlating2 = new Floor("frae-aged-plating-verticalin"){{
            variants = 0;
            blendGroup = fraePlating;
        }};

        damagedFraePlating2 = new Floor("frae-damaged-aged-plating-verticinaeium") {{
            variants = 0;
            blendGroup = damagedFraePlating;
        }};

        fraePlating3 = new Floor("frae-aged-plating3"){{
            variants = 0;
            blendGroup = fraePlating;
        }};

        fraePlating4 = new Floor("frae-aged-plating4"){{
            variants = 0;
            blendGroup = fraePlating;
        }};

        fraePlating5 = new Floor("frae-aged-plating5"){{
            variants = 0;
            blendGroup = fraePlating;
        }};

        fraeAgedMetal = new StaticWall("frae-aged-metal-block"){{
            variants = 2;
        }};

        mhemPlating = new Floor("mhem-aged-plating"){{
            variants = 0;
        }};

        mhemPlating3 = new Floor("mhem-aged-plating3"){{
            variants = 0;
            blendGroup = mhemPlating;
        }};

        mhemPlating4 = new Floor("mhem-aged-plating4"){{
            variants = 0;
            blendGroup = mhemPlating;
        }};

        mhemPlating5 = new Floor("mhem-aged-plating5"){{
            variants = 0;
            blendGroup = mhemPlating;
        }};

        mhemAgedFraeBlock = new StaticWall("mhem-aged-frae-block"){{
            variants = 1;
        }};

        mhemAgedMetal = new StaticWall("mhem-aged-metal-block"){{
            variants = 3;
        }};

        paileanStolnen = new Floor("pailean-stolnen"){{
            speedMultiplier = 0.95f;
            variants = 3;
            attributes.set(Attribute.water, -0.85f);
            wall = paileanWallen;
        }};

        paileanSanden = new Floor("pailean-sanden"){{
            speedMultiplier = 0.75f;
            variants = 3;
            wall = paileanWallen;
        }};

        paileanPathen = new Floor("pailean-pathen"){{
            speedMultiplier = 0.8f;
            variants = 2;
            attributes.set(Attribute.water, -0.85f);
            attributes.set(Attribute.heat, 0.075f);
            blendGroup = paileanStolnen;
            wall = paileanWallen;
        }};

        ebrinDrylon = new Floor("ebrin-drylon"){{
            itemDrop = Items.sand;
            speedMultiplier = 0.75f;
            variants = 6;
            attributes.set(Attribute.water, -1f);
            attributes.set(Attribute.spores, -0.15f);
            attributes.set(Attribute.heat, 0.025f);
            wall = paileanBarreren;
        }};

        salineStolnene = new Floor("saline-stolnene"){{
            speedMultiplier = 0.85f;
            variants = 3;
            status = RustingStatusEffects.hailsalilty;
            attributes.set(Attribute.water, -1f);
            attributes.set(Attribute.spores, -0.35f);
            wall = salineBarreren;
        }};

        classemStolnene = new Floor("classem-stolnene"){{
            speedMultiplier = 0.85f;
            variants = 3;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.05f);
            lightRadius = 10;
            attributes.set(Attribute.water, 0.15f);
            attributes.set(Attribute.heat, -0.15f);
            wall = classemBarrreren;
        }};

        classemSanden = new Floor("classem-sanden"){{
            speedMultiplier = 0.85f;
            variants = 3;
            itemDrop = Items.sand;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.05f);
            lightRadius = 10;
            attributes.set(Attribute.water, 0.125f);
            attributes.set(Attribute.heat, -0.05f);
            wall = classemBarrreren;
        }};

        classemPulsen = new Floor("classem-pulsen"){{
            speedMultiplier = 0.85f;
            variants = 6;
            status = RustingStatusEffects.fuesin;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.09f);
            lightRadius = 25;
            attributes.set(Attribute.water, 0.75f);
            attributes.set(Attribute.heat, -0.55f);
            attributes.set(Attribute.spores, -0.15f);
            wall = classemPulsen;
        }};

        classemPathen = new Floor("classem-pathen"){{
            speedMultiplier = 0.85f;
            variants = 2;
            status = RustingStatusEffects.macrosis;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.12f);
            lightRadius = 25;
            attributes.set(Attribute.water, 1.35f);
            attributes.set(Attribute.heat, -0.35f);
            wall = classemBarrreren;
        }};

        impurenSanden = new DamagingFloor("impuren-sanden"){{
            speedMultiplier = 1.15f;
            variants = 3;
            itemDrop = Items.sand;
            status = StatusEffects.burning;
            attributes.set(Attribute.heat, 0.15f);
            wall = volenWallen;
            damage = 0.005f;
        }};

        moistenStolnene = new Floor("moisten-stolnene"){{
            variants = 6;
            status = StatusEffects.muddy;
            attributes.set(Attribute.water, 0.25f);
            wall = moistenWallen;
        }};

        moistenLushen = new Floor("moisten-lushen"){{
            variants = 4;
            attributes.set(Attribute.water, 0.35f);
            wall = moistenVinen;
        }};

        dripiveGrassen = new Floor("dripive-grassen"){{
            speedMultiplier = 0.95f;
            attributes.set(Attribute.water, 1.14f);
            attributes.set(Attribute.spores, -0.25f);
            attributes.set(Attribute.heat, -0.35f);
            wall = dripiveWallen;
        }};

        volenStolnene = new Floor("volen-stolnene"){{
            variants = 3;
            wall = volenStolnene;
        }};

        paileanWallen = new StaticWall("pailean-wallen"){{
            variants = 2;
        }};

        paileanBarreren = new StaticWall("pailean-barreren"){{
            variants = 2;
        }};

        salineBarreren = new StaticWall("saline-barreren"){{
            variants = 3;
        }};

        classemWallen = new StaticWall("classem-wallen"){{
            variants = 3;
        }};

        classemBarrreren = new OverrideColourStaticWall("classem-barreren"){{
            variants = 5;
            overrideMapColour = Color.valueOf("#2c2f3b");
        }};

        classemTree = new TreeBlock("classem-tree"){{

        }};

        moistenWallen = new OverrideColourStaticWall("moisten-wallen"){{
            variants = 3;
            overrideMapColour = Color.valueOf("#716765");
        }};

        moistenVinen = new StaticWall("moisten-vinen"){{
            variants = 2;
        }};

        dripiveWallen = new StaticWall("dripive-wallen"){{
            variants = 2;
        }};

        volenWallen = new StaticWall("volen-wallen"){{
            variants = 2;
        }};

        melonaleum = new FixedOreBlock("melonaleum"){{
            itemDrop = RustingItems.melonaleum;
            overrideMapColor = itemDrop.color;
            variants = 2;
        }};

        taconite = new FixedOreBlock("taconite"){{
            itemDrop = RustingItems.taconite;
            overrideMapColor = itemDrop.color;
            variants = 3;
        }};

        cameoShardling = new FixedOreBlock("cameo-shardling"){{
            itemDrop = RustingItems.cameoShardling;
            overrideMapColor = itemDrop.color;
            variants = 3;
        }};

        //endregion

        fraePulseCapedWall = new PulseBlock("pulse-capped-frae-wall"){{
            requirements(Category.defense, with(Items.titanium, 35, RustingItems.bulastelt, 15, RustingItems.cameoShardling, 25));
            buildVisibility = BuildVisibility.editorOnly;
            researchTypes.add(RustingResearchTypes.capsule);
        }};

        capsuleCenterTest = new CapsuleBlockResearchCenter("etst"){{
            requirements(Category.effect, with());
        }};

        //region crafting
        bulasteltForgery = new GenericCrafter("bulastelt-forgery"){{
            requirements(Category.crafting, with(Items.lead, 35, Items.coal, 25, RustingItems.taconite, 65));
            craftEffect = Fx.smeltsmoke;
            outputItem = new ItemStack(RustingItems.bulastelt, 6);
            craftTime = 425f;
            size = 2;
            hasPower = false;
            hasLiquids = true;

            drawer = new DrawLiquidSmelter();

            consumes.items(with(Items.coal, 3, RustingItems.taconite, 5));
            consumes.liquid(Liquids.water, 0.155f);
        }};

        desalinationMixer = new GenericCrafter("desalination-mixer"){{
            requirements(Category.crafting, with(Items.lead, 65, Items.graphite, 15, Items.silicon, 35, Items.sand, 85));
            craftEffect = Fxr.salty;
            outputItem = new ItemStack(RustingItems.halsinte, 3);
            craftTime = 125;
            size = 2;
            hasPower = true;
            hasLiquids = true;

            drawer = new DrawItemLiquid();

            consumes.power(7.2f);
            consumes.liquid(Liquids.water, 0.1235f);
        }};

        cameoPaintMixer = new ResearchableCrafter("cameo-paint-mixer"){{
            requirements(Category.crafting, with(Items.lead, 145, Items.graphite, 75, Items.titanium, 45, RustingItems.bulastelt, 65));
            centerResearchRequirements(true, ItemStack.with(Items.silicon, 355, RustingItems.halsinte, 135, RustingItems.bulastelt, 65));
            researchTypes.add(RustingResearchTypes.capsule);
            buildVisibility = BuildVisibility.hidden;
            craftEffect = Fx.none;
            outputLiquid = new LiquidStack(RustingLiquids.cameaint, 7.5f);
            craftTime = 50;
            size = 3;

            drawer = new DrawRotorTop();
            consumes.power(0.72f);
            consumes.items(with(Items.lead, 1, Items.copper, 3, RustingItems.halsinte, 2));
            consumes.liquid(Liquids.water, 0.25f);
        }};


        cameoCrystallisingBasin = new ResearchableCrafter("cameo-crystallising-basin"){{
            requirements(Category.crafting, with(Items.lead, 65, Items.graphite, 85, Items.silicon, 145, RustingItems.bulastelt, 55));
            centerResearchRequirements(true, ItemStack.with(Items.silicon, 355, RustingItems.halsinte, 135, RustingItems.bulastelt, 65));
            researchTypes.add(RustingResearchTypes.capsule);
            craftEffect = Fx.none;
            outputItem = new ItemStack(RustingItems.cameoShardling, 6);
            craftTime = 325;
            size = 4;
            hasPower = true;
            hasLiquids = true;

            drawer = new DrawItemLiquid();

            consumes.power(7.2f);
            consumes.liquid(RustingLiquids.cameaint, 0.447f);
        }};

        camaintAmalgamator = new ResearchableCrafter( "camaint-amalgamator"){{
            requirements(Category.crafting, with(RustingItems.taconite, 95, RustingItems.bulastelt, 75, RustingItems.cameoShardling, 55));
            centerResearchRequirements(true, ItemStack.with(Items.silicon, 650, RustingItems.gelChip, 150, RustingItems.bulastelt, 165, RustingItems.cameoShardling, 150));
            researchTypes.add(RustingResearchTypes.capsule);
            craftEffect = Fx.none;
            outputItem = new ItemStack(RustingItems.camaintAmalgam, 8);
            craftTime = 150;
            size = 2;
            hasPower = true;
            hasLiquids = true;

            consumes.power(3.25f);
            consumes.liquid(RustingLiquids.cameaint, 0.635f);
            consumes.items(with(Items.titanium, 3, RustingItems.bulastelt, 4, RustingItems.cameoShardling, 2));
        }};

        cameoHarvestingBasin = new GenericCrafter("cameo-harvesting-basin"){{
            requirements(Category.crafting, with(RustingItems.taconite, 95, RustingItems.bulastelt, 75, RustingItems.cameoShardling, 55));
            //centerResearchRequirements(false, ItemStack.with(Items.silicon, 650, RustingItems.gelChip, 150, RustingItems.bulastelt, 165, RustingItems.cameoShardling, 150));
            craftEffect = Fx.none;
            craftTime = 950;
            size = 3;
            hasPower = true;
            hasLiquids = true;

            consumes.power(3.25f);
            consumes.liquid(RustingLiquids.cameaint, 0.0635f);
            consumes.items(with(Items.titanium, 3, RustingItems.bulastelt, 4, RustingItems.cameoShardling, 2));
        }};

        //endregion crafting

        //region defense
        terraMound = new Wall("terra-mound"){{
            requirements(Category.defense, with(Items.coal, 6, RustingItems.taconite, 3, RustingItems.bulastelt, 1));
            size = 1;
            health = 420 * size * size;
            insulated = true;
        }};

        terraMoundLarge = new Wall("terra-mound-large"){{
            requirements(Category.defense, with(Items.coal, 24, RustingItems.taconite, 12, RustingItems.bulastelt, 4));
            size = 2;
            health = 420 * size * size;
            insulated = true;
        }};

        hailsiteBarrier = new ProjectileAttackWall("hailsite-barrier"){{
            requirements(Category.defense, with(RustingItems.taconite, 3, RustingItems.halsinte, 7));
            size = 1;
            health = 355 * size * size;
            variants = 2;
            absorbLasers = true;
        }};

        hailsiteBarrierLarge = new ProjectileAttackWall("hailsite-barrier-large"){{
            requirements(Category.defense, with(RustingItems.taconite, 12, RustingItems.halsinte, 28));
            size = 2;
            health = 355 * size * size;
            variants = 2;
            absorbLasers = true;
            deathProjectiles = 23;
        }};

        decilitaCyst = new ProjectileAttackWall("decilita-cyst"){{
            requirements(Category.defense, with(RustingItems.decilita, 4, RustingItems.bulastelt, 6));
            size = 1;
            health = 150 * size * size;
            variants = 2;
            deathProjectiles = 3;
            bullet = deathBullet = RustingBullets.fossilShard;
        }};

        decilitaCystLarge = new ProjectileAttackWall("decilita-cyst-large"){{
            requirements(Category.defense, with(RustingItems.decilita, 16, RustingItems.bulastelt, 24));
            size = 2;
            health = 150 * size * size;
            deathProjectiles = 12;
            bullet = deathBullet = RustingBullets.fossilShard;
        }};

        wol = new Wall("wol"){{
            requirements(Category.defense, with(Items.coal, 24, RustingItems.taconite, 12, RustingItems.bulastelt, 4));
            size = 1;
            health = 420 * size * size;
        }};

        //endregion defense

        //region power

        waterBoilerGenerator = new AttributeBurnerGenerator("water-boiler-generator"){{
            requirements(Category.power, with(Items.copper, 40, Items.graphite, 35, Items.lead, 50, Items.silicon, 35, Items.metaglass, 40));
            powerProduction = 3.25f;
            generateEffect = Fx.redgeneratespark;
            size = 3;
            minItemEfficiency = 0.15f;

            consumes.liquid(Liquids.water, 0.12f).optional(false, false);
        }};

        //endregion

        //region drill

        terraPulveriser = new ConditionalDrill("terra-pulveriser"){{
            requirements(Category.production, with(Items.copper, 25, Items.coal, 15, RustingItems.taconite, 15));
            size = 2;
            tier = 2;
            drops = Seq.with(
                new ItemDrop(){{
                    item = RustingItems.taconite;
                    floors = Seq.with(Blocks.stone.asFloor(), Blocks.craters.asFloor(), Blocks.basalt.asFloor());
                }},
                new ItemDrop(){{
                    item = Items.sand;
                    floors = Seq.with(Blocks.sand.asFloor(), Blocks.darksand.asFloor(), Blocks.sandWater.asFloor(), Blocks.darksandWater.asFloor());
                }},
                new ItemDrop(){{
                    item = Items.coal;
                    floors = Seq.with(Blocks.charr.asFloor());
                }},
                new ItemDrop(){{
                    item = RustingItems.halsinte;
                    floors = Seq.with(Blocks.salt.asFloor(), salineStolnene.asFloor());
                }}
            );

            consumes.liquid(Liquids.water, 0.05f).boost();

        }};

        //endregion

        //region distribution

        terraConveyor = new Conveyor("terra-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 2, Items.coal, 1, RustingItems.bulastelt, 2));
            health = 95;
            speed = 0.04f;
            displayedSpeed = 5.5f;
            floating = true;
        }};

        //endregion distribution

        //region pulse

        //a small geode containing a lot of Pulse. Destroyable for it's Melonaleum crystals (the less Pulse is siphoned the more crystals drop on the floor for collection) but the more volatile the Geode is
        melonaleumGeode = new PulseBoulder("melonaleum-geode"){{
            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;

            health = 250;
            pulseStorage = 650;
            selfDamage = 0.25f;
            pulseProduction = 0.075f;

            passiveEffect = Fxr.craeWeaversResidue;
            effectChance = 0.08f;
            hideFromUI();
        }};

        //region collection

        pulseInceptionPoint = new PulseSapper("pulse-inception-point") {{
            requirements(Category.power, with(Items.lead, 10, Items.titanium, 5, Items.graphite, 4));
            centerResearchRequirements(false, with());

            health = 250;
            collectSpeed = 0.1f;
            pulseStorage = 15;
            pulsePressure = 10;
        }};

        //Collects pulse. Requires some sort of Siphon to collect the pulse.
        pulseCollector = new PulseGenerator("pulse-collector"){{
            requirements(Category.power, with(Items.copper, 35, Items.coal, 15, Items.titanium, 10));
            centerResearchRequirements(false, with());
            size = 1;
            canOverload = false;
            configurable = false;
            productionTime = 50;
            pulseAmount = 7.5f;
            connectionsPotential = 0;
            connectable = false;
            pulseStorage = 55;
            resistance = 0.75f;
            laserOffset = 4;
        }};

        //Rapidly collects pulse. Quite good at storing pulse. Needs Pulse to kickstart the process.
        pulseGenerator = new PulseGenerator("pulse-generator"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 55, Items.titanium, 45));
            centerResearchRequirements(true, with(Items.copper, 350,  Items.coal, 125, Items.graphite, 95, Items.titanium, 225, RustingItems.melonaleum, 85));
            consumes.item(RustingItems.melonaleum, 1);
            size = 3;
            canOverload = true;
            overloadCapacity = 125;
            productionTime = 30;
            pulseAmount = 43.5f;
            pulseReloadTime = 15;
            energyTransmission = 8.5f;
            connectionsPotential = 3;
            pulseStorage = 275;
            resistance = 0.25f;
            laserOffset = 10;
            laserRange = 7;
            minRequiredPulsePercent = 0.35f;
        }};

        infectedsGeneratorCore = new InfectedsGeneratorCore("infecteds-generator-core"){{
            requirements(Category.power, with(Items.lead, 450, Items.titanium, 650, Items.metaglass, 350, RustingItems.melonaleum, 350, RustingItems.gelChip, 540));
            centerResearchRequirements(true, with(Items.titanium, 1500, Items.metaglass, 2340, RustingItems.gelChip, 950, RustingItems.melonaleum, 1250));
            consumes.item(RustingItems.melonaleum, 3);
            size = 6;
            canOverload = true;
            overloadCapacity = 1250;
            productionTime = 10;
            pulseAmount = 115.5f;
            pulseReloadTime = 5;
            energyTransmission = 45.5f;
            connectionsPotential = 7;
            pulseStorage = 2350;
            resistance = 0.25f;
            laserOffset = 10;
            laserRange = 55;
            minRequiredPulsePercent = 0.35f;
        }};

        //endregion collection

        //Loses power fast, but is great at transmitting pulses to far blocks.

        pulseCanal = new PulseCanal("pulse-canal"){{
            requirements(Category.power, with(Items.lead, 2, Items.titanium, 1));
            centerResearchRequirements(false, with());
            pulseStorage = 25;
        }};

        pulseFlowSplitter = new PulseFlowSplitter("pulse-flow-splitter"){{
            requirements(Category.power, with(Items.lead, 4, Items.titanium, 3));
            centerResearchRequirements(false, with());
            pulseStorage = 65;
        }};

        pulseCanalTunnel = new PulseCanalTunnel("pulse-tunnel-dock"){{
            hideFromUI();
        }};

        pulseNode = new PulseNode("pulse-node"){{
            requirements(Category.power, with(Items.copper, 5, Items.lead, 4, Items.titanium, 3));
            centerResearchRequirements(true, with(Items.copper, 120, Items.lead, 95, Items.titanium, 65));
            size = 1;
            powerLoss = 0.0025f;
            pulseReloadTime = 15;
            energyTransmission = 25f;
            pulseStorage = 65;
            resistance = 0.075f;
            laserRange = 13;
            canOverload = false;
        }};

        //Shoots lightning around itself when overloaded. Easly overloads. Acts as a large power node, with two connections, but slower reload
        pulseTesla = new PulseNode("pulse-tesla"){{
            requirements(Category.power, with(Items.copper, 65, Items.lead, 45, Items.graphite, 25, Items.titanium, 20));
            centerResearchRequirements(true, with(Items.copper, 365, Items.lead, 175, Items.coal, 155, Items.titanium, 80));
            size = 2;
            projectile = RustingBullets.craeBolt;
            projectileChanceModifier = 0.15f;
            powerLoss = 0.00835f;
            pulseReloadTime = 35;
            minRequiredPulsePercent = 0.15f;
            connectionsPotential = 2;
            energyTransmission = 35f;
            pulseStorage = 95;
            overloadCapacity = 15;
            resistance = 0.075f;
            laserOffset = 3;
            laserRange = 18;
            canOverload = true;
        }};

        //stores power for later usage less effectively than nodes, but stores more power. Transmits power to blocks nearby with less pulse power percentage.
        pulseResonator = new ConductivePulseBlock("pulse-resonator"){{
            requirements(Category.power, with(Items.copper, 35, Items.silicon, 20, Items.titanium, 10));
            centerResearchRequirements(false, with());
            health = 350;
            size = 1;
            powerLoss = 0.00425f;
            resistance = 0;
            pulseStorage = 350;
            energyTransmission = 2.5f;
            canOverload = false;
        }};

        pulseSiphon = new PulseSiphon("pulse-siphon"){{
            requirements(Category.power, with(Items.copper, 10, Items.graphite, 20, Items.titanium, 15));
            centerResearchRequirements(false, with());
            size = 1;
            powerLoss = 0.000035f;
            siphonAmount = 5;
            energyTransmission = 11f;
            pulseReloadTime = 55;
            pulseStorage = 35;
            laserRange = 6;
            canOverload = false;
        }};

        pulseGraphiteForge = new PulseGenericCrafter("pulse-graphite-forge"){{
            requirements(Category.crafting, with(Items.lead, 65, Items.graphite, 25, Items.titanium, 35));
            centerResearchRequirements(false, with(Items.coal, 125, Items.silicon, 45, Items.metaglass, 65, Items.titanium, 85));
            drawer = new DrawPulseSpinningCrafter();
            size = 2;
            itemCapacity = 30;
            powerLoss = 0.05f;
            pulseStorage = 240;
            canOverload = false;
            minRequiredPulsePercent = 0.45f;
            customConsumes.pulse = 14;
            craftTime = 75;

            consumes.items(ItemStack.with(Items.coal, 4, Items.sand, 2));

            outputItems = ItemStack.with(Items.graphite, 2, Items.silicon, 1);
        }};

        pulseCondensary = new PulseCondensary("pulse-melonaleum-condensery"){{
            requirements(Category.crafting, with(Items.copper, 345, Items.coal, 235, Items.silicon, 200, Items.titanium, 185, Items.metaglass, 130));
            centerResearchRequirements(true, with(Items.coal, 65, Items.silicon, 45, Items.pyratite, 25, Items.metaglass, 85));
            size = 2;
            powerLoss = 0.35f;
            pulseStorage = 1750;
            canOverload = false;
            minRequiredPulsePercent = 0.65f;
            customConsumes.pulse = 350;
            craftTime = 85;

            drawer = new CondensaryDrawer();
            itemCapacity = 70;

            consumes.liquid(RustingLiquids.melomae, 1.725f);
            outputItems = ItemStack.with(RustingItems.melonaleum, 35);
        }};

        pulseMelomaeMixer = new PulseGenericCrafter("pulse-melomae-mixer"){{
            requirements(Category.crafting, with(Items.lead, 80, Items.titanium, 15, Items.metaglass, 45));
            centerResearchRequirements(true, with(Items.coal, 125, Items.silicon, 45, Items.metaglass, 65, Items.titanium, 85));
            drawer = new DrawPulseLiquidMixer();
            hasLiquids = true;
            size = 2;
            powerLoss = 0.05f;
            pulseStorage = 150;
            canOverload = false;
            minRequiredPulsePercent = 0.45f;
            customConsumes.pulse = 6.5f;
            consumes.liquid(Liquids.water, 0.16f);
            craftTime = 15;
            liquidCapacity = 75;

            outputLiquid = new LiquidStack(RustingLiquids.melomae, 3);
        }};


        pulseGelPress = new PulseGenericCrafter("pulse-gel-press"){{
            requirements(Category.crafting, with(Items.copper, 95, Items.metaglass, 65, Items.plastanium, 55, RustingItems.halsinte, 35));
            centerResearchRequirements(true, with(Items.coal, 125, Items.silicon, 45, Items.metaglass, 65, Items.titanium, 85));
            drawer = new DrawPulseLiquidCrafter();
            hasLiquids = true;
            size = 3;
            powerLoss = 0.05f;
            pulseStorage = 350;
            canOverload = false;
            minRequiredPulsePercent = 0.45f;
            customConsumes.pulse = 45;
            consumes.liquid(RustingLiquids.melomae, 0.16f);
            consumes.items(ItemStack.with(RustingItems.bulastelt, 2, RustingItems.halsinte, 3, Items.lead, 1));
            craftTime = 85;
            liquidCapacity = 75;

            outputItems = ItemStack.with(RustingItems.gelChip, 3);
        }};

        pulseBarrier = new PulseBarrier("pulse-barrier"){{
            requirements(Category.defense, with(Items.copper, 8, Items.graphite, 6, Items.titanium, 5));
            centerResearchRequirements(true, with(Items.copper, 115, Items.coal, 65, Items.titanium, 30));
            size = 1;
            health = 410 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 55;
            canOverload = false;
        }};

        pulseBarrierLarge = new PulseBarrier("pulse-barrier-large"){{
            requirements(Category.defense, with(Items.copper, 32, Items.graphite, 24, Items.titanium, 20));
            centerResearchRequirements(true, with(Items.copper, 450, Items.graphite, 75, Items.titanium, 120));
            size = 2;
            health = 410 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 135;
            laserOffset = 5.5f;
            canOverload = false;
        }};

        pulseResearchCenter = new PulseResearchBlock("pulse-research-center"){{
            requirements(Category.effect, with(Items.copper, 65, Items.lead, 50, Items.coal, 25));
            centerResearchRequirements(false, with(Items.copper, 40,  Items.coal, 15));
            size = 2;
            fieldNames.add("pulseStorage");
            fieldNames.add("canOverload");
        }};

        pulseUpkeeper = new PulseChainNode("pulse-upkeeper"){{
            requirements(Category.effect, with(Items.copper, 95, Items.lead, 75, Items.silicon, 45, Items.titanium, 25));
            centerResearchRequirements(true, with(Items.copper, 550,  Items.coal, 355, Items.metaglass, 100, Items.graphite, 125, Items.titanium, 175, RustingItems.melonaleum, 75));
            size = 2;
            powerLoss = 0.0000155f;
            minRequiredPulsePercent = 0.5f;
            pulseReloadTime = 165;
            connectionsPotential = 4;
            energyTransmission = 0.5f;
            pulseStorage = 70;
            overloadCapacity = 30;
            laserRange = 10;
            laserOffset = 9;
            healingPercentCap = 13;
            healPercent = 26;
            healPercentFalloff = healPercent/3;
            overdrivePercent = 0.65f;
        }};

        smallParticleSpawner = new PulseParticleSpawner("small-particle-spawner"){{
            requirements(Category.effect, with(Items.silicon, 5, Items.graphite, 2, Items.titanium, 10));
            centerResearchRequirements(false, with());
            flags = EnumSet.of(BlockFlag.generator);
            effects = new Effect[] {Fxr.blueSpark};
            size = 1;
            health = 35 * size * size;
            projectileChanceModifier = 0;
            customConsumes.pulse = 0.25f;
            pulseStorage = 70;
            overloadCapacity = 30;
            powerLoss = 0;
            minRequiredPulsePercent = 0;
            canOverload = true;
            effectFrequency = 0.45f;
            lightRadius = 145;
            lightAlpha = 0.3f;
            lightColor = Palr.pulseBullet;
        }};

        fraeCore = new CoreBlock("frae-core"){{
            requirements(Category.effect, BuildVisibility.editorOnly, with(Items.copper, 1000, Items.lead, 800));
            alwaysUnlocked = false;

            unitType = RustingUnits.duoly;
            health = 2100;
            itemCapacity = 6500;
            size = 3;

            unitCapModifier = 13;
        }};

        //Todo: fully flesh this out
        craeCore = new PlayerCore("crae-core"){{
            requirements(Category.effect, BuildVisibility.editorOnly, with(Items.copper, 1000, Items.lead, 800));
            alwaysUnlocked = false;

            unitType = RustingUnits.glimpse;
            solid = false;
            health = 2100;
            itemCapacity = 6500;
            size = 3;

            unitCapModifier = 13;
        }};

        archangel = new DysfunctionalMonolith("archangel"){{
            requirements(Category.effect, with(Items.copper, 300, Items.lead, 115, Items.metaglass, 50, Items.titanium, 45));
            centerResearchRequirements(with(Items.copper, 350,  Items.coal, 95, Items.graphite, 55, Items.titanium, 225));
            flags = EnumSet.of(BlockFlag.turret);
            size = 3;
            health = 135 * size * size;
            projectile = RustingBullets.craeWeaver;
            projectileChanceModifier = 0;
            reloadTime = 125;
            shots = 2;
            bursts = 3;
            burstSpacing = 3;
            inaccuracy = 5;
            customConsumes.pulse = 65;
            cruxInfiniteConsume = false;
            pulseStorage = 140;
            overloadCapacity = 30;
            powerLoss = 0;
            minRequiredPulsePercent = 0;
            canOverload = true;
        }};

        pulseMotar = new PulsePulsar("pulse-motar"){{
            //requirements(Category.effect, with(Items.copper, 300, Items.lead, 115, Items.metaglass, 50, Items.titanium, 45));
            centerResearchRequirements(with(Items.copper, 350,  Items.coal, 95, Items.graphite, 55, Items.titanium, 225));
            buildVisibility = BuildVisibility.hidden;
            flags = EnumSet.of(BlockFlag.turret);
            size = 3;
            health = 135 * size * size;
            projectile = RustingBullets.craeQuadStorm;
            shots = 2;
            bursts = 3;
            burstSpacing = 7;
            inaccuracy = 13;
            projectileChanceModifier = 0;
            range = 31;
            reloadTime = 85;
            customConsumes.pulse = 25;
            cruxInfiniteConsume = true;
            pulseStorage = 70;
            overloadCapacity = 30;
            powerLoss = 0;
            minRequiredPulsePercent = 0;
            canOverload = true;
        }};

        pulseFieldGen = new PulseBlock("pulse-rip-field-generator"){{

        }};

        //region landmines
        pulseLandmine = new PulseLandmine("pulse-landmine") {{
            requirements(Category.effect, with(Items.lead, 15, Items.silicon, 10, RustingItems.melonaleum, 5));
            centerResearchRequirements(with(Items.copper, 45,  Items.coal, 245, Items.graphite, 95, Items.silicon, 55, RustingItems.melonaleum, 15));
            health = 135;
            reloadTime = 85;
            shots = 3;
            customConsumes.pulse = 10;
            pulseStorage = 75;
            cruxInfiniteConsume = true;
            canOverload = false;
            powerLoss = 0;
        }};


        //region units

        pulseCommandCenter = new PulseCommandCenter("pulse-command-center"){{
            buildVisibility = buildVisibility.shown;
            category = Category.units;
            size = 2;
        }};

        pulseFactory = new PulseUnitFactory("pulse-factory"){{
            requirements(Category.units, with(Items.copper, 75, Items.lead, 60, Items.coal, 35, Items.titanium, 25));
            centerResearchRequirements(with(Items.copper, 145,  Items.lead, 145, Items.graphite, 55, Items.titanium, 85, Items.pyratite, 35));
            customConsumes.pulse = 10f;
            powerLoss = 0.00155f;
            minRequiredPulsePercent = 0.35f;
            laserOffset = 8f;
            pulseStorage = 55;
            overloadCapacity = 25;
            size = 3;
            plans.addAll(
                new UnitPlan(RustingUnits.duono, 1920, ItemStack.with(Items.lead, 25, Items.silicon, 35, Items.titanium, 10)),
                new UnitPlan(RustingUnits.fahrenheit, 1250, ItemStack.with(Items.lead, 35, Items.silicon, 15, RustingItems.melonaleum, 10))
            );
        }};

        //not for player use, however accessible through custom games
        antiquaeGuardianBuilder = new GuardianPulseUnitFactory("antiquae-guardian-builder"){{
            requirements(Category.units, with(Items.copper, 75, Items.lead, 60, Items.coal, 35, Items.titanium, 25));
            centerResearchRequirements(false, with(Items.copper, 145,  Items.lead, 145, Items.graphite, 55, Items.titanium, 85, Items.pyratite, 35));
            consumes.liquid(RustingLiquids.melomae, 0.85f);
            hideFromUI();
            buildVisibility = BuildVisibility.hidden;
            liquidCapacity = 85;
            customConsumes.pulse = 65f;
            powerLoss = 0.0f;
            minRequiredPulsePercent = 0.65f;
            laserOffset = 8f;
            pulseStorage = 1365;
            canOverload = false;
            size = 7;
            cruxInfiniteConsume = true;
            plans.addAll(
                new UnitPlan(RustingUnits.stingray, 143080, ItemStack.with(Items.lead, 4550, Items.silicon, 1450, Items.titanium, 3500, RustingItems.halsinte, 2500, RustingItems.melonaleum, 750))
            );
        }};

        enlightenmentReconstructor = new PulseReconstructor("enlightenment-reconstructor") {{
            requirements(Category.units, with(Items.copper, 135, Items.lead, 85, Items.silicon, 45, Items.titanium, 35));
            centerResearchRequirements(with(Items.copper, 450,  Items.lead, 375, Items.silicon, 145, Items.titanium, 135, Items.pyratite, 75, RustingItems.melonaleum, 45));
            consumes.items(ItemStack.with(Items.silicon, 35, Items.titanium, 25, RustingItems.melonaleum, 25));
            customConsumes.pulse = 25f;
            powerLoss = 0.00155f;
            minRequiredPulsePercent = 0.65f;
            laserOffset = 8f;
            pulseStorage = 85;
            canOverload = false;
            size = 3;
            upgrades.add(
                new UnitType[]{RustingUnits.duono, RustingUnits.duoly},
                new UnitType[]{RustingUnits.fahrenheit, RustingUnits.celsius}
            );
            constructTime = 720;
        }};

        ascendanceReconstructor = new PulseReconstructor("ascendance-reconstructor") {{
            requirements(Category.units, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));
            centerResearchRequirements(with(Items.lead, 1255, Items.silicon, 455, Items.titanium, 235, Items.pyratite, 145, RustingItems.melonaleum, 125));
            consumes.items(ItemStack.with(Items.silicon, 145, Items.titanium, 55, RustingItems.melonaleum, 55, RustingItems.bulastelt, 85));
            customConsumes.pulse = 65f;
            powerLoss = 0.00155f;
            minRequiredPulsePercent = 0.55f;
            laserOffset = 8f;
            pulseStorage = 145;
            canOverload = false;
            size = 5;
            upgrades.add(
                    new UnitType[]{RustingUnits.duoly, RustingUnits.duanga},
                    new UnitType[]{RustingUnits.metaphys, RustingUnits.ribigen}
            );
            constructTime = 720;
        }};



        //region logic

        pulseDirectionalController = new PulseController("pulse-controller"){{
            requirements(Category.effect, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));

        }};

        pulseContactSender = new PulseContactSender("pulse-sender"){{
            requirements(Category.effect, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));

        }};

        pulseSource = new PulseSource("pulse-source"){{
            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        //endregion pulse

        //region frae

        //temperary block
        fraeResarchCenter = new CapsuleBlockResearchCenter("frae-research-center"){{
            requirements(Category.effect, with(Items.copper, 60, Items.lead, 25, Items.silicon, 25));
            size = 2;
        }};
        //endregion frae

        //region turrets

        thrum = new HealerBeamTurret("thrum"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 25, Items.silicon, 25));
            health = 220;
            size = 1;
            reloadTime = 60;
            range = 115;
            shootCone = 1;
            powerUse = 0.5f;
            rotateSpeed = 10;
            squares = 3;
            alphaFalloff = 0.65f;
            maxEffectSize = 4;
            healing = 40;
            targetAir = true;
            targetGround = true;
            shootType = RustingBullets.paveBolt;
        }};

        spikent = new AreaHealerBeamTurret("spikent"){{
            requirements(Category.turret, with(Items.copper, 125, Items.lead, 85, Items.silicon, 55, RustingItems.melonaleum, 35));
            health = 650;
            size = 2;
            reloadTime = 35;
            range = 150;
            shootCone = 3;
            powerUse = 0.8f;
            rotateSpeed = 8;
            alphaFalloff = 0.35f;
            healing = 35;
            healRadius = 25;
            targetAir = true;
            targetGround = true;
            shootType = RustingBullets.paveBolt;
        }};

        conserve = new ChainHealerBeamTurret("conserve"){{
            requirements(Category.turret, with(Items.copper, 125, Items.lead, 85, Items.silicon, 55, RustingItems.melonaleum, 35));
            size = 2;
            range = 150;
            reloadTime = 120;
            healing = 35;

            shootLength = 4.5f;
            requiresWarmup = false;
        }};

        kindle = new PowerTurret("kindle"){{
            requirements(Category.turret, with());
            cooldown = 0.05f;
            health = 350;
            reloadTime = 95;
            range = 185;
            shootType = new BulletSpawnBulletType(0, 45, "none") {{
                consUpdate = RustingBullets.homing;
                trueSpeed = 0.325f;
                range = 185;
                collides = true;
                pierce = true;
                pierceBuilding = true;
                homingPower = 0.05f;
                homingRange = 0;
                lifetime = 192;
                frontColor = Pal.lightPyraFlame;
                backColor = Palr.darkPyraBloom;
                despawnEffect = Fx.sparkShoot;
                drawSize = 3.5f;
                bullets = Seq.with(
                    new BulletSpawner(){{
                        bullet = RustingBullets.mhemShard;
                        reloadTime = 35;
                        manualAiming = true;
                        inaccuracy = 0;
                        intervalIn = 35;
                        intervalOut = 65;
                    }}
                );
            }};
        }};

        plasmae = new DirectAimPowerTurret("plasmae") {{
            requirements(Category.turret, with());
            cooldown = 0.11f;
            size = 2;
            health = 350;
            reloadTime = 120;
            shots = 5;
            burstSpacing = 5;
            inaccuracy = 5;
            spread = 5;
            range = 215;
            shootLength = 5;
            shootType = RustingBullets.mhemaeShard;
        }};

        photosphere = new DirectAimPowerTurret("photosphere"){{
            requirements(Category.turret, with());
            cooldown = 0.11f;
            size = 3;
            health = 350;
            reloadTime = 550;
            shots = 2;
            spread = 90;
            range = 450;
            shootLength = 5;
            shootType = new BulletSpawnBulletType(2, 45, "none") {{
                consUpdate = RustingBullets.velbasedHomingFlame;
                trueSpeed = 0.325f;
                range = 185;
                collides = true;
                pierce = true;
                pierceBuilding = true;
                homingPower = 0.015f;
                homingRange = 0;
                lifetime = 750;
                frontColor = Pal.lightPyraFlame;
                backColor = Palr.darkPyraBloom;
                despawnEffect = Fx.sparkShoot;
                drawSize = 3.5f;
                bullets = Seq.with(
                    new BulletSpawner(){{
                        bullet = new BounceBulletType( 5.5f,  4, "bullet"){{
                            consUpdate = RustingBullets.velbasedHomingFlame;
                            despawnEffect = Fx.fireSmoke;
                            hitEffect = Fx.fire;
                            bounceEffect = Fxr.shootMhemFlame;
                            incendAmount = 10;
                            status = StatusEffects.burning;
                            statusDuration = 3600;
                            maxRange = 156;
                            width = 6;
                            height = 8;
                            hitSize = 12;
                            lifetime = 85;
                            homingPower = 0.25f;
                            homingRange = 0;
                            homingDelay = 35;
                            hitEffect = Fx.hitFuse;
                            trailLength = 0;
                            bounciness = 0.85f;
                            bounceCap = 0;
                            weaveScale = 4;
                            weaveMag = 3;
                            knockback = 1;
                            drag = 0.0015f;
                            pierceBuilding = false;
                            buildingDamageMultiplier = 0.15f;
                        }};
                        reloadTime = 15;
                        manualAiming = true;
                        inaccuracy = 0;
                        intervalIn = 35;
                        intervalOut = 65;
                    }}
                );
            }};
        }};


        pilink = new HarpoonTurret("pilink") {{
            requirements(Category.turret, with(Items.lead, 35, RustingItems.bulastelt, 25));
            size = 1;
            health = 250;
            reloadTime = 115;
            range = 245;
            pullStrength = 35;
            basePullStrength = 0;
            shootLength = 3.5f;
            ammoTypes = ObjectMap.of(RustingItems.bulastelt, RustingBullets.buulasteltSmallHarpoon);
            shootSound = ModSounds.harpoonLaunch;
            shootEffect = Fx.shootBig;
            shootShake = 3;
        }};

        tether = new HarpoonTurret("tether"){{
            requirements(Category.turret, with(Items.lead, 75, Items.titanium, 55, Items.thorium, 15, RustingItems.camaintAmalgam, 75));
            size = 2;
            health = 350 * size * size;
            shootLength = 6.25f;
            reloadTime = 115;
            range = 245;
            pullStrength = 85;
            ammoTypes = ObjectMap.of(RustingItems.cameoShardling, RustingBullets.cameoSmallHarpoon);
            shootSound = ModSounds.harpoonLaunch;
            shootEffect = Fx.shootBig;
            shootShake = 3;
            unitSort = (unit, x, y) -> unit.vel.len();
        }};

        prikend = new PowerTurret("prikend"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 45, Items.silicon, 35));
            range = 185f;
            shootLength = 2;
            chargeEffects = 7;
            recoilAmount = 2f;
            reloadTime = 75f;
            cooldown = 0.03f;
            powerUse = 1.25f;
            shootShake = 2f;
            shootEffect = Fx.hitFlameSmall;
            smokeEffect = Fx.none;
            heatColor = Color.orange;
            size = 1;
            health = 280 * size * size;
            shootSound = Sounds.bigshot;
            shootType = RustingBullets.fossilShard;
            shots = 2;
            burstSpacing = 15f;
            inaccuracy = 2;
        }};

        prsimdeome = new PanelTurret("prsimdeome"){{
            requirements(Category.turret, with(Items.copper, 85, Items.lead, 70, Items.silicon, 50, RustingItems.bulastelt, 35));
            range = 165f;
            chargeEffects = 7;
            recoilAmount = 2f;
            reloadTime = 96f;
            cooldown = 0.03f;
            powerUse = 4f;
            shootShake = 2f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Color.red;
            size = 2;
            health = 295 * size * size;
            shootSound = Sounds.flame2;
            shootType = RustingBullets.mhemShard;
            shots = 6;
            spread = 10f;
            burstSpacing = 5f;
            inaccuracy = 10;
            panels.add(
                new PanelHolder(name){{
                    panelX = 6;
                    panelY = -4;
                }}
            );
        }};

        prefraecon = new PanelTurret("prefraecon"){{
            requirements(Category.turret, with(Items.titanium, 115, Items.silicon, 65, RustingItems.bulastelt, 55, RustingItems.melonaleum, 45));
            range = 200f;
            recoilAmount = 2f;
            reloadTime = 65f;
            powerUse = 6f;
            shootShake = 5f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Pal.darkPyraFlame;
            size = 3;
            health = 310 * size * size;
            shootSound = Sounds.release;
            shootType = RustingBullets.fraeShard;
            panels.add(
                new PanelHolder(name){{
                    panelX = 10;
                    panelY = -4;
                }}
            );
        }};

        rangi = new PanelTurret("rangi"){{
            requirements(Category.turret, with(Items.metaglass, 75, Items.silicon, 55, RustingItems.taconite, 45, RustingItems.bulastelt, 25));
            range = 166f;
            recoilAmount = 2f;
            reloadTime = 145f;
            shootCone = 360;
            powerUse = 8f;
            shootShake = 1f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Palr.dustriken;
            size = 3;
            health = 255 * size * size;
            shootSound = Sounds.release;
            shootType = RustingBullets.cloudyVortex;
        }};

        pafleaver  = new PanelTurret("pafleaver"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            buildVisibility = BuildVisibility.hidden;
            range = 260f;
            recoilAmount = 2f;
            reloadTime = 60f;
            powerUse = 6f;
            shootShake = 2f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Pal.darkPyraFlame;
            size = 4;
            health = 345 * size * size;
            shootSound = Sounds.flame;
            shootType = RustingBullets.paveShard;
            shots = 3;
            burstSpacing = 5;
            panels.add(
                new PanelHolder(name + "1"){{
                    panelX = 10.75;
                    panelY = -4.5;
                }},
                new PanelHolder(name + "2"){{
                    panelX = -10.75;
                    panelY = -4.5;
                }},
                new ShootingPanelHolder(name + "1"){{
                    panelX = 13.75;
                    panelY = -3.75;
                    shootType = RustingBullets.mhemShard;
                    lifetimeMulti = 2.5f;
                }},
                new ShootingPanelHolder(name + "2"){{
                    panelX = -13.75;
                    panelY = -3.75;
                    shootType = RustingBullets.mhemShard;
                    lifetimeMulti = 2.5f;
                }}
            );
        }};

        octain = new AutoreloadItemTurret("octain"){{
            requirements(Category.turret, with(Items.graphite, 35, Items.metaglass, 25, RustingItems.taconite, 65));
            size = 2;
            health = 255 * size * size;
            ammo(
                Items.metaglass, RustingBullets.spawnerGlass,
                RustingItems.bulastelt, RustingBullets.spawnerBulat
            );
            shots = 2;
            burstSpacing = 5;
            inaccuracy = 2;
            reloadTime = 125f;
            recoilAmount = 2.5f;
            range = 175f;
            shootCone = 25f;
            shootSound = Sounds.release;
            coolantMultiplier = 1.1f;
            autoreloadThreshold = 1 - 1/reloadTime;

        }};

        triagon = new AutoreloadItemTurret("triagon"){{
            requirements(Category.turret, with(Items.graphite, 75, Items.titanium, 45, RustingItems.taconite, 95, RustingItems.bulastelt, 35));
            size = 3;
            health = 295 * size * size;
            ammo(
                Items.pyratite, RustingBullets.flamstrikenVortex,
                RustingItems.melonaleum, RustingBullets.boltingVortex
            );
            shots = 1;
            reloadTime = 325f;
            recoilAmount = 2.5f;
            range = 175f;
            shootCone = 25f;
            shootSound = Sounds.release;
            coolantMultiplier = 1.15f;
            autoreloadThreshold = 1 - 1/reloadTime;
            shootLength = 5.25f;
        }};

        cuin = new QuakeTurret("cuin"){{
            requirements(Category.turret, with(Items.graphite, 35, Items.metaglass, 25, RustingItems.taconite, 65));
            buildVisibility = BuildVisibility.hidden;
            size = 3;
            health = 255 * size * size;
            targetAir = false;
            shots = 3;
            spread = 15;
            reloadTime = 162f;
            recoilAmount = 2.5f;
            range = 175f;
            quakeInterval = 2;
            spacing = 7;
            shootCone = 25f;
            shootSound = Sounds.explosionbig;
            coolantMultiplier = 0.85f;
            shootType = Bullets.artilleryIncendiary;
            shootEffect = Fx.flakExplosion;
        }};

        icosahen = new LiquidBeamTurret("icosahen"){{
            requirements(Category.turret, ItemStack.with());
            buildVisibility = BuildVisibility.hidden;
            size = 2;
            health = 145 * size * size;
            reloadTime = 250;
            range = 135;
            shots = 4;
            burstSpacing = 3;
            pressureCap = 11.5f;
            ammo(
                    Liquids.water, RustingBullets.waterBeamShot,
                    Liquids.slag, RustingBullets.slagBeamShot,
                    Liquids.cryofluid, RustingBullets.cryoBeamShot,
                    Liquids.oil, RustingBullets.oilBeamShot,
                    RustingLiquids.melomae, RustingBullets.melomaeBeamShot,
                    RustingLiquids.cameaint, RustingBullets.cameoBeamShot
            );
        }};

        pulver = new LightningTurret("pulver"){{
            centerResearchRequirements(true, ItemStack.with(Items.silicon, 260, RustingItems.gelChip, 50));
            requirements(Category.turret, ItemStack.with(RustingItems.gelChip, 25, Items.metaglass, 45, RustingItems.bulastelt, 55));
            size = 2;
            health = 185 * size * size;


            canOverload = false;
            pulseStorage = 125;

            customConsumes.pulse = 10.875f;

            range = 145;
            lightning = 4;
            healAmount = 15;

            shootCone = 65;
            reloadTime = 35;
            damage = 65;

            shootSound = Sounds.spark;
            lightColor = Palr.pulseBullet;
            lightAlpha = 0.65f;
            beamLength = 35;
            healEffect = Fxr.healingWaterSmoke;
            color = Color.valueOf("#a9e2ea");
        }};

        contingent = new PulsePreciseLaserTurret("contingent"){{
            category = Category.effect;
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 3;
            damage = 4.5f;
            health = 155 * size * size;
        }};

        horaNoctis = new AutoreloadItemTurret("hora-noctis"){{
            requirements(Category.turret, with());
            buildVisibility = BuildVisibility.hidden;
            size = 2;
            health = 165 * size * size;
            shootLength = -35;
            range = 265;
            spread = 2;
            inaccuracy = 4;
            xRand = 8;
            shots = 3;
            burstSpacing = 6;
            reloadTime = 42;
            consumes.power(0.8f);
            ammo(
                Items.titanium, RustingBullets.lightfractureTitanim,
                RustingItems.bulastelt, RustingBullets.lightfractureBulat
            );
        }};

        holocaust = new AutoreloadItemTurret("holocaust"){{
            requirements(Category.turret, with());
            buildVisibility = BuildVisibility.hidden;
            size = 2;
            health = 315 * size * size;
            range = 152;
            shootLength = 7;
            spread = 2;
            inaccuracy = 4;
            xRand = 5;
            shots = 2;
            reloadTime = 4.75f;
            ammo(
                Items.pyratite, RustingBullets.longPyraFlame,
                Items.thorium, RustingBullets.longThorFlame
            );
        }};

        spraien = new PumpLiquidTurret("spraien"){{
            requirements(Category.turret, with(Items.lead, 16, RustingItems.taconite, 23, RustingItems.halsinte, 12));
            ammo(
                Liquids.water, Bullets.waterShot,
                Liquids.slag, Bullets.slagShot,
                Liquids.cryofluid, Bullets.cryoShot,
                Liquids.oil, Bullets.oilShot,
                RustingLiquids.melomae, RustingBullets.melomaeShot,
                RustingLiquids.cameaint, RustingBullets.cameoShot
            );
            floating = true;
            size = 1;
            recoilAmount = 0f;
            reloadTime = 54f;
            shots = 5;
            spread = 2.5f;
            burstSpacing = 15;
            inaccuracy = 2f;
            shootCone = 50f;
            liquidCapacity = 16f;
            shootEffect = Fx.shootLiquid;
            range = 110f;
            health = 250;
            flags = EnumSet.of(BlockFlag.turret, BlockFlag.extinguisher);
        }};

        glare = new BerthaTurret("glare"){{
            category = Category.turret;
            shootType = RustingBullets.saltyBolt;
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 3;
            health = 8000;

            shots = 5;
            inaccuracy = 0.5f;
            burstSpacing = 2;

            range = 1350;
            restitution = 0.0075f;
            cooldown = 0.0025f;
            reloadTime = 420;
            maxCharge = 3600;
            xOffset = 4.5f;
            shellReloadOffset = 2.75f;
            yOffset = 0.75f;
            recoilAmount = 5;

            shellRecoil = 1.5f;
            shootSound = Sounds.shootBig;

            heatColor = Color.orange;
            canOverdrive = false;
        }};

        refract = new ItemTurret("refract"){{
            requirements(Category.turret, with(Items.copper, 40, Items.graphite, 17));
            ammo(
                Items.graphite, RustingBullets.denseLightRoundaboutLeft,
                RustingItems.halsinte, RustingBullets.saltyLightRoundaboutLeft,
                RustingItems.melonaleum, RustingBullets.craeLightRoundaboutLeft
            );

            health = 340;

            shots = 3;
            burstSpacing = 7;
            reloadTime = 105f;
            recoilAmount = 1.5f;
            range = 135f;
            inaccuracy = 15f;
            shootCone = 15f;
            shootSound = Sounds.bang;
        }};

        diffract = new ItemTurret("diffract"){{
            requirements(Category.turret, with(Items.copper, 85,  Items.lead, 70, Items.graphite, 55));
            ammo(
                Items.graphite, RustingBullets.denseLightGlaive,
                RustingItems.melonaleum, RustingBullets.craeLightGlaive,
                RustingItems.halsinte, RustingBullets.saltyLightGlaive
            );

            ammoPerShot = 4;
            health = 960;

            size = 2;
            shots = 1;

            reloadTime = 120f;
            recoilAmount = 1.5f;
            range = 175f;
            inaccuracy = 0;
            shootCone = 15f;
            shootSound = Sounds.bang;
        }};

        reflect = new BoomerangTurret("reflect"){{
            requirements(Category.turret, with(Items.copper, 40, Items.graphite, 17));
            ammo(
                Items.graphite, RustingBullets.craeLightGlaiveLeft,
                RustingItems.halsinte, RustingBullets.saltyLightRoundaboutLeft
            );
            buildVisibility = BuildVisibility.hidden;

            health = 1460;

            size = 3;
            shots = 8;
            spread = 45;

            burstSpacing = 7.5f;
            shootLength = 11;
            reloadTime = 60f;
            recoilAmount = 0f;
            range = 165f;
            inaccuracy = 0;
            shootCone = 360f;
            rotateSpeed = 1;
            shootSound = Sounds.bang;
        }};

        //endregion


        //region unit

        hotSpringSprayer = new HotSpring("hot-spring-sprayer"){{
            requirements(Category.units, with(RustingItems.bulastelt, 6, RustingItems.halsinte, 12));
            consumes.liquid(Liquids.water, 0.05f);
            liquidCapacity = 250;
            healthPerSecond = 35;
        }};

        coldSpringSprayer = new HotSpring("cold-spring-sprayer"){{
            requirements(Category.units, with(RustingItems.bulastelt, 6, RustingItems.halsinte, 12));
            consumes.liquid(RustingLiquids.melomae, 0.05f);
            apply = StatusEffects.freezing;
            liquidCapacity = 250;


            healthPerSecond = 15;
            smokeEffect = Fxr.healingColdWaterSmoke;

            washOff.removeAll(Seq.with(StatusEffects.freezing, RustingStatusEffects.hailsalilty));
            washOff.addAll(RustingStatusEffects.macrosis, RustingStatusEffects.macotagus, RustingStatusEffects.balancedPulsation, RustingStatusEffects.causticBurning);
        }};

        fraeFactory = new UnitFactory("frae-factory"){{
            requirements(Category.units, with(Items.copper, 75, RustingItems.taconite, 55, RustingItems.bulastelt, 30));
            plans = Seq.with(
                new UnitPlan(RustingUnits.marrow, 2345, with(Items.silicon, 35, Items.copper, 15, RustingItems.taconite, 25)),
                new UnitPlan(RustingUnits.stingray, 60, with()),
                    new UnitPlan(RustingUnits.kelvin, 60, with())
            );
            size = 3;
            consumes.power(0.85f);
        }};

        absentReconstructor = new Reconstructor("absent-reconstructor"){{
            requirements(Category.units, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));
            consumes.items(ItemStack.with(Items.silicon, 65, Items.titanium, 25, Items.pyratite, 5, RustingItems.melonaleum, 15));
            size = 3;
            upgrades.add(
                    new UnitType[]{RustingUnits.marrow, RustingUnits.metaphys}
            );
            constructTime = 854;
        }};

        dwindlingReconstructor = new Reconstructor("dwindling-reconstructor"){{
            requirements(Category.units, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));
            consumes.items(ItemStack.with(Items.silicon, 65, Items.titanium, 25, Items.pyratite, 5, RustingItems.melonaleum, 15));
            size = 5;
            upgrades.add(
                    new UnitType[]{RustingUnits.metaphys, RustingUnits.ribigen}
            );
            constructTime = 1460;
        }};

        pulseDistributor = new PulsePoint("pulse-distributor"){{
            requirements(Category.units, with(Items.lead, 465, Items.metaglass, 245, Items.pyratite, 85, Items.titanium, 85));
            hideFromUI();
        }};

        //endregion

        //region, *sigh* logic

        mhemLog = new UnbreakableMessageBlock("mhem-log"){{
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        raehLog = new UnbreakableMessageBlock("raeh-log"){{
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        fraeLog = new UnbreakableMessageBlock("frae-log"){{
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        halsinteLamp = new LightBlock("halsinte-lamp"){{
            requirements(Category.effect, BuildVisibility.lightingOnly, with(Items.metaglass, 5, RustingItems.halsinte, 16, RustingItems.bulastelt, 9));
            hasPower = false;
            brightness = 0.15f;
            radius = 85;
        }};

        gelBasin = new Block("gel-crucible"){{

        }};
        //endregion

        addLiquidAmmoes(Blocks.wave, ObjectMap.of(RustingLiquids.melomae, RustingBullets.melomaeShot, RustingLiquids.cameaint, RustingBullets.cameoShot));
        addLiquidAmmo(Blocks.tsunami, RustingLiquids.melomae, RustingBullets.heavyMelomaeShot);
        addLiquidAmmo(Blocks.tsunami, RustingLiquids.cameaint, RustingBullets.heavyCameoShot);
    }
}
