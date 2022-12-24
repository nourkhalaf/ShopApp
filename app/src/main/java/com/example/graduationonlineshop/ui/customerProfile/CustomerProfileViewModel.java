package com.example.graduationonlineshop.ui.customerProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomerProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CustomerProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CustomerProfile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}