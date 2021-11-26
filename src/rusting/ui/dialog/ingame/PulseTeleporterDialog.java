package rusting.ui.dialog.ingame;

import arc.Core;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.world.blocks.pulse.utility.PulseTeleporterController.PulseTeleporterControllerBuild;

public class PulseTeleporterDialog extends CustomBaseDialog {

    public PulseTeleporterDialog() {
        super(Core.bundle.get("erui.pulseblockpage"), Core.scene.getStyle(DialogStyle.class));
        addCloseButton();
    }

    public void show(PulseTeleporterControllerBuild building){

    }

}
