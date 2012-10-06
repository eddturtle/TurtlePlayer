package turtle.player.common.filefilter;

import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public interface FileFilters
{
    final static FilenameFilter PLAYABLE_FILES_FILTER = new ExtensionsFilter(".mp3", ".ogg");

    final static List<? extends FilenameFilter> folderArtFilters = Arrays.asList(
            new NameFilter("folder.jpg"),
            new ExtensionsFilter(".jpg", ".gif")
    );
}
