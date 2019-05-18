package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

public class MaLiLibInputHandler implements IKeybindProvider
{
    private static final MaLiLibInputHandler INSTANCE = new MaLiLibInputHandler();

    private MaLiLibInputHandler()
    {
        super();
    }

    public static MaLiLibInputHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        manager.addKeybindToMap(MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind());
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        List<? extends IHotkey> hotkeys = ImmutableList.of( MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS );
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, "malilib.hotkeys.category.generic_hotkeys", hotkeys);
    }
}
