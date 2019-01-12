package com.example.spoluri.legato.youtube;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

interface ServiceTaskInterface {
    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JsonFactory jsonFactory = new GsonFactory();
}
