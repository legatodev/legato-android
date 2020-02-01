package com.legato.music.legato;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.legato.music.BuildConfig;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import co.chatsdk.core.session.ChatSDK;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static androidx.test.espresso.Espresso.onIdle;
import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public abstract class FireBaseTest implements OnCompleteListener<AuthResult> {

    private static final String TAG = FireBaseTest.class.getSimpleName();
    private static final String IDLING_NAME = "com.legato.music.FireBaseTest.key.IDLING_NAME";
    private static final CountingIdlingResource idlingResource = new CountingIdlingResource(IDLING_NAME);

    private static final String TESTUSER_EMAIL = "lgtu1000@outlook.com";
    private static final String TESTUSER_PASSWORD = "lgtu1000";


    @Before
    public void prepare() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int apps = FirebaseApp.getApps(context).size();
        if (apps == 0) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey(BuildConfig.apiKey)
                    .setApplicationId(BuildConfig.applicationId)
                    .setDatabaseUrl(BuildConfig.databaseUrl)
                    .setProjectId(BuildConfig.projectId)
                    .build();
            FirebaseApp.initializeApp(context, options);
        }
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES);
        IdlingRegistry.getInstance().register(idlingResource);
        FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(TESTUSER_EMAIL, TESTUSER_PASSWORD)
                    .addOnCompleteListener(this);
        idlingResource.increment();
        onIdle();
    }


    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ChatSDK.auth()
                    .authenticate()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.d(TAG,"ChatSDK logged in successfully!!");
                            idlingResource.decrement();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d(TAG,throwable.toString());
                        }
                    });

        } else {
            fail("The user was not logged successfully");
        }
    }
}
