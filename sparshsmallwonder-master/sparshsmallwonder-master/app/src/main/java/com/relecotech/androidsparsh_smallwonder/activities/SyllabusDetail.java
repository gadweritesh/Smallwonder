package com.relecotech.androidsparsh_smallwonder.activities;

import android.support.v7.app.AppCompatActivity;

public class SyllabusDetail extends AppCompatActivity {
//
//    private File fullFilePath;
//    private String directory;
//    private PhotoView imageView;
//    private String attachmentIdentifier;
//    private int attachmentCount;
//    private List<String> syllabusAttachmentList;
//    private List<String> downloadAttachmentList;
//    private boolean isPdf;
//    private String syllabusSubject;
//    private ProgressBar progressBar;
//    private MobileServiceClient mClient;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_syllabus);
//
//        mClient = Singleton.Instance().mClientMethod(this);
//
//        progressBar = (ProgressBar) findViewById(R.id.syllabus_detail_progressBar);
//
//        syllabusSubject = getIntent().getStringExtra("subject");
//        attachmentIdentifier = getIntent().getStringExtra("attachmentIdentifier");
//        attachmentCount = getIntent().getIntExtra("attachmentCount", 0);
//
//        File dir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Syllabus");
//        directory = dir.getPath();
//        try {
//            if (dir.mkdirs()) {
//                System.out.println("Directory  created");
//            } else {
//                System.out.println("Directory not created");
//            }
//        } catch (Exception e) {
//            System.out.println("directory creation EXCEPTION" + e.getMessage());
//        }
//        syllabusAttachmentList = new ArrayList<>();
//        downloadAttachmentList = new ArrayList<>();
//        fetchAttachmentMetadata();
//    }
//
//    private void fetchAttachmentMetadata() {
//        JsonObject jsonObjectToFetchAttachmentMetadata = new JsonObject();
//        jsonObjectToFetchAttachmentMetadata.addProperty("attachmentIdentifier", attachmentIdentifier);
//
//        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentFetchDetail", jsonObjectToFetchAttachmentMetadata);
//
//        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//            @Override
//            public void onFailure(Throwable exception) {
//                resultFuture.setException(exception);
//                System.out.println(" SyllabusDetail API exception    " + exception);
//            }
//
//            @Override
//            public void onSuccess(JsonElement response) {
//                resultFuture.set(response);
//                System.out.println(" SyllabusDetail API   response    " + response);
//                JsonArray attachmentListArray = response.getAsJsonArray();
//                addItemsToAttachList(attachmentListArray);
//            }
//        });
//    }
//
//    private void addItemsToAttachList(JsonArray attachmentListArray) {
//        for (int loop = 0; loop <= attachmentListArray.size() - 1; loop++) {
//            System.out.println("loop no " + loop);
//            JsonObject jsonObjectForIteration = attachmentListArray.get(loop).getAsJsonObject();
//
//            String attachmentFileName = jsonObjectForIteration.get("fileName").toString();
//            attachmentFileName = attachmentFileName.substring(1, attachmentFileName.length() - 1);
//            System.out.println(" attachmentFileName " + attachmentFileName);
//
//            fullFilePath = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Syllabus/" + attachmentFileName);
//            if (attachmentFileName.contains(".pdf")) {
//                if (fullFilePath.exists()) {
////                    fromFile(String.valueOf(fullFilePath));
//                    callPdfFragment();
//                } else {
//                    new downloadingPdfAttachment().execute(attachmentFileName);
//                }
//                isPdf = true;
//            } else {
//                isPdf = false;
//                if (fullFilePath.exists()) {
//                    syllabusAttachmentList.add(attachmentFileName);
//                } else {
//                    downloadAttachmentList.add(attachmentFileName);
//                }
//            }
//        }
//        if (downloadAttachmentList.size() != 0) {
//            new downloadingImageAttachment().execute(downloadAttachmentList);
//        } else {
//            System.out.println(" ELSE OF downloadingImageAttachment");
//            if (!isPdf){
//                callImageFragment();
//            }
//        }
//    }
//
//    private void callPdfFragment() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        progressBar.setVisibility(View.INVISIBLE);
//        SyllabusPdfViewFragment syllabusViewFragment = new SyllabusPdfViewFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("filePath", String.valueOf(fullFilePath));
//        syllabusViewFragment.setArguments(bundle);
//        ft.replace(R.id.SyllabusDetailLinear, syllabusViewFragment);
//        ft.addToBackStack(null);
//        ft.commit();
//    }
//
//    private void callImageFragment() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        progressBar.setVisibility(View.INVISIBLE);
//        SyllabusImageFragment syllabusViewFragment = new SyllabusImageFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("syllabusSubject", syllabusSubject);
//        bundle.putSerializable("imageList", (Serializable) syllabusAttachmentList);
//        syllabusViewFragment.setArguments(bundle);
//        ft.replace(R.id.SyllabusDetailLinear, syllabusViewFragment);
//        ft.addToBackStack(null);
//        ft.commit();
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private class downloadingPdfAttachment extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//                CloudBlobContainer container = getContainer();
//                for (ListBlobItem blobItem : container.listBlobs(params[0])) {
//                    if (blobItem instanceof CloudBlob) {
//                        CloudBlob blob = (CloudBlob) blobItem;
//                        try {
//                            System.out.println("directory  blob.getName()---------------------------------" + directory + "/" + blob.getName());
//                            blob.download(new FileOutputStream(directory + "/" + blob.getName()));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("Exception downloadingAttachment" + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            callPdfFragment();
////            fromFile(String.valueOf(fullFilePath));
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private class downloadingImageAttachment extends AsyncTask<List<String>, Void, Void> {
//        @Override
//        protected Void doInBackground(List<String>... params) {
//            for (String list : params[0]) {
//                System.out.println(" list " + list);
//                try {
//                    CloudBlobContainer container = getContainer();
//                    for (ListBlobItem blobItem : container.listBlobs(list)) {
//                        if (blobItem instanceof CloudBlob) {
//                            CloudBlob blob = (CloudBlob) blobItem;
//                            try {
//                                System.out.println("directory  blob.getName()---------------------------------" + directory + "/" + blob.getName());
//                                blob.download(new FileOutputStream(directory + "/" + blob.getName()));
//                            } catch (Exception e) {
//                                System.out.println(" downloadingImageAttachment " + e.getMessage());
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    System.out.println("Exception downloadingAttachment" + e.getMessage());
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            syllabusAttachmentList.addAll(downloadAttachmentList);
//            callImageFragment();
////            imageFromFile(syllabusAttachmentList);
//        }
//    }

}
