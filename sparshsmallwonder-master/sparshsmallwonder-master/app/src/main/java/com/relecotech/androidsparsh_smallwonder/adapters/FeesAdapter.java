package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.FeesInformationListData;

import java.util.Date;
import java.util.List;

public class FeesAdapter extends BaseAdapter {

    protected List<FeesInformationListData> feesList;
    Context context;
    LayoutInflater inflater;
    private String check_fees_dueDate;
    private Date fees_dueDate;
    private boolean getDueDateResult;
    private String check_fees_status;

    public FeesAdapter(Context context, List<FeesInformationListData> feesList) {
        this.feesList = feesList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return feesList.size();
    }

    public FeesInformationListData getItem(int position) {
        return feesList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.fees_list_item, parent, false);


            holder.feesListItemLayout = (LinearLayout) convertView.findViewById(R.id.fees_list_item_layout);
            holder.txtFeesDate = (TextView) convertView.findViewById(R.id.feesDate);
            holder.txtFeesAmount = (TextView) convertView.findViewById(R.id.feesAmount);
            holder.txtFeesStatus = (TextView) convertView.findViewById(R.id.feesStatus);
            holder.txtFeeTitle = (TextView) convertView.findViewById(R.id.feeTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FeesInformationListData feesInformationListData = feesList.get(position);


        holder.txtFeesDate.setTextColor(Color.WHITE);
        holder.txtFeesAmount.setTextColor(Color.WHITE);
        holder.txtFeesStatus.setTextColor(Color.WHITE);
        holder.txtFeeTitle.setTextColor(Color.WHITE);

        holder.txtFeesDate.setText(feesInformationListData.getFees_due_date());
        holder.txtFeesAmount.setText("₹ " + feesInformationListData.getFees_amount_payable());
        holder.txtFeesStatus.setText(feesInformationListData.getFees_status());
        holder.txtFeeTitle.setText(feesInformationListData.getFees_title());


        if (feesInformationListData.getFees_status().contains("Unpaid")) {
            System.out.println("Unpaid------------------------------- color change if");
            holder.feesListItemLayout.setBackgroundColor(Color.parseColor("#FFD64537"));
        } else if (feesInformationListData.getFees_status().contains("Paid")) {
            System.out.println("Unpaid------------------------------- color change else");
            holder.feesListItemLayout.setBackgroundColor(Color.parseColor("#afafaf"));

        }


        return convertView;
    }


    private class ViewHolder {
        TextView txtFeesDate;
        TextView txtFeesAmount;
        TextView txtFeesStatus;
        TextView txtFeeTitle;
        LinearLayout feesListItemLayout;

    }

}
