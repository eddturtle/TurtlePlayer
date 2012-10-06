package turtle.player.model;

import java.util.List;
import java.util.Set;

public interface Instance
{
    Set<? extends Instance> getChilds(Set<Track> tracks);

    <R> R accept(InstanceVisitor<R> visitor);
}
