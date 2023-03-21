package malilib.gui.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import malilib.MaLiLib;
import malilib.config.value.HudAlignment;
import malilib.gui.BaseScreen;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.listener.EventListener;
import malilib.util.game.wrap.GameUtils;

public class GuiUtils
{
    private static final Set<String> LINK_PROTOCOLS = Sets.newHashSet("http", "https");

    public static int getScaledWindowWidth()
    {
        return GameUtils.getClient().getWindow().getGuiScaledWidth();
    }

    public static int getScaledWindowHeight()
    {
        return GameUtils.getClient().getWindow().getGuiScaledHeight();
    }

    public static int getVanillaScreenScale()
    {
        Minecraft mc = GameUtils.getClient();
        int scale = Math.min(getDisplayWidth() / 320, getDisplayHeight() / 240);
        scale = Math.min(scale, GameUtils.getVanillaOptionsScreenScale());
        scale = Math.max(scale, 1);

        if (mc.isEnforceUnicode() && (scale & 0x1) != 0 && scale > 1)
        {
            scale -= 1;
        }

        return scale;
    }

    public static int getDisplayWidth()
    {
        return GameUtils.getClient().getWindow().getScreenWidth();
    }

    public static int getDisplayHeight()
    {
        return GameUtils.getClient().getWindow().getScreenHeight();
    }

    public static int getMouseScreenX(int screenWidth)
    {
        Minecraft mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.xpos() * (double) screenWidth / (double) window.getScreenWidth());
    }

    public static int getMouseScreenY(int screenHeight)
    {
        Minecraft mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.ypos() * (double) screenHeight / (double) window.getScreenHeight());
    }

    public static int getMouseScreenX()
    {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
    }

    public static int getMouseScreenY()
    {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
    }

    @Nullable
    public static Screen getCurrentScreen()
    {
        return GameUtils.getClient().screen;
    }

    @Nullable
    public static <T> T getCurrentScreenIfMatches(Class<T> clazz)
    {
        Screen screen = getCurrentScreen();

        if (clazz.isAssignableFrom(screen.getClass()))
        {
            return clazz.cast(screen);
        }

        return null;
    }

    public static void reInitCurrentScreen()
    {
        Screen screen = getCurrentScreen();

        if (screen != null)
        {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();
            screen.init(mc, window.getGuiScaledWidth(), window.getGuiScaledHeight());
        }
    }

    public static boolean isMouseInRegion(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, Player player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<MobEffectInstance> effects = player.getActiveEffects();

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (MobEffectInstance effect : effects)
                {
                    MobEffect potion = effect.getEffect();

                    if (effect.isVisible() && effect.showIcon())
                    {
                        if (potion.isBeneficial())
                        {
                            y1 = 26;
                        }
                        else
                        {
                            y2 = 52;
                            break;
                        }
                    }
                }

                return (int) (Math.max(y1, y2) / scale);
            }
        }

        return 0;
    }

    public static int getHudPosY(int yOrig, int yOffset, int contentHeight, double scale, HudAlignment alignment)
    {
        int scaledHeight = GuiUtils.getScaledWindowHeight();
        int posY = yOrig;

        if (alignment == HudAlignment.BOTTOM_LEFT || alignment == HudAlignment.BOTTOM_RIGHT)
        {
            posY = (int) ((scaledHeight / scale) - contentHeight - yOffset);
        }
        else if (alignment == HudAlignment.CENTER)
        {
            posY = (int) ((scaledHeight / scale / 2.0d) - (contentHeight / 2.0d) + yOffset);
        }

        return posY;
    }

    public static boolean changeTextFieldFocus(List<BaseTextFieldWidget> textFields, boolean reverse)
    {
        final int size = textFields.size();

        if (size > 1)
        {
            int currentIndex = -1;

            for (int i = 0; i < size; ++i)
            {
                BaseTextFieldWidget textField = textFields.get(i);

                if (textField.isFocused())
                {
                    currentIndex = i;
                    textField.setFocused(false);
                    break;
                }
            }

            if (currentIndex != -1)
            {
                int count = size - 1;
                int newIndex = currentIndex + (reverse ? -1 : 1);

                for (int i = 0; i < count; ++i)
                {
                    if (newIndex >= size)
                    {
                        newIndex = 0;
                    }
                    else if (newIndex < 0)
                    {
                        newIndex = size - 1;
                    }

                    BaseTextFieldWidget textField = textFields.get(newIndex);

                    if (textField.isEnabled())
                    {
                        textField.setFocused(true);
                        return true;
                    }

                    newIndex += (reverse ? -1 : 1);
                }
            }
        }

        return false;
    }

    /**
     * Creates a click handler that opens the given URL via the vanilla OPEN_LINK
     * text component click handling.
     */
    public static EventListener createLabelClickHandlerForInfoUrl(String urlString)
    {
        return () -> tryOpenLink(urlString);
    }

    public static void tryOpenLink(String urlString)
    {
        try
        {
            URI uri = new URI(urlString);
            String s = uri.getScheme();

            if (s == null)
            {
                throw new URISyntaxException(urlString, "Missing protocol");
            }

            if (LINK_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT)) == false)
            {
                throw new URISyntaxException(urlString, "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
            }

            final Screen currentScreen = getCurrentScreen();

            if (GameUtils.getOptions().getChatLinksPrompt().getValue())
            {
                BaseScreen.openScreen(new ConfirmLinkScreen((result) -> {
                    if (result)
                        openWebLink(uri);
                    else
                        BaseScreen.openScreen(currentScreen);
                    }, urlString, false));
            }
            else
            {
                openWebLink(uri);
            }
        }
        catch (URISyntaxException urisyntaxexception)
        {
            MaLiLib.LOGGER.error("Can't open url for {}", urlString, urisyntaxexception);
        }
    }

    public static boolean openWebLink(URI uri)
    {
        try
        {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("browse", URI.class).invoke(object, uri);
            return true;
        }
        catch (Throwable t)
        {
            Throwable throwable = t.getCause();
            MaLiLib.LOGGER.error("Couldn't open link: {}", (throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
            return false;
        }
    }
}
