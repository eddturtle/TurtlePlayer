package turtle.player.util;

import turtle.player.model.Instance;
import turtle.player.presentation.InstanceFormatter;

import java.text.Collator;
import java.util.Comparator;

public class FormattedInstanceComparator implements Comparator<Instance>
{
    private final InstanceFormatter listInstanceFormatter;

    public FormattedInstanceComparator(InstanceFormatter listInstanceFormatter)
    {
        this.listInstanceFormatter = listInstanceFormatter;
    }

    @Override
    public int compare(Instance lhs, Instance rhs)
    {
        return Collator.getInstance().compare(lhs.accept(listInstanceFormatter), rhs.accept(listInstanceFormatter));
    }


}
