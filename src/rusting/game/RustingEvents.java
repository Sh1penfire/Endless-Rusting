package rusting.game;

import rusting.ctype.UnlockableAchievement;

public class RustingEvents {

    /** Called when the player taps/clicks on an achievement while it's locked.*/
    public static class AchievementQuestionMarkClick{}
    /** Called when the player completes an achievement.*/
    public static class AchievementUnlockEvent{
        public final UnlockableAchievement achievement;

        public AchievementUnlockEvent(UnlockableAchievement achievement){
            this.achievement = achievement;
        }
    }
}
