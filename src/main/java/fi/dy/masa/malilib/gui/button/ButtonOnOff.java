package fi.dy.masa.malilib.gui.button;

import net.minecraft.client.resources.I18n;

public class ButtonOnOff extends ButtonGeneric
{
    protected final String translationKey;

    /**
     * Pass -1 as the <b>width</b> to automatically set the width
     * to a value where the ON and OFF buttons are the same width,
     * using the given translation key.
     * @param x
     * @param y
     * @param width
     * @param rightAlign
     * @param translationKey
     * @param isCurrentlyOn
     * @param hoverStrings
     */
    public ButtonOnOff(int x, int y, int width, boolean rightAlign, String translationKey, boolean isCurrentlyOn, String... hoverStrings)
    {
        super(x, y, width, 20, "", hoverStrings);

        this.translationKey = translationKey;
        this.updateDisplayString(isCurrentlyOn);

        if (width < 0)
        {
            int w1 = this.getStringWidth(ButtonOnOff.getDisplayStringForStatus(translationKey, true));
            int w2 = this.getStringWidth(ButtonOnOff.getDisplayStringForStatus(translationKey, false));
            this.width = Math.max(w1, w2) + 10;
        }

        if (rightAlign)
        {
            this.x = x - this.width;
        }
    }

    public void updateDisplayString(boolean isCurrentlyOn)
    {
        this.displayString = getDisplayStringForStatus(this.translationKey, isCurrentlyOn);
    }

    public static String getDisplayStringForStatus(String translationKey, boolean isCurrentlyOn)
    {
        String strStatus = "malilib.gui.label_colored." + (isCurrentlyOn ? "on" : "off");
        return I18n.format(translationKey, I18n.format(strStatus));
    }
}
