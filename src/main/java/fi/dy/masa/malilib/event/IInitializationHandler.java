package fi.dy.masa.malilib.event;

public interface IInitializationHandler
{
    /**
     * This method will be called for any registered {@link IInitializationHandler}
     * when the game has been initialized and the mods can register their keybinds and configs
     * to malilib without causing class loading issues.
     * <br><br>
     * So call all your (malilib-facing) mod init stuff inside this handler!
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.InitializationDispatcher}
     */
    void registerModHandlers();
}
