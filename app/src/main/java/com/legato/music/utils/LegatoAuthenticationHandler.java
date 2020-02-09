package com.legato.music.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.enums.AuthStatus;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.hook.HookEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.core.types.AuthKeys;
import co.chatsdk.core.types.ChatError;
import co.chatsdk.core.utils.CrashReportingCompletableObserver;
import co.chatsdk.firebase.FirebaseAuthenticationHandler;
import co.chatsdk.firebase.FirebasePaths;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LegatoAuthenticationHandler extends FirebaseAuthenticationHandler {

    private String TAG = LegatoAuthenticationHandler.class.getSimpleName();
    @Nullable private AccountDetails accountDetails;
    @Nullable AuthCredential credential = null;

    public Completable deleteUser() {
        return Completable.create(
                emitter-> {
                    final User user = ChatSDK.currentUser();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    getCredential(firebaseUser);

                    // Stop listening to user related alerts. (added message or thread.)
                    ChatSDK.events().impl_currentUserOff(user.getEntityID());

                    Disposable disposable = ChatSDK.hook().executeHook(HookEvent.WillLogout, new HashMap<>()).concatWith(ChatSDK.core().setUserOffline()).subscribe(()->{
                        deleteUserDetails(ChatSDK.currentUserID());

                        removeLoginInfo(AuthKeys.CurrentUserID);

                        ChatSDK.events().source().onNext(NetworkEvent.logout());

                        if (ChatSDK.socialLogin() != null) {
                            ChatSDK.socialLogin().logout();
                        }

                        if (ChatSDK.hook() != null) {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put(HookEvent.User, user);
                            ChatSDK.hook().executeHook(HookEvent.DidLogout, data).subscribe(new CrashReportingCompletableObserver());;
                        }

                        deleteUserAuthentication(firebaseUser, emitter);
                        authenticatedThisSession = false;

                        //emitter.onComplete();
                    }, emitter::onError);

                }).subscribeOn(Schedulers.single());
    }

    private void deleteUserDetails(String currentUserId) {
        deleteProfilePic(ChatSDK.currentUser().getAvatarURL());
        //delete any profile pictures
        FirebasePaths.userRef(currentUserId).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "Failed to remove user details from Database", e);
            }
        });

        FirebasePaths.firebaseRef().child("searchIndex").child(currentUserId).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "Failure getting searchIndex from Database", e);
            }
        });

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference().child("geofire").child(currentUserId).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "Failure getting geofire", e);
            }
        });
    }

    private void deleteProfilePic(String avatarUrl) {
        if (!TextUtils.isEmpty(avatarUrl) && avatarUrl.contains("https://firebasestorage.googleapis.com")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            try {
                StorageReference storageRef = storage.getReferenceFromUrl(avatarUrl);
                if (storageRef != null) {
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: deleted file");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "onFailure: did not delete file");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Issue while deleting profile picture", e);
            }
        }
    }

    @Override
    public Completable authenticate(final AccountDetails details) {
        accountDetails = details;
        return Single.create((SingleOnSubscribe<FirebaseUser>)
                emitter->{
                    if (isAuthenticating()) {
                        emitter.onError(ChatError.getError(ChatError.Code.AUTH_IN_PROCESS, "Can't execute two auth in parallel"));
                        return;
                    }

                    setAuthStatus(AuthStatus.AUTH_WITH_MAP);

                    OnCompleteListener<AuthResult> resultHandler = task-> AsyncTask.execute(()->{
                        if (task.isComplete() && task.isSuccessful()) {
                            emitter.onSuccess(task.getResult().getUser());
                        } else {
                            emitter.onError(task.getException());
                        }
                    });

                    switch ( details.type ) {
                        case Username:
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(details.username, details.password).addOnCompleteListener(resultHandler);
                            break;
                        case Register:
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(details.username, details.password).addOnCompleteListener(resultHandler);
                            break;
                        case Anonymous:
                            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(resultHandler);
                            break;
                        case Custom:
                            FirebaseAuth.getInstance().signInWithCustomToken(details.token).addOnCompleteListener(resultHandler);
                            break;
                        // Should be handled by Social Login Module
                        case Facebook:
                        case Twitter:
                        default:
                            emitter.onError(ChatError.getError(ChatError.Code.NO_LOGIN_TYPE, "No matching login type was found"));
                            break;
                    }
                })
                .flatMapCompletable(this::authenticateWithUser)
                .doOnTerminate(this::setAuthStateToIdle)
                .subscribeOn(Schedulers.single());
    }

    @Nullable public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    private void getCredential(FirebaseUser firebaseUser) {
        for (UserInfo profile : firebaseUser.getProviderData()) {
            // Id of the provider (ex: google.com)
            String provider = profile.getProviderId();
            if (FacebookAuthProvider.PROVIDER_ID.equals(provider)) {
                credential = getFbCredentials();
                break;
            } else if (GoogleAuthProvider.PROVIDER_ID.equals(provider))  {
                credential = getGoogleCredentials();
                break;
            } else if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                credential = getEmailCredentials();
                break;
            }
        }
    }

    private void deleteUserAuthentication(FirebaseUser firebaseUser, CompletableEmitter emitter) {
        if (credential != null) {
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");
                                firebaseUser.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User deleted");
                                                } else {
                                                    Log.e(TAG, "User authentication deletion failed :");
                                                }
                                            }
                                        });
                            } else {
                                Log.e(TAG, "User re-authentication failed. Unable to delete account.");
                            }
                            emitter.onComplete();
                        }
                    });
        }
    }

    @Nullable private AuthCredential getEmailCredentials(){
        AccountDetails accountDetails = this.getAccountDetails();
        if (accountDetails != null) {
            return EmailAuthProvider
                    .getCredential(accountDetails.username, accountDetails.password);
        }

        return null;
    }

    @Nullable private AuthCredential getGoogleCredentials() {
        AccountDetails accountDetails = this.getAccountDetails();
        if (accountDetails != null) {
            String token = accountDetails.token;
            return GoogleAuthProvider.getCredential(token, null);
        }

        return null;
    }

    private AuthCredential getFbCredentials() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return FacebookAuthProvider.getCredential(token.getToken());
    }
}
