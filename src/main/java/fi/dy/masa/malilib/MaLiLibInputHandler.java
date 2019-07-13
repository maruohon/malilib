package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;

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
    public List<? extends IHotkey> getAllHotkeys()
    {
        return ImmutableList.of(MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS);
    }

    @Override
    public List<KeybindCategory> getHotkeyCategoriesForCombinedView()
    {
        return ImmutableList.of(new KeybindCategory(MaLiLibReference.MOD_NAME, "malilib.hotkeys.category.generic_hotkeys", this.getAllHotkeys()));
    }
}
