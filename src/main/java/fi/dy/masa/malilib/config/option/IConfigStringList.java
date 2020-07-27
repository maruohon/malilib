package fi.dy.masa.malilib.config.option;

import java.util.List;
import com.google.common.collect.ImmutableList;

public interface IConfigStringList extends IConfigBase
{
    ImmutableList<String> getStrings();

    ImmutableList<String> getDefaultStrings();

    void setStrings(List<String> strings);
}
