package com.relecotech.androidsparsh_smallwonder.adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Alert;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.Listview_communicator;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;


/**
 * Created by Relecotech on 26-02-2018.
 */

public class AlertRecyclerAdapter extends RecyclerView.Adapter<AlertRecyclerAdapter.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    List<Alert> alertListData;
    private static ItemClickListener clickListener;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView alertTitleTextView;
        private TextView alertDescriptionTextView;
        private TextView alertDateTextView;
        private ImageView alertAttachmentImageView;
        private TextView submittedByTextView;
        private TextView likeCountTextView;
        private LinearLayout alertTitleLinear;
        private TextView alertDelete;
        private TextView alertEdit;

        public ViewHolder(View convertView) {
            super(convertView);
            alertTitleLinear = (LinearLayout) convertView.findViewById(R.id.AlertLinear);
            alertTitleTextView = (TextView) convertView.findViewById(R.id.alert_Title);
            alertDescriptionTextView = (TextView) convertView.findViewById(R.id.alert_Body);
            alertDateTextView = (TextView) convertView.findViewById(R.id.alert_DateTime);
            alertAttachmentImageView = (ImageView) convertView.findViewById(R.id.alertAttachmentPin);
            submittedByTextView = (TextView) convertView.findViewById(R.id.submittedByTextView);
            likeCountTextView = (TextView) convertView.findViewById(R.id.likeCountTextView);
            alertDelete = (TextView) convertView.findViewById(R.id.alert_list_item_delete);
            alertEdit = (TextView) convertView.findViewById(R.id.alert_list_item_Edit);
            convertView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());
            }
        }
    }

    public AlertRecyclerAdapter(Context context, List<Alert> alertListData) {
        this.context = context;
        this.alertListData = alertListData;
        dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
        System.out.println("setClickListener setClickListener");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Alert getAlertListObject = alertListData.get(position);
        System.out.println("getAlertListObject.getTitle() " + getAlertListObject.getTitle());
        holder.alertTitleTextView.setText(getAlertListObject.getTitle());
        holder.alertDescriptionTextView.setText(getAlertListObject.getDescription());
        holder.alertDateTextView.setText(dateFormat.format(getAlertListObject.getPostDate()));

        System.out.println("getAlertListObject.getLikeCount()  " + getAlertListObject.getLikeCount());
        holder.likeCountTextView.setText(String.valueOf(getAlertListObject.getLikeCount()));

        if (getAlertListObject.getAlert_priority().equals("Urgent")) {
            holder.alertTitleLinear.setBackgroundResource(R.color.urgentAlertColor);
        }else {
            holder.alertTitleLinear.setBackgroundResource(R.color.colorPrimary);
        }

        if (getAlertListObject.getAttachmentCount().equals("0") || getAlertListObject.getAttachmentCount().equals("null")) {
            holder.alertAttachmentImageView.setVisibility(View.INVISIBLE);
        } else {
            holder.alertAttachmentImageView.setVisibility(View.VISIBLE);
        }

        SessionManager sessionManager = new SessionManager(context, sharedPrefValue);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String userRole = null;
        try {
            userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userRole.equals("Student")) {
            holder.alertEdit.setVisibility(View.INVISIBLE);
        } else {
            holder.alertEdit.setVisibility(View.VISIBLE);
        }
        holder.alertEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Listview_communicator) context).listViewOnclick(position, 2);
            }
        });

        if (userRole.equals("Student")) {
            holder.alertDelete.setVisibility(View.INVISIBLE);
        } else {
            holder.alertDelete.setVisibility(View.VISIBLE);
        }
        holder.alertDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Listview_communicator) context).listViewOnclick(position, 1);
            }
        });

        if (userRole.equals("Student")) {
            if (getAlertListObject.getSubmitted_By_to().contains("null")) {
                holder.submittedByTextView.setText("Submitted By : Admin");
            } else {
                System.out.println(" getAlertListObject.getSubmitted_By_to() " + getAlertListObject.getSubmitted_By_to());
                holder.submittedByTextView.setText("Submitted By : " + getAlertListObject.getSubmitted_By_to());
            }
        } else if (userRole.equals("Teacher") || (userRole.equals("Administrator"))) {

            if (getAlertListObject.getAlertDivision().equals("All")) {
                holder.submittedByTextView.setText("Submitted to class " + getAlertListObject.getAlertClass() + " - All divisions");
                System.out.println("Submitted to class " + getAlertListObject.getAlertClass() + " - All divisions");
            } else if (getAlertListObject.getAlertStudId().equals("All")) {
                holder.submittedByTextView.setText("Submitted to class " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - All students");
                System.out.println("Submitted to class " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - All students");

            } else if (getAlertListObject.getAlertStudId().contains("students")) {
                holder.submittedByTextView.setText("Submitted to class " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - " + getAlertListObject.getSubmitted_By_to());
                System.out.println("Submitted to class " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - All students");

            } else {
                holder.submittedByTextView.setText("Submitted to " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - " + getAlertListObject.getSubmitted_By_to());
                System.out.println("Submitted to " + getAlertListObject.getAlertClass() + " " + getAlertListObject.getAlertDivision() + " - " + getAlertListObject.getSubmitted_By_to());

            }
        }

    }

    @Override
    public int getItemCount() {
        return alertListData.size();
    }

}