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
import com.relecotech.androidsparsh_smallwonder.models.UserProfileListData;

import java.util.List;

/**
 * Created by Relecotech on 09-02-2018.
 */

public class EditProfileAdapter extends RecyclerView.Adapter<EditProfileAdapter.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    List<UserProfileListData> profileListData;
    private UserProfileListData getEditListObject;


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

    public EditProfileAdapter(Context context, List<UserProfileListData> profileListData) {
        this.context = context;
        this.profileListData = profileListData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_edit_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getEditListObject = profileListData.get(position);

        System.out.println(" profileListData.getDescription " + getEditListObject.getDescription());
        holder.userProfileDescriptionTextView.setText(getEditListObject.getDescription());
        holder.userProfileTitleTextView.setText(getEditListObject.getTitle());
        if (!getEditListObject.getEditable())
            holder.editImage.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return profileListData.size();
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
        private EditProfileAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EditProfileAdapter.ClickListener clickListener) {
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