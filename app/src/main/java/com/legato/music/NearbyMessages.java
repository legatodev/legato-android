package com.legato.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.legato.music.views.activity.UserProfileActivity;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.session.ChatSDK;

public class NearbyMessages {

    private static final String TAG = NearbyMessages.class.getSimpleName();
    @Nullable MessageListener mMessageListener;
    @Nullable Message mActiveMessage;
    @Nullable Activity activity;

    public NearbyMessages(Activity activity) {
        this.activity = activity;
    }

    private void publish(String message) {
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(activity).publish(mActiveMessage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "publish failed");
            }
        });
    }

    private void unpublish() {
        if (mActiveMessage != null) {
            Nearby.getMessagesClient(activity).unpublish(mActiveMessage);
            mActiveMessage = null;
        }
    }

    public void RegisterMessageListener() {
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.v( TAG, "Found a nearby user");
                if (activity != null) {
                    Context context = activity.getApplicationContext();
                    String userEntityID = new String(message.getContent());
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Keys.PushKeyUserEntityID, userEntityID);
                    ChatSDK.ui().notificationDisplayHandler().createMessageNotification(
                            context, intent, userEntityID, "Nearby User Found", "Click to see profile");
                }
            }

            @Override
            public void onLost(Message message) {
            }
        };
    }

    // Subscribe to receive messages.
    private void subscribe() {
        Nearby.getMessagesClient(activity).subscribe(mMessageListener).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v( TAG, "subscribe failed");
            }
        });
    }

    private void unsubscribe() {
        Nearby.getMessagesClient(activity).unsubscribe(mMessageListener);
    }

    public void initialize() {
        if (mMessageListener == null) {
            RegisterMessageListener();
            subscribe();
        }

        publish(ChatSDK.currentUserID());
    }
}
