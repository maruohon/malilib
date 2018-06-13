package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class HoverInfo
{
    private final List<String> lines;
    private int x;
    private int y;
    private int width;
    private int height;

    public HoverInfo(int x, int y, int width, int height)
    {
        this.lines = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    /**
     * Adds the provided lines to the list.
     * The strings will be split into separate lines from any "\n" sequences.
     * @param lines
     */
    public void addLines(String... lines)
    {
        for (String line : lines)
        {
            line = I18n.format(line);
            String[] split = line.split("\n");

            for (String str : split)
            {
                this.lines.add(str);
            }
        }
    }

    public List<String> getLines()
    {
        return this.lines;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX <= (this.x + this.width) && mouseY >= this.y && mouseY <= (this.y + this.height);
    }
}
