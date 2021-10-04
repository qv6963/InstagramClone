package com.example.instagramclone.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagramclone.Post;
import com.example.instagramclone.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription1;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    public static final String TAG = "ComposeFragment";

    ActivityResultLauncher<Intent> someActivityResultLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //if(result.getResultCode() == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            ivPostImage.setImageBitmap(takenImage);
                        }else{
                            Toast.makeText(getContext(), "picture wasn't taken!!",Toast.LENGTH_SHORT).show();
                        }
                        //}
                    /*if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        ivPostImage.setImageBitmap(takenImage);

                        //launchCamera();
                    }else{
                        Toast.makeText(MainActivity.this, "No Picture",Toast.LENGTH_SHORT).show();
                    }*/
                    }
                });
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // This event is triggered soon after onCreateView().
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        etDescription1 = view.findViewById(R.id.tvUsername);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //if(result.getResultCode() == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            ivPostImage.setImageBitmap(takenImage);
                        }else{
                            Toast.makeText(getContext(), "picture wasn't taken!!",Toast.LENGTH_SHORT).show();
                        }
                        //}
                    /*if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        ivPostImage.setImageBitmap(takenImage);

                        //launchCamera();
                    }else{
                        Toast.makeText(MainActivity.this, "No Picture",Toast.LENGTH_SHORT).show();
                    }*/
                    }
                });

        btnCaptureImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        //queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String description = etDescription1.getText().toString();
                if(description.isEmpty()){
                    Toast.makeText(getContext(), "Description can not be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(getContext(), "There is no image",Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });
    }
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG,"photoFileName"+photoFileName);
        photoFile = getPhotoFileUrl(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(),"com.codepath.fileprovider",photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        Log.d(TAG,"intent.resolveActivity(getPackageManager()) "+intent.resolveActivity(getContext().getPackageManager()));
        if(intent.resolveActivity(getContext().getPackageManager()) != null){
            //startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            someActivityResultLauncher.launch(intent);
        }else{
            Toast.makeText(getContext(), "No Picture on launch",Toast.LENGTH_SHORT).show();
        }
    }


    /** protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
     //super.onActivityResult(requestCode, resultCode, data);
     if(resultCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
     if(resultCode == RESULT_OK){
     Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

     ivPostImage.setImageBitmap(takenImage);
     }else{
     Toast.makeText(this, "picture wasn't taken!!",Toast.LENGTH_SHORT).show();
     }
     }
     }**/

    public File getPhotoFileUrl(String fileName){
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create Directory");
        }
        return new File(mediaStorageDir.getPath()+File.separator + fileName);
    }

    private void savePost(String description, ParseUser currentUser, File photoFile){
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving!!",e);
                    Toast.makeText(getContext(), "Error while saving!!",Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!!!");
                etDescription1.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }
}