package rusting.ui.dialog.ingame;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.Sounds;
import rusting.Varsr;
import rusting.content.RustingAchievements;
import rusting.ctype.UnlockableAchievement;
import rusting.game.RustingEvents.AchievementQuestionMarkClick;
import rusting.game.RustingEvents.AchievementUnlockEvent;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.ui.dialog.Texr;

public class ERAchievementsDialog extends CustomBaseDialog {

    private final float achievementOffset = 10;
    private int questionMarkClick = 0;

    public ERAchievementsDialog() {
        super(Core.bundle.get("erui.ahievements"), Core.scene.getStyle(DialogStyle.class), false);
        addCloseListener();
        Events.on(AchievementUnlockEvent.class, e -> {
            if(isShown()){
                hide();
                show();
            }
            else rebuild();
        });
    }

    public void rebuild(){
        cont.clear();
        addCloseListener();
        cont.pane(p -> {
            Varsr.content.achievements().each(a -> {
                p.table(t -> {
                    addAchievement(t, a);
                }).padLeft(achievementOffset);
            });
        });
    }

    public void addAchievement(Table table, UnlockableAchievement achievement){
        table.table(t -> {
            Image image = new Image(achievement.unlocked() ? achievement.uiIcon : Core.atlas.find("endless-rusting-locked"));
            if(achievement.unlocked()) {
                image.addListener(
                    new Tooltip(tip -> {
                        tip.background(Texr.button).table(t2 -> {
                            t2.add(achievement.localizedName);
                            t2.row();
                            t2.add(achievement.description);
                            t2.row();
                            t2.add("[grey]" + achievement.details);
                        });
                    })
                );
            }
            else {
                image.clicked(() -> {
                    Sounds.buttonClick.play();
                    questionMarkClick++;
                    if(questionMarkClick >= 100 && RustingAchievements.youMonster.locked()){
                        RustingAchievements.youMonster.unlock();
                        Events.fire(AchievementUnlockEvent.class, new AchievementUnlockEvent(RustingAchievements.youMonster));
                    }
                    Events.fire(new AchievementQuestionMarkClick());
                    image.color.set(Color.black);
                });
                image.addListener(new HandCursorListener());
                image.update(() -> image.color.lerp(Color.white, Mathf.clamp(0.4f * Time.delta)));
            }
            t.add(image).size(64, 64).top().padTop(15);
        }).top();
    }

    @Override
    public Dialog show() {
        rebuild();
        return super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
