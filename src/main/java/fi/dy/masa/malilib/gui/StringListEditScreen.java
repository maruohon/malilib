package fi.dy.masa.malilib.gui;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.config.ConfigScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.StringListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.header.StringListEditHeaderWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListEditScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final StringListConfig config;
    protected final ConfigScreen configGui;
    protected int dialogWidth;
    protected int dialogHeight;
    protected int dialogLeft;
    protected int dialogTop;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;

    public StringListEditScreen(StringListConfig config, ConfigScreen configGui,
                                @Nullable DialogHandler dialogHandler, GuiScreen parent, @Nullable EventListener saveListener)
    {
        super(0, 0);

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;
        this.title = StringUtils.translate("malilib.gui.title.string_list_edit", config.getName());

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }
    }

    protected void setWidthAndHeight()
    {
        this.dialogWidth = 400;
        this.dialogHeight = GuiUtils.getScaledWindowHeight() - 90;
    }

    protected void centerOnScreen()
    {
        if (this.getParent() != null)
        {
            this.dialogLeft = this.getParent().width / 2 - this.dialogWidth / 2;
            this.dialogTop = this.getParent().height / 2 - this.dialogHeight / 2;
        }
        else
        {
            this.dialogLeft = 20;
            this.dialogTop = 20;
        }

        this.setListPosition(this.dialogLeft + 8, this.dialogTop + 20);
    }

    @Override
    protected int getListWidth()
    {
        return this.dialogWidth - 14;
    }

    @Override
    protected int getListHeight()
    {
        return this.dialogHeight - 25;
    }

    @Override
    public void onGuiClosed()
    {
        this.config.setStrings(ImmutableList.copyOf(this.getListWidget().getCurrentEntries()));

        if (this.saveListener != null)
        {
            this.saveListener.onEvent();
        }

        super.onGuiClosed();
    }

    @Override
    protected DataListWidget<String> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<String> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight, this.config::getStrings);

        listWidget.setZLevel((int) this.zLevel + 2);
        listWidget.setListEntryWidgetFixedHeight(20);
        listWidget.addDefaultSearchBar();

        listWidget.setHeaderWidgetFactory(StringListEditHeaderWidget::new);

        listWidget.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, entry, lw) -> {
            List<String> defaultList = this.config.getDefaultStrings();
            String defaultValue = li < defaultList.size() ? defaultList.get(li) : "";
            return new StringListEditEntryWidget(wx, wy, ww, wh, li, oi, entry, defaultValue, lw);
        });

        return listWidget;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().setWorldAndResolution(mc, width, height);
        }

        this.setWidthAndHeight();
        this.centerOnScreen();

        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR, (int) this.zLevel);
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }
}
