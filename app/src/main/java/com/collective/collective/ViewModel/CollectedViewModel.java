package com.collective.collective.ViewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.collective.collective.Model.Firestore.Album;
import com.collective.collective.View.Utils.ListsUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectedViewModel extends ViewModel {
    private FirebaseQueryLiveData liveData;
    private DocumentReference collectedReference;
    private LiveData<Album> collectedLiveData;

    CollectedViewModel(String currentUser, String currentAlbum) {
        collectedReference = FirebaseFirestore.getInstance().collection("/users")
                .document(currentUser)
                .collection(ListsUtils.ALBUM_COLLECTED_NAME)
        .document(currentAlbum);
        assignLiveData();
    }

    private synchronized void assignLiveData() {
        liveData = new FirebaseQueryLiveData(collectedReference);
        collectedLiveData = Transformations.map(liveData, new CollectedViewModel.Deserializer());
        this.notify();
    }

    @NonNull
    public LiveData<DocumentSnapshot> getDocumentSnapshotLiveData() {
        return liveData;
    }

    private class Deserializer implements Function<DocumentSnapshot, Album> {
        @Override
        public Album apply(DocumentSnapshot documentSnapshot) {
            return documentSnapshot.toObject(Album.class);
        }
    }

    public synchronized LiveData<Album> getUserLiveData() {
        while (collectedLiveData == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return collectedLiveData;
    }
}
