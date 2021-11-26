package rusting.content;

import arc.graphics.Color;
import mindustry.Vars;
import mindustry.ctype.ContentList;
import mindustry.game.Team;

public class RustingTeams implements ContentList {
    public static Team
    antiquumNatives, acrillimyl, pulseInfected, voidInfected;
    @Override
    public void load() {
        Vars.mods.getScripts().runConsole(
            "let natives = extend(Team, 113, \"Antiquum Natives\", Color.valueOf(\"#70696c\"), {});" +
            "let acrillimyl = extend(Team, 114, \"Antiquum Natives\", Color.valueOf(\"#b6cad6\"), {});" +
            "let pulseInfected = extend(Team, 115, \"Pulse Infected\", Color.valueOf(\"#5c79d0\"), Color.valueOf(\"#646eb2\"), Color.valueOf(\"#6977d6\"), (\"#8199e1\"{});" +
            "let voidInfected = extend(Team, 116, \"Void Infected\", Color.valueOf(\"#3d3742\"), {});" +
            "Team.baseTeams = Arrays.copyOf(Team.baseTeams, Team.baseTeams.length + 4);" +
            "Team.baseTeams[Team.baseTeams.length - 1] = acrillimyl;" +
            "Team.baseTeams[Team.baseTeams.length -m 2] = natives;" +
            "Team.baseTeams[Team.baseTeams.length - 3] = pulseInfected;" +
            "Team.baseTeams[Team.baseTeams.length - 4] = voidInfected;"
        );

        antiquumNatives = Team.get(113);
        antiquumNatives.name = "antiquum-natives";
        antiquumNatives.palette[0].set(antiquumNatives.color.set(Color.valueOf("#70696c")));
        antiquumNatives.palette[1] = antiquumNatives.color.cpy().mul(0.75f);
        antiquumNatives.palette[2] = antiquumNatives.color.cpy().mul(0.5f);

        acrillimyl = Team.get(114);
        //acrillimyl = new Team(180, "Acrillimyl", Color.valueOf("#b6cad6"));
    }
}
