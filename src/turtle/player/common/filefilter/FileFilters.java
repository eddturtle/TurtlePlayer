package turtle.player.common.filefilter;

import java.io.FilenameFilter;

public interface FileFilters
{
    final static FilenameFilter PLAYABLE_FILES_FILTER = new ExtensionsFilter(".mp3", ".ogg");
}
