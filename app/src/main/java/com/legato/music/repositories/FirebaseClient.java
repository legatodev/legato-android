package com.legato.music.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
Any interaction with FireBase frame should be done by this class
*/

class FirebaseClient {

    @Nullable private  static FirebaseClient mFirebaseinstance;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;

    public static FirebaseClient getInstance(){
        if(mFirebaseinstance == null){
            mFirebaseinstance = new FirebaseClient();
        }
        return mFirebaseinstance;
    }

    public void destroyInstance() {
        mFirebaseinstance = null;
    }

    private FirebaseClient(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getChild(String pathString){
            return mFirebaseDatabase.getReference().child(pathString);
    }

    private boolean isEmailVerificationRequired() {
        for (UserInfo profile : mFirebaseAuth.getCurrentUser().getProviderData()) {
            // Id of the provider (ex: google.com)
            String provider = profile.getProviderId();
            if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                return true;
            }
        }

        return false;
    }

    public Boolean isEmailVerified(){
        if (isEmailVerificationRequired()) {
            return mFirebaseAuth.getCurrentUser().isEmailVerified();
        } else {
            return true;
        }
    }

    public void sendEmailVerification(){
        if (isEmailVerificationRequired()) {
                mFirebaseAuth.getCurrentUser().sendEmailVerification();
        }
    }
}
