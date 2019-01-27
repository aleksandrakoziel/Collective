package com.collective.collective.ViewModel;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;


public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final String currentUser;

    @Inject
    public UserViewModelFactory (String currentUser) {
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public UserViewModel create(@NonNull Class modelClass) {
        return new UserViewModel(currentUser);
    }
}
