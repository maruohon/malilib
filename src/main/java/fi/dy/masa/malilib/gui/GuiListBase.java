package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;

public abstract class GuiListBase<TYPE, WIDGET extends WidgetBase, WIDGETLIST extends WidgetListBase<TYPE, WIDGET>> extends GuiBase
{
    private final int listX;
    private final int listY;
    private WIDGETLIST widget;

    protected GuiListBase(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected abstract WIDGETLIST createListWidget(int listX, int listY);

    protected abstract int getBrowserWidth();

    protected abstract int getBrowserHeight();

    @Nullable
    protected ISelectionListener<TYPE> getSelectionListener()
    {
        return null;
    }

    protected WIDGETLIST getListWidget()
    {
        if (this.widget == null)
        {
            this.reCreateListWidget();
        }

        return this.widget;
    }

    protected void reCreateListWidget()
    {
        this.widget = this.createListWidget(this.listX, this.listY);
    }

    @Override
    public GuiBase setParent(Gui parent)
    {
        return super.setParent(parent);
    }

    @Override
    public void onInitialized()
    {
        super.onInitialized();

        this.getListWidget().setSize(this.getBrowserWidth(), this.getBrowserHeight());
        this.getListWidget().onInitialized();
    }

    @Override
    public void onClosed()
    {
        super.onClosed();

        this.getListWidget().onClosed();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getListWidget().onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, int mouseWheelDelta)
    {
        if (this.getListWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.getListWidget().onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }

    @Override
    public void initialize(MinecraftClient mc, int width, int height)
    {
        super.initialize(mc, width, height);

        this.getListWidget().initialize(mc, width, height);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        this.getListWidget().drawContents(mouseX, mouseY, partialTicks);
    }
}
