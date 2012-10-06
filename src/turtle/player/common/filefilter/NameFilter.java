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
    public boolean accept(File dir, String filename)
    {
        if(super.accept(dir, filename))
        {
            for(String name : names)
            {
                if(name.equals(filename))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
