package turtle.player.preferences;

public class Key<T>{

    private final String key;
    private final T defaultValue;

    Key(String key, T defaultValue)
    {
        this.defaultValue = defaultValue;
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
