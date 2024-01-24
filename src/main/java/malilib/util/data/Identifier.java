package malilib.util.data;

/**
 * This is a simple dummy "wrapper" class that just extends the vanilla ResourceLocation/Identifier class.
 * The sole purpose of this class is to hide that mapping difference from lots of the places in
 * the mods' code, where an identifier is needed.
 */
public class Identifier
{
    protected final String namespace;
    protected final String value;

    public Identifier(String resourceName)
    {
        this("minecraft", resourceName);
    }

    public Identifier(String namespaceIn, String pathIn)
    {
        this.namespace = namespaceIn;
        this.value = pathIn;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}

        Identifier that = (Identifier) o;

        if (!this.namespace.equals(that.namespace)) {return false;}
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode()
    {
        int result = this.namespace.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}
