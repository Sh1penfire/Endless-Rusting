package rusting.ui.dialog.ingame;

import arc.Core;
import arc.scene.ui.layout.Cell;
import rusting.ui.dialog.CustomBaseDialog;

public class ModFinishDialog extends CustomBaseDialog {
    private String name = "";
    private Cell button;
    private Cell savedTable;
    private boolean enableSkip = false;

    public ModFinishDialog() {
        super(Core.bundle.get("erui.welcomepage"), Core.scene.getStyle(DialogStyle.class), false);
    }
}
