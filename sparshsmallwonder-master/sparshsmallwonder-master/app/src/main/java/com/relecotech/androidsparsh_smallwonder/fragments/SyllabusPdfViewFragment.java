package com.relecotech.androidsparsh_smallwonder.fragments;

import android.support.v4.app.Fragment;

public class SyllabusPdfViewFragment extends Fragment {
//    private String filePath;
//    private ViewPager viewPager;
//    private ArrayList<Bitmap> itemData;
//    private VigerAdapter adapter;
//    private VigerPDF vigerPDF;
//    private String fileName;
//    private int pageCount;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        System.out.println(" INSIDE SyllabusPdfViewFragment");
//        filePath = getArguments().getString("filePath");
//
//        fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
//        System.out.println(" filePath " + filePath);
//        getActivity().setTitle(fileName);
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);
//        viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerSyllabus);
//        vigerPDF = new VigerPDF(getActivity());
//        itemData = new ArrayList<>();
//        adapter = new VigerAdapter(getActivity(), itemData);
//        viewPager.setAdapter(adapter);
//
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            public void onPageScrollStateChanged(int state) {
//            }
//
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            public void onPageSelected(int position) {
//                System.out.println(" position " + position);
//                getActivity().setTitle(String.format("%s (%s/%s)", fileName, position + 1, pageCount));
//            }
//        });
//
//        fromFile(filePath);
//
//        return rootView;
//    }
//
//
//    private void fromFile(String path) {
//        itemData.clear();
//        adapter.notifyDataSetChanged();
//        File file = new File(path);
//        vigerPDF.cancle();
//        System.out.println(" file " + file);
//        vigerPDF.initFromFile(file, new OnResultListener() {
//            @Override
//            public void resultData(Bitmap data) {
//                itemData.add(data);
//                pageCount = itemData.size();
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void progressData(int progress) {
//                Log.e("data", "" + progress);
//            }
//
//            @Override
//            public void failed(Throwable t) {
//            }
//        });
//    }
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
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
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

