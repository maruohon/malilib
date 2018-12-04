package fi.dy.masa.malilib.interfaces;

public interface IInitializationDispatcher
{
    /**
     * Register an initialization handler, which will get called once the game has been initialized
     * and set up, and things are ready to be accessed and initialized by mods. 
     * @param handler
     */
    void registerInitializationHandler(IInitializationHandler handler);
}
