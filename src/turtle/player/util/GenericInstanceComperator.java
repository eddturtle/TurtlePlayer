package turtle.player.util;

import turtle.player.model.*;

import java.text.Collator;
import java.util.Comparator;

public class GenericInstanceComperator implements Comparator<Instance>
{
    @Override
    public int compare(Instance lhs, final Instance rhs)
    {
        return lhs.accept(new InstanceVisitor<Integer>()
        {
            @Override
            public Integer visit(final Track trackLhs)
            {
                return rhs.accept(new InstanceVisitor<Integer>()
                {
                    @Override
                    public Integer visit(Track trackRhs)
                    {
                        int ret = compare(trackLhs.GetArtist().getName(), trackRhs.GetArtist().getName());
                        if(ret == 0)
                        {
                            ret = compare(trackLhs.GetAlbum().getName(), trackRhs.GetAlbum().getName());
                        }
                        if(ret == 0)
                        {
                            ret = compare(trackLhs.GetNumber(), trackRhs.GetNumber());
                        }
                        if(ret == 0)
                        {
                            ret = compare(trackLhs.GetTitle(), trackRhs.GetTitle());
                        }
                        if(ret == 0)
                        {
                            ret = compare(trackLhs.GetLength(), trackRhs.GetLength());
                        }
                        return ret;
                    }

                    @Override
                    public Integer visit(Album albumRhs)
                    {
                        return 1;
                    }

                    @Override
                    public Integer visit(Artist artistRhs)
                    {
                        return 1;
                    }
                });
            }

            @Override
            public Integer visit(final Album albumLhs)
            {
                return rhs.accept(new InstanceVisitor<Integer>()
                {
                    @Override
                    public Integer visit(Track trackRhs)
                    {
                        return -1;
                    }

                    @Override
                    public Integer visit(Album albumRhs)
                    {
                        return compare(albumLhs.getName(), albumRhs.getName());
                    }

                    @Override
                    public Integer visit(Artist artistRhs)
                    {
                        return 1;
                    }
                });
            }

            @Override
            public Integer visit(final Artist artistLhs)
            {
                return rhs.accept(new InstanceVisitor<Integer>()
                {
                    @Override
                    public Integer visit(Track trackRhs)
                    {
                        return -1;
                    }

                    @Override
                    public Integer visit(Album albumRhs)
                    {
                        return -1;
                    }

                    @Override
                    public Integer visit(Artist artistRhs)
                    {
                        return compare(artistLhs.getName(), artistRhs.getName());
                    }
                });
            }
        });
    }

    private int compare(String lhs, String rhs)
    {
        if(lhs == null)
        {
            if(rhs == null)
            {
                return 0;
            }
            return 1;
        }else{
            if(rhs == null)
            {
                return -1;
            }
        }

        return Collator.getInstance().compare(lhs, rhs);
    }

    private <T> int compare(Comparable<T> lhs, T rhs)
    {
        if(lhs == null)
        {
            if(rhs == null)
            {
                return 0;
            }
            return 1;
        }else{
            if(rhs == null)
            {
                return -1;
            }
        }

        return lhs.compareTo(rhs);
    }
}
