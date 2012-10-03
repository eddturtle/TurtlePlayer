package turtle.player.common.filefilter;


import java.io.File;
import java.io.FilenameFilter;

public abstract class AccessableFileFilter implements FilenameFilter
{
    @Override
    public boolean accept(File dir, String filename)
    {
        return true; //TODO; accept readable
    }
}
