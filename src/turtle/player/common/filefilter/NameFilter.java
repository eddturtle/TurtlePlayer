package turtle.player.common.filefilter;

import java.io.File;

public class NameFilter extends  AccessableFileFilter
{
    final String[] patterns;

    public NameFilter(String... patterns)
    {
        this.patterns = patterns;
    }

    @Override
    public boolean accept(File dir, String name)
    {
        if(super.accept(dir, name))
        {
            for(String pattern : patterns)
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
