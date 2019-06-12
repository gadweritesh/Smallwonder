package com.relecotech.androidsparsh_smallwonder.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AchievementListData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class AchievementDialogFragment extends DialogFragment {


    private AchievementListData achievementListData;
    private TextView achievementTitleTextView;
    private TextView achievementDescTextView;
    private String shareText;
    private Uri imageUri;
    private String imageName;
    private KonfettiView konfettiView;

    public static AchievementDialogFragment newInstance() {
        AchievementDialogFragment dialogFragment = new AchievementDialogFragment();
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File dir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement");
        try {
            if (dir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTION" + e.getMessage());
        }

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AchievementDialog);

        achievementListData = (AchievementListData) getArguments().getSerializable("AchievementListData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_achievement_dialog, container, false);
        ImageView achievementImageView = (ImageView) rootView.findViewById(R.id.achievementImageView);
        achievementTitleTextView = (TextView) rootView.findViewById(R.id.achievement_title_textView);
        achievementDescTextView = (TextView) rootView.findViewById(R.id.achievement_desc_textView);
        Button shareButton = (Button) rootView.findViewById(R.id.button_share);

        konfettiView = (KonfettiView) rootView.findViewById(R.id.viewKonfetti);

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(konfettiView.getWidth() + konfettiView.getWidth() / 2f, konfettiView.getHeight() + konfettiView.getHeight() / 3f) // for Center burst(not working)
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .stream(150, 2000L);

        int index = achievementListData.getAchievementImageUrl().lastIndexOf('/');
        imageName = achievementListData.getAchievementImageUrl().substring(index + 1);

        Picasso.with(getActivity())
                .load(achievementListData.getAchievementImageUrl())
                .into(new PhotoLoader(imageName, achievementImageView));

        achievementTitleTextView.setText(achievementListData.getAchievementTitle());
        achievementDescTextView.setText(achievementListData.getAchievementDescription());

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareText = "💥✨💐 Congratulations 💐✨💥\n" + achievementTitleTextView.getText().toString() + "\n\n" + achievementDescTextView.getText().toString();
                System.out.println(" shareText shareText " + shareText);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    File file = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement");
                    String filePath = "file://" + file.getPath() + "/" + imageName;
                    System.out.println(" filePath IF " + filePath);
                    File file1 = new File(filePath.replace("file://", ""));
                    imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file1);
                    System.out.println(" imageUri IF " + imageUri);
                } else {
                    File file1 = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement/" + imageName);
                    imageUri = Uri.parse(file1.getAbsolutePath());
                    System.out.println(" imageUri Else " + imageUri);
                }


                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Using"));
            }
        });

//        Picasso.get()
//                .load(achievementListData.getAchievementImageUrl())
//                .resize(200, 200)
//                .centerCrop()
//                .into(achievementImageView);


        return rootView;
    }


    //target to save
    private Target getTarget(final String nameOfImage, final ImageView imageView) {
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement/" + nameOfImage);
//                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        System.out.println(" file file " + file);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        return target;
    }

    public class PhotoLoader implements Target {
        private final String name;
        private ImageView imageView;
        private File file;

        public PhotoLoader(String name, ImageView imageView) {
            this.name = name;
            this.imageView = imageView;
        }

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
            file = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement/" + name);
            System.out.println(" file onBitmapLoaded " + file);
            try {
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                ostream.close();
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }


    }
}