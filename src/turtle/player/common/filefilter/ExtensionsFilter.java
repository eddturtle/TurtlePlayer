package turtle.player.common.filefilter;

import java.io.File;

public class ExtensionsFilter extends  AccessableFileFilter
{
    final String[] extensions;

    public ExtensionsFilter(String... extensions)
    {
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File dir, String filename)
    {
        if(super.accept(dir, filename))
        {
            for(String extension : extensions)
            {
                if(filename.endsWith(extension))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
