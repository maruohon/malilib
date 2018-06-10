package fi.dy.masa.malilib.hotkeys;

public interface IKeyEventHandler
{
    /**
     * Called on keyboard events with the key and whether the key was pressed or released.
     * @param keyCode
     * @param keyDown
     * @return true if further processing of this key event should be cancelled
     */
    boolean onKeyInput(int keyCode, boolean keyDown);
}
