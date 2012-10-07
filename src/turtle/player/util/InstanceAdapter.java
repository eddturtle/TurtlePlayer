package turtle.player.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.Instance;
import turtle.player.presentation.InstanceFormatter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class InstanceAdapter extends ArrayAdapter<Instance>
{
    final InstanceFormatter formatter;

    /**
     * @param context
     * @param instances
     * @param formatter
     * @param comparator if null, {@link FormattedInstanceComparator} is used
     */
    public InstanceAdapter(
            Context context,
            Set<? extends Instance> instances,
            InstanceFormatter formatter,
            Comparator<Instance> comparator)
    {
        super(context, R.layout.file_list_entry,
                getSortedInstances(instances, comparator == null ?
                        new FormattedInstanceComparator(formatter) : comparator));

        this.formatter = formatter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.file_list_entry, parent, false);

        Instance currInstance = getItem(position);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(currInstance.accept(formatter));

        return rowView;
    }

    private static Instance[] getSortedInstances(Set<? extends Instance> instances, Comparator<Instance> comparator)
    {
        Instance[] sortedInstances = instances.toArray(new Instance[instances.size()]);
        Arrays.sort(sortedInstances, comparator);
        return sortedInstances;
    }
}
