package com.relecotech.androidsparsh_smallwonder.fragments;

import android.support.v4.app.Fragment;

public class SyllabusImageFragment extends Fragment {
//
//    private List<String> imageList;
//    private File fullFilePath;
//    private PhotoView imageView;
//    private LinearLayout layout;
//    private String syllabusSubject;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        System.out.println(" INSIDE SyllabusImageFragment");
//
//        syllabusSubject = getArguments().getString("syllabusSubject");
//        imageList = getArguments().getStringArrayList("imageList");
//
//        getActivity().setTitle(syllabusSubject + " Syllabus");
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
//        layout = (LinearLayout)rootView.findViewById(R.id.SyllabusViewLinear);
//
//        imageFromFile(imageList);
//
//        return rootView;
//    }
//
//    private void imageFromFile(List<String> syllabusAttachmentList) {
//        for (int i = 0; i < syllabusAttachmentList.size(); i++) {
//            fullFilePath = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Syllabus/" + syllabusAttachmentList.get(i));
//            Bitmap mBitmap = BitmapFactory.decodeFile(fullFilePath.toString());
//            imageView = new PhotoView(getActivity());
//            imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_placeholder));
//            imageView.setImageBitmap(mBitmap);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            lp.setMargins(15, 15, 15, 15);
//            imageView.setLayoutParams(lp);
//
//            // Adds the view to the layout
//            layout.addView(imageView);
//        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    // handle back button's click listener
//
//                    getActivity().finish();
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

}
