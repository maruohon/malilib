package malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.input.Hotkey;
import malilib.input.HotkeyCategory;
import malilib.input.HotkeyProvider;
import malilib.util.ListUtils;
import malilib.util.data.ModInfo;

public class MaLiLibHotkeyProvider implements HotkeyProvider
{
    static final MaLiLibHotkeyProvider INSTANCE = new MaLiLibHotkeyProvider();

    private MaLiLibHotkeyProvider()
    {
    }

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return ListUtils.getAppendedList(MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS,
                                         MaLiLibConfigs.Debug.HOTKEYS);
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        ModInfo modInfo = MaLiLibReference.MOD_INFO;

        return ImmutableList.of(
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.debug_hotkeys"  , MaLiLibConfigs.Debug.HOTKEYS),
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.generic_hotkeys", MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS));
    }
}
