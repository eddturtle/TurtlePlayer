package turtle.player.common.filefilter;

import java.io.File;

public class NameFilter extends  AccessableFileFilter
{
    final String[] names;

    public NameFilter(String... names)
    {
        this.names = names;
    }

    @Override
    public boolean accept(File dir, String pattern)
    {
        if(super.accept(dir, pattern))
        {
            for(String name : names)
            {
                if(name.toLowerCase().matches(pattern))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
