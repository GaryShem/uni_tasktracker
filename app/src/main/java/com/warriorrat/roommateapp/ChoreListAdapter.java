package com.warriorrat.roommateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ChoreListAdapter extends ArrayAdapter<Chore> {

    private class ChoreViewHolder {
        TextView description;
        CheckBox completedCheckBox;
    }

    public ChoreListAdapter(Context context, ArrayList<Chore> chores) {
        super(context, 0, chores);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        ChoreViewHolder viewHolder = null;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.chore_list_item, parent, false);
            viewHolder = new ChoreViewHolder();
            viewHolder.description = (TextView)listItemView.findViewById(R.id.chore_description);
            viewHolder.completedCheckBox = (CheckBox) listItemView.findViewById(R.id.completed_btn);
            listItemView.setTag(viewHolder);
        }

        if (viewHolder == null) {
            viewHolder = (ChoreViewHolder)listItemView.getTag();
        }

        final Chore currentChore = getItem(position);

        TextView descriptionTextView = viewHolder.description;
        descriptionTextView.setText(currentChore.getDescription());

        final CheckBox completedCheckBox = viewHolder.completedCheckBox;
        completedCheckBox.setChecked(currentChore.isCompleted());

        completedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChore.setCompleted(completedCheckBox.isChecked());
                currentChore.updateTime();
                ChoresActivity.pushChoreUpdate(currentChore);
            }
        });

        return listItemView;
    }
}
