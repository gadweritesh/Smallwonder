package com.relecotech.androidsparsh_smallwonder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.SchoolGalleryListData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amey on 06-04-2018.
 */

public class SlideshowDialogFragment extends DialogFragment {
    private TextView lblCount, lblTitle, lblDate;
    private ArrayList<SchoolGalleryListData> dialogGalleryListDataArrayList;
    private int selectedPosition;
    private TextView imgNotFoundTv;
    private ViewPager viewPager;
    private SchoolGalleryListData dsdg;
    private MyViewPagerAdapter myViewPagerAdapter;
    private AdView mAdView;


    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_slider, container, false);
        lblCount = (TextView) rootView.findViewById(R.id.lbl_count);
        lblTitle = (TextView) rootView.findViewById(R.id.title);
        lblDate = (TextView) rootView.findViewById(R.id.date);
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        dialogGalleryListDataArrayList = (ArrayList<SchoolGalleryListData>) getArguments().getSerializable("listofGallery");
        selectedPosition = getArguments().getInt("position");
        System.out.println("dialogGalleryListDataArrayList-------------------  " + dialogGalleryListDataArrayList);

        System.out.println("dsdsdsdsddsdsdsdsdsdsd    1");
        myViewPagerAdapter = new MyViewPagerAdapter();
        System.out.println("dsdsdsdsddsdsdsdsdsdsd    2");
        viewPager.setAdapter(myViewPagerAdapter);
        System.out.println("dsdsdsdsddsdsdsdsdsdsd     3");
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        System.out.println("dsdsdsdsddsdsdsdsdsdsd    4");

        setCurrentItem(selectedPosition);
        System.out.println("dsdsdsdsddsdsdsdsdsdsd    5");
        return rootView;
    }

    private void setCurrentItem(int selectedPosition) {
        viewPager.setCurrentItem(selectedPosition, false);
        displayMetaInfo(selectedPosition);
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + dialogGalleryListDataArrayList.size());
        lblTitle.setText(dialogGalleryListDataArrayList.get(position).getPhotoName());
        lblDate.setText(dialogGalleryListDataArrayList.get(position).getGalleryPostDate());
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private SchoolGalleryListData pagerAdapterGalleryListDatashdg;

        public MyViewPagerAdapter() {
            System.out.println("INSIDE MyViewPagerAdapter ");
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            final ProgressBar progressbar = (ProgressBar) view.findViewById(R.id.progress);
            imgNotFoundTv = (TextView) view.findViewById(R.id.noImgTv);
            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.fullGalleryImageView);

            pagerAdapterGalleryListDatashdg = dialogGalleryListDataArrayList.get(position);
            Picasso.with(getActivity())
                    .load(pagerAdapterGalleryListDatashdg.getImageUrlToDownloadImage())
                    .resize(600, 600)
                    .centerInside()
                    .into(imageViewPreview, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            imgNotFoundTv.setText("Image not found");
                        }


                    });

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return dialogGalleryListDataArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}