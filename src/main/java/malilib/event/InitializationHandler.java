package malilib.event;

public interface InitializationHandler extends PrioritizedEventHandler
{
    /**
     * This method will be called for any registered {@link InitializationHandler}
     * when the game has been initialized and the mods can register their keybinds and configs
     * to malilib without causing class loading issues.
     * <br><br>
     * So call all your (malilib-facing) mod init stuff inside this handler!
     * <br><br>
     * The classes implementing this method should be registered to {@link malilib.event.dispatch.InitializationDispatcherImpl}
     */
    void registerModHandlers();
}
