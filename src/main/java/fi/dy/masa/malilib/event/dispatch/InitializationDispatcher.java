package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.IInitializationHandler;

public class InitializationDispatcher implements IInitializationDispatcher
{
    public static final IInitializationDispatcher INSTANCE = new InitializationDispatcher();

    private final List<IInitializationHandler> handlers = new ArrayList<>();

    @Override
    public void registerInitializationHandler(IInitializationHandler handler)
    {
        if (this.handlers.contains(handler) == false)
        {
            this.handlers.add(handler);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onGameInitDone()
    {
        if (this.handlers.isEmpty() == false)
        {
            for (IInitializationHandler handler : this.handlers)
            {
                handler.registerModHandlers();
            }
        }

        ((ConfigManagerImpl) ConfigManager.INSTANCE).loadAllConfigs();
        InputEventDispatcher.getKeyBindManager().updateUsedKeys();
    }
}
