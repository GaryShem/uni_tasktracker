package com.warriorrat.roommateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class BillListAdapter extends ArrayAdapter<Bill> {

    private class BillViewHolder {
        TextView description;
        TextView billSum;
        CheckBox completedCheckBox;
    }

    public BillListAdapter(Context context, ArrayList<Bill> bills) {
        super(context, 0, bills);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        BillViewHolder viewHolder = null;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bill_list_item, parent, false);
            viewHolder = new BillViewHolder();
            viewHolder.billSum = (TextView)listItemView.findViewById(R.id.bill_sum);
            viewHolder.description = (TextView)listItemView.findViewById(R.id.bill_description);
            viewHolder.completedCheckBox = (CheckBox) listItemView.findViewById(R.id.completed_btn);
            listItemView.setTag(viewHolder);
        }

        if (viewHolder == null) {
            viewHolder = (BillViewHolder)listItemView.getTag();
        }

        final Bill currentBill = getItem(position);
        viewHolder.description.setText(currentBill.getDescription());
        viewHolder.billSum.setText(String.format(Locale.US, "%.2f", currentBill.getAmount()));

        final CheckBox completedCheckBox = viewHolder.completedCheckBox;
        completedCheckBox.setChecked(currentBill.isCompleted());

        completedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBill.setCompleted(completedCheckBox.isChecked());
                currentBill.updateTime();
                BillsActivity.pushBillUpdate(currentBill);
            }
        });

        return listItemView;
    }
}
