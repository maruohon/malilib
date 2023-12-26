package malilib.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import malilib.MaLiLibConfigScreen;
import malilib.gui.BaseScreen;

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