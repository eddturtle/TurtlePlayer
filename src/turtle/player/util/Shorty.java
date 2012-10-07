package turtle.player.util;

public class Shorty
{
    public static boolean isVoid(String string)
    {
        return string == null || string.length() == 0;
    }

    public static boolean isVoid(Integer integer)
    {
        return integer == null || integer == 0;
    }

    public static String avoidNull(String s){
        return isVoid(s) ? "" : s;
    }
}
