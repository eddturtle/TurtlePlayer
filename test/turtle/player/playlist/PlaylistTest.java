package turtle.player.playlist;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * author: Hoene84 - Simon Honegger
 */

public class PlaylistTest
{
    @Test
    public void testParseTrackNumber() throws Exception
    {
        assertEquals(1, Playlist.parseTrackNumber("1"));
        assertEquals(1, Playlist.parseTrackNumber("01"));
        assertEquals(1, Playlist.parseTrackNumber("1/2"));
        assertEquals(1, Playlist.parseTrackNumber("1,2"));
        assertEquals(1, Playlist.parseTrackNumber("1;2"));
        assertEquals(0, Playlist.parseTrackNumber("00"));
        assertEquals(0, Playlist.parseTrackNumber("0"));
        assertEquals(0, Playlist.parseTrackNumber("0/0"));
        assertEquals(0, Playlist.parseTrackNumber(""));
        assertEquals(0, Playlist.parseTrackNumber("asdad"));
        assertEquals(10, Playlist.parseTrackNumber("10"));
        assertEquals(10, Playlist.parseTrackNumber("010"));
    }
}
