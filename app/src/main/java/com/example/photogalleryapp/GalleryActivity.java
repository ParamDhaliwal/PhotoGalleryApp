package com.example.photogalleryapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

import static com.example.photogalleryapp.MainActivity.PHOTO_FILEPROVIDER;
import static com.example.photogalleryapp.MainActivity.decodeFile;
import static com.example.photogalleryapp.MainActivity.photoGallery;

public class GalleryActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        gridLayout = findViewById(R.id.gallery_grid);

        for (final File image: photoGallery) {
            ImageView iv = new ImageView(GalleryActivity.this);
            iv.setImageBitmap(decodeFile(image));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    200,
                    200
            );
            iv.setLayoutParams(lp);
            iv.setPadding(5,5,5,5);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(GalleryActivity.this, "Image clicked", Toast.LENGTH_SHORT).show();
                    final Dialog dialog = new Dialog(GalleryActivity.this);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setTitle(image.getName());

                    ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                    dialogImage.setImageBitmap(decodeFile(image));

                    TextView dialogText = dialog.findViewById(R.id.dialog_text);

                    ImageButton btnShare = (ImageButton) dialog.findViewById(R.id.btnShareImage);

                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareIt = new Intent(android.content.Intent.ACTION_SEND);
                            shareIt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            shareIt.setType("image/*");

                            Uri uri = FileProvider.getUriForFile(GalleryActivity.this, PHOTO_FILEPROVIDER, image);
                            shareIt.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(shareIt, "Share via"));

                        }
                    });

                    Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    // if button is clicked, close the custom dialog
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
            gridLayout.addView(iv);
        }
    }
}
