package com.example.alex.datascraper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    private ImageButton mPhotoButton;
    private Context context;
    private File photo_file;
    private boolean faceDetectionOn;
    private static Button nextScreenButton;
    private FaceView overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        faceDetectionOn = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        LinearLayout photoL = (LinearLayout) findViewById(R.id.photoLayout);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        context = this.getApplicationContext();
        photo_file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FacePhoto");
        Uri uri = Uri.fromFile(photo_file);
        mPhotoButton = (ImageButton) this.findViewById(R.id.image_camera);
        overlay = this.findViewById(R.id.faceView);
        PackageManager packageManager = this.getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mPhotoButton.setEnabled(true);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, 1);
            }
        });
/*
        nextScreenButton = (Button) findViewById(R.id.nextPHQ2);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhotoActivity.this,SocialMediaActivity.class));
            }
        });*/

        photoL.setOnTouchListener(new SwipeActivity(this){
            @Override
            public void onSwipeLeft(){
                startActivity(new Intent(PhotoActivity.this,ResultsActivity.class));
            }
            public void onSwipeRight(){
                startActivity(new Intent(PhotoActivity.this,SocialMediaActivity.class));

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            if (resultCode != Activity.RESULT_OK)
                return;
            else if (requestCode == 1) {
                setPhoto();
            }
        }
    }

    private void setPhoto() {
        if(photo_file == null || !photo_file.exists()) {}
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    photo_file.getPath(), this);
            if(bitmap != null)
                faceDetection(bitmap);
        }
    }

    private void faceDetection(Bitmap bitmap) {
        //InputStream stream = getResources().openRawResource(R.raw.face2);
        //Bitmap bitmap = BitmapFactory.decodeStream(stream);

        // A new face detector is created for detecting the face and its landmarks.
        //
        // Setting "tracking enabled" to false is recommended for detection with unrelated
        // individual images (as opposed to video or a series of consecutively captured still
        // images).  For detection on unrelated individual images, this will give a more accurate
        // result.  For detection on consecutive images (e.g., live video), tracking gives a more
        // accurate (and faster) result.
        //
        // By default, landmark detection is not enabled since it increases detection time.  We
        // enable it here in order to visualize detected landmarks.

        if(faceDetectionOn) {
            FaceDetector detector = new FaceDetector.Builder(this)
                    .setTrackingEnabled(false)
                    .build();

            // This is a temporary workaround for a bug in the face detector with respect to operating
            // on very small images.  This will be fixed in a future release.  But in the near term, use
            // of the SafeFaceDetector class will patch the issue.
            Detector<Face> safeDetector = new SafeFaceDetector(detector);

            // Create a frame from the bitmap and run face detection on the frame.
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = safeDetector.detect(frame);


            overlay.setContent(bitmap, faces);
            safeDetector.release();
        }
        else {
            SparseArray<Face> dummy = new SparseArray<Face>();
            overlay.setContent(bitmap, dummy);
        }
        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.

    }
}
