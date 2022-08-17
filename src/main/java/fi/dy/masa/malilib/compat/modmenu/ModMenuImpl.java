package fi.dy.masa.malilib.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.gui.BaseScreen;

public class ModMenuImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return (currentScreen) -> {
            BaseScreen screen = MaLiLibConfigScreen.create();
            screen.setParent(currentScreen);
            return screen;
        };
    }
}
