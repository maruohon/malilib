package fi.dy.masa.malilib.gui;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.client.gui.FontRenderer;

public class GuiTextFieldDouble extends GuiTextFieldGeneric
{
    private static final Pattern PATTER_NUMBER = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?");

    public GuiTextFieldDouble(int x, int y, int width, int height, FontRenderer fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

        this.setValidator(new Predicate<String>()
        {
            @Override
            public boolean test(String input)
            {
                if (input.length() > 0 && PATTER_NUMBER.matcher(input).matches() == false)
                {
                    return false;
                }

                return true;
            }
        });
    }
}
