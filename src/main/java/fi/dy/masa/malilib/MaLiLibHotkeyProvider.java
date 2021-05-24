package fi.dy.masa.malilib;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import fi.dy.masa.malilib.util.ListUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibHotkeyProvider implements HotkeyProvider
{
    public static final MaLiLibHotkeyProvider INSTANCE = new MaLiLibHotkeyProvider();

    private MaLiLibHotkeyProvider()
    {
    }

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return ListUtils.getAppendedList(MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS,
                                         Collections.singletonList(MaLiLibConfigs.Debug.GUI_DEBUG_KEY));
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        ModInfo modInfo = MaLiLibReference.MOD_INFO;

        return ImmutableList.of(
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.debug_hotkeys"  , ImmutableList.of(MaLiLibConfigs.Debug.GUI_DEBUG_KEY)),
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.generic_hotkeys", MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS));
    }
}
