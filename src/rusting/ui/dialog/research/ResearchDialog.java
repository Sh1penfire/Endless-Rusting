package rusting.ui.dialog.research;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Cicon;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.ctype.ResearchType;
import rusting.graphics.ResearchCenterUI;
import rusting.interfaces.ResearchCenter;
import rusting.interfaces.ResearchableObject;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.ui.dialog.Texr;

import static mindustry.Vars.mobile;
import static mindustry.Vars.player;

public class ResearchDialog extends CustomBaseDialog{

    private TextField search;
    private Table all = new Table();
    private Table information = new Table();
    public Table informationButtons = new Table();
    public Table currentlySelected = new Table();

    public Seq<ResearchType> researchTypes = Seq.with();
    public ObjectMap<String, Seq<ResearchableObject>> categorized = ObjectMap.of();
    public ResearchableObject selected;
    public Seq<ResearchableObject> researchable = new Seq<ResearchableObject>();
    public Seq<String> databaseQuotes = new Seq<String>();
    public Table uiDisplay = new Table();

    public ObjectMap<Boolf<ResearchableObject>, Table> buttons = ObjectMap.of();

    private static int tmpWidth = 0;

    public ResearchDialog() {
        super(Core.bundle.get("erui.researchcenter"), Core.scene.getStyle(DialogStyle.class));
        shouldPause = true;
        addCloseButton();

        all.margin(20).marginTop(0f).top().left().setWidth(300);

        information.margin(15).marginTop(0).top().left().setWidth(450);

        addButton(Core.atlas.getDrawable("endless-rusting-unlock-block"), (o) -> !o.researched(player.team()), () -> {
            information.clear();
            Tile tile = Varsr.research.getCenter(Seq.with(selected.researchTypes().get(0)), player.team()).tile;

            ItemStack[] rCost = selected.getResearchModule().centerResearchRequirements;
            Table itemsCost = new Table();

            itemsCost.table(table -> {

                //used for columns.
                int count = 1;
                int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 8);

                for(ItemStack costing: rCost) {
                    Image itemImage = new Image(new TextureRegionDrawable().set(costing.item.icon(Cicon.medium))).setScaling(Scaling.fit);

                    table.stack(
                            itemImage,
                            new Table(t -> {
                                t.add(Math.min(tile.build.team.core().items.get(costing.item), costing.amount) + "/" + costing.amount);
                            }).left().margin(1, 3, 2, 0)
                    ).pad(10f);
                    if((count++) % cols == 0) table.row();
                }
            });

            information.pane(table -> {
                table.center();
                table.button("Unlock?", () -> {
                    Building building = tile.build;
                    CoreBuild coreBlock = building.team.core();
                    boolean canResearch = false;

                    //if it's infinite resources or the core has the resources available, continue
                    if(Vars.state.rules.infiniteResources || coreBlock.items.has(rCost, 1)){

                        //remove items from core
                        for(int i = 0; i < selected.getResearchModule().centerResearchRequirements.length; i++){
                            coreBlock.items.remove(selected.getResearchModule().centerResearchRequirements[i]);
                        }

                        //research the block
                        building.configure(selected.name());
                        Sounds.unlock.at(player.x, player.y);
                        rebuildInformation();
                    }
                }).height(75f).width(145);
                table.image(selected.researchUIcon()).size(8 * 12);
                table.row();
                table.add(itemsCost);
            });
        }, "Unlock Block");

