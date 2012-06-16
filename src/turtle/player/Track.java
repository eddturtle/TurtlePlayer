/*
 * TURTLE PLAYER
 */

// Package
package turtle.player;

// Import - Java
import java.util.Comparator;


public class Track
{
	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	//
	private int id;
	private String title;
	private int number;
	//private String number;
	private String artist;
	private String album;
	private double length;
	private String src;
	private String rootSrc;
	private boolean hasAlbumArt;
	
	
	// ========================================= //
	// 	Constructor
	// ========================================= //
	
	public Track()
	{
		id = 0;
		title = "";
		number = 0;
		artist = "";
		album = "";
		length = 0.0;
		src = "";
	}
	
	public Track(String nTitle, int nNumber, String nArtist, String nAlbum, double nLength, String nSrc)
	{
		id = 0;
		title = nTitle;
		number = nNumber;
		artist = nArtist;
		album = nAlbum;
		length = nLength;
		src = nSrc;
	}
	
	public Track(int nId, String nTitle, int nNumber, String nArtist, String nAlbum, double nLength, String nSrc)
	{
		id = nId;
		title = nTitle;
		number = nNumber;
		artist = nArtist;
		album = nAlbum;
		length = nLength;
		src = nSrc;
	}

	
	// ========================================= //
	// 	Id
	// ========================================= //
	
	public int GetId()
	{
		return id;
	}
	
	public void SetId(int nId)
	{
		id = nId;
	}
	
	
	// ========================================= //
	// 	Title
	// ========================================= //
	
	public String GetTitle()
	{
		return title;
	}
	
	public void SetTitle(String nTitle)
	{
		title = nTitle;
	}
	
	
	// ========================================= //
	// 	Number
	// ========================================= //
	
	public int GetNumber()
	{
		return number;
	}
	
	public void SetNumber(int nNumber)
	{
		number = nNumber;
	}
	
	
	// ========================================= //
	// 	Artist
	// ========================================= //
	
	public String GetArtist()
	{
		return artist;
	}
	
	public void SetArtist(String nArtist)
	{
		artist = nArtist;
	}
	
	
	// ========================================= //
	// 	Album
	// ========================================= //
	
	public String GetAlbum()
	{
		return album;
	}
	
	public void SetAlbum(String nAlbum)
	{
		album = nAlbum;
	}
	
	
	// ========================================= //
	// 	Length
	// ========================================= //
	
	public double GetLength()
	{
		return length;
	}
	
	public void SetLength(double nLength)
	{
		length = nLength;
	}
	
	
	// ========================================= //
	// 	Src
	// ========================================= //
	
	public String GetSrc()
	{
		return src;
	}
	
	public void SetSrc(String nSrc)
	{
		src = nSrc;
	}
	
	
	// ========================================= //
	// 	Root SRC
	// ========================================= //
	
	public String GetRootSrc()
	{
		return rootSrc;
	}
	
	public void SetRootSrc(String nRootSrc)
	{
		rootSrc = nRootSrc;
	}
	
	
	// ========================================= //
	// 	Album Art
	// ========================================= //
	
	public boolean HasAlbumArt()
	{
		return hasAlbumArt;
	}
	
	public void SetAlbumArt(boolean nAlbumArt)
	{
		hasAlbumArt = nAlbumArt;
	}
	
}


class TitleComparator implements Comparator<Track>
{
	@Override
	public int compare(Track x, Track y)
	{
		if (x != null && y != null)
		{
			String title1 = x.GetTitle();
			String title2 = y.GetTitle();
			
			if (title1 != null && title1 != "" && title2 != null && title2 != "")
			{
				return title1.compareTo(title2);				
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return 0;
		}
	}
}


class NumberComparator implements Comparator<Track>
{
	@Override
	public int compare(Track x, Track y)
	{
		if (x != null && y != null)
		{
			int num1 = x.GetNumber();
			int num2 = y.GetNumber();
			
			if (num1 == num2)
			{
				return 0;				
			}
			else if (num1 > num2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 0;
		}
	}
}


class ArtistComparator implements Comparator<Track>
{
	@Override
	public int compare(Track x, Track y)
	{
		if (x != null && y != null)
		{
			String title1 = x.GetArtist();
			String title2 = y.GetArtist();
			
			if (title1 != null && title1 != "" && title2 != null && title2 != "")
			{
				return title1.compareTo(title2);				
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return 0;
		}
	}
}


class AlbumComparator implements Comparator<Track>
{
	@Override
	public int compare(Track x, Track y)
	{
		int toReturn = 0;
		try
		{
			String a = x.GetAlbum();
			String b = y.GetAlbum();
			
			if (a == null || a == "")
			{
				a = "Unknown";
			}
			
			if (b == null || b == "")
			{
				b = "Unknown";
			}
			
			toReturn = a.compareTo(b);
		}
		catch (NullPointerException e)
		{
			// TODO Log Error
		}
		
		return toReturn;
	}
}