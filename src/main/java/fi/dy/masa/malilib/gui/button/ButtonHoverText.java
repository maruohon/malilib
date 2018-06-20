package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.IGuiIcon;
import net.minecraft.client.resources.I18n;

public class ButtonHoverText extends ButtonGeneric
{
    protected List<String> hoverStrings = new ArrayList<>();

    public ButtonHoverText(int id, int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(id, x, y, width, height, text, null, hoverStrings);
    }

    public ButtonHoverText(int id, int x, int y, int width, int height, String text, IGuiIcon icon, String... hoverStrings)
    {
        super(id, x, y, width, height, text, icon);

        for (String str : hoverStrings)
        {
            String[] parts = str.split("\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(I18n.format(part));
            }
        }
    }

    public List<String> getHoverStrings()
    {
        return this.hoverStrings;
    }
}