        addButton(Core.atlas.getDrawable("endless-rusting-block-information"), (o) -> true, () -> {
            information.clear();
            information.pane(pane -> {
                pane.add("name:" + selected.localizedName());
                pane.row();
                if(selected.customStatHolder() != null){
                    pane.add("Unique Stats");
                    pane.add(new Image().setScaling(Scaling.fit)).fillX();
                    pane.table(t -> {
                        ResearchCenterUI.displayCustomStats(t, selected);
                    });
                    pane.row();
                }
            });
        },  "Information");
        addButton(Core.atlas.getDrawable("endless-rusting-upgrade-block"), (o) -> o.upgrades().size > 0, () -> {}, "Upgrade Block");

    }

    @Override
    public Dialog show() {
        //split into three different methods to make it easier to tell where something goes wrong
        refresh();
        makeList();
        setup();
        return super.show();
    }

    public void makeList() {
        researchable.clear();

        researchTypes.each(type -> {
            Varsr.research.researchMap.get(type).each(m -> {

                if(researchable.contains(m.item)) return;

                Seq<ResearchType> containing = m.item.researchTypes().copy();

                researchTypes.each(t -> {
                    if(containing.contains(t)) containing.remove(t);
                });

                if(containing.size <= 0) researchable.add(m.item);
            });
        });
    }

    public void refresh() {
        researchTypes.clear();

        Groups.build.each(b -> {
            if(b.block instanceof ResearchCenter) ((ResearchCenter) b.block).researchTypes().each(r -> {
                if(!researchTypes.contains(r)) researchTypes.add(r);
            });
        });
    }

    public void setup(){

        cont.clear();
        cont.margin(25);
        cont.center();

        rebuildList();

        int size = 650;

        cont.pane(pane -> {
            pane.table(s -> {
                s.image(Icon.zoom).padRight(8);
                search = s.field(null, text -> rebuildList()).growX().get();
                search.setMessageText(Core.bundle.get("players.search"));
                search.addInputDialog();
            }).fillX().padTop(4).row();

            tmpWidth = Math.max(Core.graphics.getWidth() - 750, 450);
            //contains the blocks categorized. Rebuild the all table after building this table/updating the text in the searchbar
            pane.table(Texr.button, leftPane -> {

                leftPane.table(t -> {
                    t.background(Texr.button);
                    t.add("Researchable Items").growX().top().color(Palr.lightstriken);
                    t.row();
                    t.image().width(210).left().pad(5).padLeft(0).padRight(0).height(4).color(Pal.accent);
                }).top().padTop(0).left();

                leftPane.row();

                leftPane.pane(all).padTop(35).grow().fill();

            }).height(700).width(tmpWidth).left().padLeft(width/3).top().padTop(35);

            pane.row();

            pane.table(t -> {
                t.add(currentlySelected).padBottom(10);
                t.row();
                Image i = new Image();
                i.setWidth(650);
                i.setHeight(4);
                t.add(i).width(650).height(4);
            }).fillX().height(64).padTop(35).row();

            tmpWidth = Math.max(Core.graphics.getWidth() - 350, 750) - 150;
            //contains all the extra information for the block/unlocking it
            pane.table(Texr.button, rightPane -> {

                if(selected == null){
                    rightPane.add(information);
                }

                else{
                    rightPane.pane(information).padTop(35).growX().fillX();
                }

                rebuildInformation();

            }).width(tmpWidth).left().padLeft(tmpWidth/3).height(size).top().padTop(5);

            //contains the additional buttons that show up
            pane.table(Texr.button, rightPane -> {
                rightPane.pane(informationButtons);
            }).height(size).width(150).padTop(5).padLeft(0);
        }).grow();
    }

    public void rebuildList(){

        categorized.clear();

        Seq<ResearchType> validTypes = Seq.with();

        Varsr.content.researchTypes().each(r -> {
            if(Varsr.research.getCenter(r, player.team()) != null) validTypes.add(r);
        });

        Boolf<Seq<ResearchType>> valid = (types) -> {
            Seq<ResearchType> remainingTypes = types.copy();
            validTypes.each(t -> {
                if(types.contains(t)) remainingTypes.remove(t);
            });
            return remainingTypes.size == 0;
        };

        researchable.each(r -> {

            if(r.hidden() || (search != null && search.getText().length() > 0 && !r.localizedName().toLowerCase().contains(search.getText().toLowerCase())) || !valid.get(r.researchTypes())) return;

            if(!categorized.containsKey(r.categoryName())) categorized.put(r.categoryName(), Seq.with());
            categorized.get(r.categoryName()).add(r);
        });

        all.clear();

        categorized.each((title, types) -> {

            all.table(t -> {
                t.add(title).left().top().color(Pal.accent).left();
                t.row();
                t.image().growX().left().pad(5).padLeft(0).padRight(0).height(4).color(Pal.accent);
            }).top().padTop(0).growX();

            all.row();

            int cols = 9;
            final int[] count = {0};

            all.table(t -> {
                types.each(type -> {

                    Image image = new Image(type.researchUIcon()).setScaling(Scaling.fit);

                    t.add(image).size(45).pad((64 - 45) / 2);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);

                    image.addListener(new Tooltip(tip -> {
                        tip.background(Texr.button).add((type.researched(Vars.player.team()) ? "The " : "(Locked)\nThe ") + type.localizedName());
                        tip.row();
                    }));

                    if (!mobile) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? (type.researched(Vars.player.team()) ? Color.white : Palr.dustriken) : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                    }

                    image.clicked(() -> {
                        if(selected != type) selected = type;
                        else selected = null;
                        rebuildInformation();
                    });

                    if ((++count[0]) % cols == 0) {
                        t.row();
                    }

                });
            });

            all.row();
        });
    }

    public void rebuildInformation(){

        information.clear();

        informationButtons.clear();

        currentlySelected.clear();

        //setup buttons

        if(selected != null){
            currentlySelected.add("Currently selected: " + selected.localizedName());
            Image selectedIcon = new Image(selected.researchUIcon()).setScaling(Scaling.fit);
            currentlySelected.add(selectedIcon).size(32);

            //currently unfinished
            buttons.each((shown, button) -> {
                if(shown.get(selected)) {
                    informationButtons.add(button);
                    informationButtons.row();
                }
            });
        }
        else{
            currentlySelected.add("Currently selected: none");

            if(Varsr.debug || Mathf.chance(0.1f)) information.add(Varsr.defaultRandomQuotes.random());
            else information.add("[grey]<select something for more information :D>");
            return;
        }
    }

    public void addButton(Drawable drawable, Boolf<ResearchableObject> shown, Runnable r, String tooltip){

        if(buttons.containsKey(shown)) return;

        //add an optional button which can be hidden
        Table addedButton = new Table();
        addedButton.clicked(r);

        Image buttonImage = new Image(drawable);
        buttonImage.setScaling(Scaling.fit);

        ClickListener listener = new ClickListener();
        buttonImage.addListener(listener);

        if (!mobile) {
            buttonImage.addListener(new HandCursorListener());
            buttonImage.update(() -> buttonImage.color.lerp(!listener.isOver() ? Color.gray : Color.white, Mathf.clamp(0.4f * Time.delta)));
        }

        buttonImage.addListener(
            new Tooltip(t -> {
                t.background(Texr.button);
                t.add(tooltip);
            })
        );

        addedButton.add(buttonImage).size(80).top();
        addedButton.background(Texr.button);

        buttons.put(shown, addedButton);
    }
}
