package turtle.player.playlist;


import java.util.ArrayList;
import java.util.Collection;

public class LimitedStack<T> extends ArrayList<T> {

    private final int maxSize;
    private final double clearPercent = 0.1;

    public LimitedStack(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T object)
    {
        boolean returnValue = super.add(object);
        keepMaxSize();
        return returnValue;
    }

    @Override
    public void add(int index, T object)
    {
        super.add(index, object);
        keepMaxSize();
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        boolean returnValue = super.addAll(collection);    //To change body of overridden methods use File | Settings | File Templates.
        keepMaxSize();
        return returnValue;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        boolean returnValue = super.addAll(index, collection);    //To change body of overridden methods use File | Settings | File Templates.
        keepMaxSize();
        return returnValue;
    }

    /**
     * @return last addded item. Dont call when empty
     */
    public T pop(){
        T poppedipop = get(size() - 1);
        remove(size() - 1);
        return poppedipop;
    }

    private void keepMaxSize()
    {
        if(size() > maxSize)
        {
            subList((int)(maxSize * clearPercent), size());
        }
    }
}
