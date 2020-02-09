package com.legato.music.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
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

    public Boolean isEmailVerified(){
        if (EmailAuthProvider.PROVIDER_ID.equals(mFirebaseAuth.getCurrentUser().getProviderId())) {
            return mFirebaseAuth.getCurrentUser().isEmailVerified();
        } else {
            return true;
        }
    }

    public void sendEmailVerification(){
        if (EmailAuthProvider.PROVIDER_ID.equals(mFirebaseAuth.getCurrentUser().getProviderId())) {
                mFirebaseAuth.getCurrentUser().sendEmailVerification();
        }
    }
}
