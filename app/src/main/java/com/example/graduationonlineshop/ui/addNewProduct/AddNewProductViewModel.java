package com.example.graduationonlineshop.ui.addNewProduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddNewProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CustomerProfile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}