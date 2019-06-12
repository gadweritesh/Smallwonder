package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.YoutubeVideoAdapter;
import com.relecotech.androidsparsh_smallwonder.models.YoutubeVideoModel;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 19-03-2018.
 */

public class VideoGallery extends AppCompatActivity implements ItemClickListener {
    private RecyclerView videoRecyclerView;
    String channelId;
    String URL;
    private ArrayList<YoutubeVideoModel> videoDetailsArrayList;
    private YoutubeVideoAdapter youtubeVideoAdapter;
    private String branchId;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private TextView noDataAvailableTextView;
    private MobileServiceClient mClient;
//    String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCvnhZgMUkliFVBfmHgruJLA&maxResults=3&key=AIzaSyDZEpCIRqY3mrXcZCp1y74ifi9upWssi0U";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_gallery);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);

        setUpRecyclerView();

        if (connectionDetector.isConnectingToInternet()) {
            fetchChannelId();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        videoDetailsArrayList = new ArrayList<>();
        youtubeVideoAdapter = new YoutubeVideoAdapter(this, videoDetailsArrayList);
        youtubeVideoAdapter.setClickListener(this);
    }

    private void fetchChannelId() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectToFetchAttachmentMetadata = new JsonObject();
        jsonObjectToFetchAttachmentMetadata.addProperty("Branch_Id", branchId);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchVideoDetail", jsonObjectToFetchAttachmentMetadata);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fetchChannelId API exception  " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new android.app.AlertDialog.Builder(VideoGallery.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetchChannelId();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 5000);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("fetchChannelId API   response  " + response);
                try {
                    JsonArray jsonArray = response.getAsJsonArray();

                    if (jsonArray.size() == 0) {
                        noDataAvailableTextView.setText(R.string.noDataAvailable);
                        noDataAvailableTextView.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else {
                        noDataAvailableTextView.setVisibility(View.INVISIBLE);
                        for (int loop = 0; loop < jsonArray.size(); loop++) {
                            System.out.println("loop no " + loop);
                            JsonObject jsonObjectForIteration = jsonArray.get(loop).getAsJsonObject();

                            channelId = jsonObjectForIteration.get("channelId").toString().replace("\"", "");
                        }

                        // get your youtube channel Id From
                        // youtube -->Setting --> Advanced Setting --> YouTube Channel ID
                        //Get APi Key From below Link
                        //https://console.cloud.google.com/apis/credentials?project=spashdevelopment

                        //for PlayList
//              UrlForPlayList = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=" + channelId + "&key=AIzaSyDZEpCIRqY3mrXcZCp1y74ifi9upWssi0U

//                URL = "https://www.googleapis.com/youtube/v3/playlists
//                URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&&playlistId=PLHPw0oq7S_0Js2RdvpaSyS0q1ALd3aRXD&key=AIzaSyDZEpCIRqY3mrXcZCp1y74ifi9upWssi0U
                        URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&maxResults=3&key=AIzaSyDZEpCIRqY3mrXcZCp1y74ifi9upWssi0U";
                        System.out.println("URL " + URL);
                        progressDialog.dismiss();
                        showVideo();
                    }
                } catch (Exception e) {
                    System.out.println(" Video parse exception  " + e.getMessage());
                }
            }
        });
    }

    /**
     * setup the RecyclerView here
     */
    private void setUpRecyclerView() {
        noDataAvailableTextView = (TextView) findViewById(R.id.videoNoDataAvailableTextView);
        videoRecyclerView = (RecyclerView) findViewById(R.id.VideoRecyclerView);
        videoRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showVideo() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(" response " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        YoutubeVideoModel videoDetails = new YoutubeVideoModel();

                        String videoId = null;
                        if (i != 0) {

                            videoId = jsonVideoId.getString("videoId");
                            videoDetails.setURL(jsonObjectdefault.getString("url"));
                            videoDetails.setVideoName(jsonsnippet.getString("title"));
                            videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                            videoDetails.setVideoId(videoId);

                            videoDetailsArrayList.add(videoDetails);
                        }
                    }
                    videoRecyclerView.setAdapter(youtubeVideoAdapter);
                    youtubeVideoAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View view, int position) {
        System.out.println(" INSIDE ON CLICK");
        YoutubeVideoModel videoModel = videoDetailsArrayList.get(position);
        Intent videoIntent = new Intent(this, YoutubePlayerActivity.class);
        videoIntent.putExtra("VideoId", videoModel.getVideoId());
        startActivity(videoIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
