package fi.dy.masa.malilib.config.value;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteList
{
    protected final ListType type;
    protected final ImmutableList<String> blackList;
    protected final ImmutableList<String> whiteList;

    public BlackWhiteList(ListType type, ImmutableList<String> blackList, ImmutableList<String> whiteList)
    {
        this.type = type;
        this.blackList = blackList;
        this.whiteList = whiteList;
    }

    public ListType getListType()
    {
        return this.type;
    }

    public ImmutableList<String> getBlackList()
    {
        return blackList;
    }

    public ImmutableList<String> getWhiteList()
    {
        return whiteList;
    }

    @Nullable
    public ImmutableList<String> getActiveList()
    {
        if (this.type == ListType.BLACKLIST)
        {
            return this.blackList;
        }
        else if (this.type == ListType.WHITELIST)
        {
            return this.whiteList;
        }

        return null;
    }

    public static BlackWhiteList of(ListType type, ImmutableList<String> blackList, ImmutableList<String> whiteList)
    {
        return new BlackWhiteList(type, blackList, whiteList);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        BlackWhiteList that = (BlackWhiteList) o;

        if (type != that.type) { return false; }
        if (blackList != null ? !blackList.equals(that.blackList) : that.blackList != null) { return false; }
        return whiteList != null ? whiteList.equals(that.whiteList) : that.whiteList == null;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (blackList != null ? blackList.hashCode() : 0);
        result = 31 * result + (whiteList != null ? whiteList.hashCode() : 0);
        return result;
    }
}
