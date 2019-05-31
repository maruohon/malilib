package fi.dy.masa.malilib.interfaces;

public interface IInitializationHandler
{
    /**
     * This method will be called for any registered <b>IInitializationHandler</b>
     * when the game has been initialized and the mods can register their keybinds and configs
     * to malilib without causing class loading issues.
     * <br><br>
     * So call all your (malilib-facing) mod init stuff inside this handler!
     */
    void registerModHandlers();
}
