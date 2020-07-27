package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBindProvider;
import fi.dy.masa.malilib.input.KeyBindCategory;

public class MaLiLibInputHandler implements IKeyBindProvider
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
    public List<? extends IHotkey> getAllHotkeys()
    {
        return ImmutableList.of(MaLiLibConfigs.Debug.GUI_DEBUG_KEY, MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS);
    }

    @Override
    public List<KeyBindCategory> getHotkeyCategoriesForCombinedView()
    {
        String mod = MaLiLibReference.MOD_NAME;

        return ImmutableList.of(
                new KeyBindCategory(mod, "malilib.hotkeys.category.debug_hotkeys"  , ImmutableList.of(MaLiLibConfigs.Debug.GUI_DEBUG_KEY)),
                new KeyBindCategory(mod, "malilib.hotkeys.category.generic_hotkeys", ImmutableList.of(MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS)));
    }
}
