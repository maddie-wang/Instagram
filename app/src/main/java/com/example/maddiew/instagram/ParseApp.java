package com.example.maddiew.instagram;

import android.app.Application;

import com.example.maddiew.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-instagram")
                .clientKey("thekeyischeese")
                .server("http://maddie-wang-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
