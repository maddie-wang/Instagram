package com.example.maddiew.instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.maddiew.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    ImageView ivPicture;
    TextView tvUsername;
    View rootView;
    public final String APP_TAG = "Instagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public static final String PROFILE_KEY = "profilePicture";
    public ParseUser user;
    File photoFile;

    ArrayList<Post> posts;
    RecyclerView rvPosts;
    GridAdapter postAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        ivPicture = rootView.findViewById(R.id.ivProfilePicture);
        user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());



        rvPosts = rootView.findViewById(R.id.rvGrid);
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        posts = new ArrayList<>();
        postAdapter = new GridAdapter(posts);
        rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvPosts.setAdapter(postAdapter);

        loadMyPosts();

        if (user.getParseFile(PROFILE_KEY) != null) {
            updatePictureView();
        }


        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
            }
        });

        return rootView;
    }


    private void loadMyPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.orderByDescending("createdAt");
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.clear();
                    for (int i = 0; i < objects.size(); i++) {
                    Log.d("Home", "Post id " + i + " = " +
                           objects.get(i).getDescription() + " Username "
                          + objects.get(i).getUser().getUsername());
                        Post post =  objects.get(i);
                        if (post.getUser().getUsername().equals(user.getUsername())) {
                            posts.add(post);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    public void updatePictureView() {
        Glide.with(getActivity())
                .load(user.getParseFile(PROFILE_KEY).getUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(ivPicture);
    }
    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                // ivPicture.setImageBitmap(takenImage);



                final ParseFile parseFile = new ParseFile(photoFile);
                updatePicture(parseFile, user);
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updatePicture(ParseFile imageFile, ParseUser user) {
        if (imageFile != null) user.put(PROFILE_KEY, imageFile);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Updated profile picture", Toast.LENGTH_SHORT).show();
                    updatePictureView();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                final Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }



}
