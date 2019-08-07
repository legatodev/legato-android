package com.legato.music;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.legato.music.R;
import com.legato.music.registration.RegistrationActivity;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.social_login.FirebaseSocialLoginModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import io.reactivex.plugins.RxJavaPlugins;

public class AppObject extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // The Chat SDK needs access to the application's context
        Context context = getApplicationContext();

        RxJavaPlugins.setErrorHandler(error -> {
            //Log error or just ignore it
            error.printStackTrace();
        });

        // Initialize the Chat SDK
        // Pass in
        try {

            // The configuration object contains all the Chat SDK settings. If you want to see a full list of the settings
            // you should look inside the `Configuration` object (CMD+Click it in Android Studio) then you can see every
            // setting and the accompanying comment
            Configuration.Builder config = new Configuration.Builder(context);

            // Perform any configuration steps
            // The root path is an optional setting that allows you to run multiple Chat SDK instances on one Realtime database.
            // For example, you could have one root path for "test" and another for "production"
            config.firebaseRootPath("prod");
            config.logoDrawableResourceID(R.drawable.legato_logo);

            // Start the Chat SDK and pass in the interface adapter and network adapter. By subclassing either
            // of these classes you could modify deep functionality withing the Chat SDK
            ChatSDK.initialize(config.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));

            // File storage is needed for profile image upload and image messages
            FirebaseFileStorageModule.activate();
            FirebaseSocialLoginModule.activate(getApplicationContext());
        }
        catch (ChatSDKException e) {
            e.printStackTrace();
        }

        // Uncomment this to enable Firebase UI
        FirebaseUIModule.activate(EmailAuthProvider.PROVIDER_ID, GoogleAuthProvider.PROVIDER_ID, FacebookAuthProvider.PROVIDER_ID);
        ChatSDK.ui().setMainActivity(RegistrationActivity.class);
    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
