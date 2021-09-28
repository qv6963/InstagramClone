package com.example.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        // Set applicationId and server based on the values in the Back4App settings.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Owm75BBlF6WDFqkB1hVVtQn4LBddGuRNPnjcxWcb")
                .clientKey("WsrF3MWeXC6yWhzQYSHYt0ZjqhUFo9Ecom7hZKSg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
