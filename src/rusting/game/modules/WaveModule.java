package rusting.game.modules;

public abstract class WaveModule {
    //Turn this off to enable custom waves/signify that custom waves are present
    public boolean vanilla = true;

    public abstract void wave();
}
