package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindProvider;

public class MaLiLibKeyBindProvider implements KeyBindProvider
{
    public static final MaLiLibKeyBindProvider INSTANCE = new MaLiLibKeyBindProvider();

    private MaLiLibKeyBindProvider()
    {
    }

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return ImmutableList.of(MaLiLibConfigs.Debug.GUI_DEBUG_KEY, MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS);
    }

    @Override
    public List<KeyBindCategory> getHotkeyCategoriesForCombinedView()
    {
        String modId = MaLiLibReference.MOD_ID;
        String modName = MaLiLibReference.MOD_NAME;

        return ImmutableList.of(
                new KeyBindCategory(modId, modName, "malilib.hotkeys.category.debug_hotkeys"  , ImmutableList.of(MaLiLibConfigs.Debug.GUI_DEBUG_KEY)),
                new KeyBindCategory(modId, modName, "malilib.hotkeys.category.generic_hotkeys", ImmutableList.of(MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS)));
    }
}
