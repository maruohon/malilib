package malilib.util.data;

/**
 * This is a simple dummy "wrapper" class that just extends the vanilla ResourceLocation/Identifier class.
 * The sole purpose of this class is to hide that mapping difference from lots of the places in
 * the mods' code, where an identifier is needed.
 */
public class Identifier
{
    protected final String namespace;
    protected final String path;

    public Identifier(String resourceName)
    {
        int colonIndex = resourceName.indexOf(':');

        if (colonIndex >= 0)
        {
            this.namespace = resourceName.substring(0, colonIndex);
            this.path = resourceName.substring(colonIndex + 1);
        }
        else
        {
            this.namespace = "minecraft";
            this.path = resourceName;
        }
    }

    public Identifier(String namespaceIn, String pathIn)
    {
        this.namespace = namespaceIn;
        this.path = pathIn;
    }

    public String getNamespace()
    {
        return this.namespace;
    }

    public String getPath()
    {
        return this.path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}

        Identifier that = (Identifier) o;

        if (!this.namespace.equals(that.namespace)) {return false;}
        return this.path.equals(that.path);
    }

    @Override
    public int hashCode()
    {
        int result = this.namespace.hashCode();
        result = 31 * result + this.path.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return this.namespace + ":" + this.path;
    }
}
