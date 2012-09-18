package turtle.player.model;

public interface InstanceVisitor<R>
{
    R visit(Track track);

    R visit(Album album);

    R visit(Artist artist);
}
