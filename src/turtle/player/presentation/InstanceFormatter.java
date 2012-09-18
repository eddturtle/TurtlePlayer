package turtle.player.presentation;

import turtle.player.model.InstanceVisitor;

public abstract class InstanceFormatter implements InstanceVisitor<String>
{
    public final static InstanceFormatter SHORT = new ShortInstanceFormatter();
    public final static InstanceFormatter LIST = new ListInstanceFormatter();
}
