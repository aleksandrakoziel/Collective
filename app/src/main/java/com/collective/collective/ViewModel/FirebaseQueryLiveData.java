package com.collective.collective.ViewModel;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;


public class FirebaseQueryLiveData extends LiveData<DocumentSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final DocumentReference query;
    private final EventListener listener = new MyValueEventListener();


    public FirebaseQueryLiveData(DocumentReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        if (query != null) {
            query.addSnapshotListener(listener);
        } else {
            Log.e(LOG_TAG, " null not good");
        }
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        if (query != null) {
            query.addSnapshotListener(listener);
        } else {
            Log.e(LOG_TAG, " null not good");
        }
    }

    private class MyValueEventListener implements EventListener {
        @Override
        public void onEvent(@Nullable Object o, @Nullable FirebaseFirestoreException e) {
            if (o instanceof DocumentSnapshot) {
                if (e != null) {
                    Log.d(LOG_TAG, "Error:" + e.getMessage());
                } else {
                    setValue((DocumentSnapshot) o);
                }
            }
        }
    }
}
