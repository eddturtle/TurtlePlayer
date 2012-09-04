package turtle.player.dirchooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import turtle.player.R;

import java.io.File;


public class FileAdapter extends ArrayAdapter<File> {

    public final FileSorter fileSorter = new FileSorter();

    public FileAdapter(Context context, File[] files) {
        super(context, R.layout.file_list_entry, files);
        sort(fileSorter);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.file_list_entry, parent, false);

        File currFile = getItem(position);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(currFile.getName());

        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        if(currFile.isDirectory()){
            icon.setImageResource(R.drawable.folder48);
        }else{
            icon.setImageResource(android.R.color.transparent);
        }

        return rowView;
    }
}
