package com.collective.collective.ViewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.collective.collective.Model.Firestore.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserViewModel extends ViewModel {
    private FirebaseQueryLiveData liveData;
    private DocumentReference userReference;
    private LiveData<User> userLiveData;

    UserViewModel(String currentUser) {
        userReference = FirebaseFirestore.getInstance().collection("/users").document(currentUser);
        assignLiveData();
    }

    private synchronized void assignLiveData() {
        liveData = new FirebaseQueryLiveData(userReference);
        userLiveData = Transformations.map(liveData, new Deserializer());
        this.notify();
    }

    @NonNull
    public LiveData<DocumentSnapshot> getDocumentSnapshotLiveData() {
        return liveData;
    }

    private class Deserializer implements Function<DocumentSnapshot, User> {
        @Override
        public User apply(DocumentSnapshot documentSnapshot) {
            return documentSnapshot.toObject(User.class);
        }
    }

    public synchronized LiveData<User> getUserLiveData() {
        while (userLiveData == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return userLiveData;
    }
}
