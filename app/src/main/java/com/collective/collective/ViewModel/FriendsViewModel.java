package com.collective.collective.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.collective.collective.Model.Firestore.Following;

import java.util.List;

public class FriendsViewModel extends ViewModel {
    MutableLiveData<List<Following>> followings = new MutableLiveData<>();

    public LiveData<List<Following>> getFollowings() {
        if (followings.getValue() == null) {

        }
        return followings;
    }
}
