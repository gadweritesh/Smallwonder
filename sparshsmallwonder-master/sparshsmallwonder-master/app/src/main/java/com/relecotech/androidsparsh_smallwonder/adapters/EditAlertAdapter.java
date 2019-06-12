package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Alert;

import java.util.List;

public class EditAlertAdapter extends RecyclerView.Adapter<EditAlertAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<Alert> alertListData;
    private Alert getEditListObject;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userProfileDescriptionTextView;
        private TextView userProfileTitleTextView;
        private ImageView editImage;

        public ViewHolder(View convertView) {
            super(convertView);
            userProfileDescriptionTextView = (TextView) convertView.findViewById(R.id.userEditDescriptionTextView);
            userProfileTitleTextView = (TextView) convertView.findViewById(R.id.userEditTitleTextView);
            editImage = (ImageView) convertView.findViewById(R.id.editImage);
        }
    }

    public EditAlertAdapter(Context context, List<Alert> alertListData) {
        this.context = context;
        this.alertListData = alertListData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_edit_alert_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getEditListObject = alertListData.get(position);

        System.out.println(" alertListData.getDescription " + getEditListObject.getDescription());
        holder.userProfileDescriptionTextView.setText(getEditListObject.getDescription());
        holder.userProfileTitleTextView.setText(getEditListObject.getTitle());
        if (!getEditListObject.getEditable())
            holder.editImage.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return alertListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private EditAlertAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EditAlertAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
