package fi.dy.masa.malilib.gui.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.Sets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

public class GuiUtils
{
    private static final Set<String> LINK_PROTOCOLS = Sets.newHashSet("http", "https");

    public static int getVanillaScreenScale()
    {
        return GameUtils.getClient().options.getGuiScale().getValue();
    }

    public static int getScaledWindowWidth()
    {
        return GameUtils.getClient().getWindow().getScaledWidth();
    }

    public static int getScaledWindowHeight()
    {
        return GameUtils.getClient().getWindow().getScaledHeight();
    }

    public static int getDisplayWidth()
    {
        return GameUtils.getClient().getWindow().getWidth();
    }

    public static int getDisplayHeight()
    {
        return GameUtils.getClient().getWindow().getHeight();
    }

    public static int getMouseScreenX(int screenWidth)
    {
        // TODO 1.13+ port
        return getMouseScreenX();
    }

    public static int getMouseScreenY(int screenHeight)
    {
        // TODO 1.13+ port
        return getMouseScreenY();
    }

    public static int getMouseScreenX()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
    }

    public static int getMouseScreenY()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());
    }

    @Nullable
    public static Screen getCurrentScreen()
    {
        return GameUtils.getClient().currentScreen;
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
            // TODO 1.13+ port
            //screen.initGui();
        }
    }

    public static boolean isMouseInRegion(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, PlayerEntity player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<StatusEffectInstance> effects = player.getStatusEffects();

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (StatusEffectInstance effect : effects)
                {
                    StatusEffect potion = effect.getEffectType();

                    if (effect.shouldShowParticles() && effect.shouldShowIcon())
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

            if (GameUtils.getClient().options.getChatLinksPrompt().getValue())
            {
                //BaseScreen.openGui(new ConfirmActionScreen(320, "", () -> openWebLink(uri), getCurrentScreen(), ""));
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
