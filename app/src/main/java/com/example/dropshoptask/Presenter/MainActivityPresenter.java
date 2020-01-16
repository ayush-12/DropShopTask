package com.example.dropshoptask.Presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dropshoptask.Contract.MainActivityContract;
import com.example.dropshoptask.Model.MainActivityModel;
import com.example.dropshoptask.R;

import java.util.List;
import java.util.Map;

public class MainActivityPresenter implements MainActivityContract.Presenter{

    private final String TAG = "MainPresenter";
    private MainActivityContract.View mView;
    private Context mContext;
    private MainActivityContract.Model model;


    public MainActivityPresenter(MainActivityContract.View view, Context context){
        mView=view;
        mContext=context;
        initPresenter();
    }

    private void initPresenter(){
        model= new MainActivityModel(mContext);
        mView.initView();
    }

    @Override
    public void onClick(android.view.View view) {
        if (view.getId() == R.id.choose_file_button) {
            Log.d(TAG, "onClick: choose button pressed");
            sendIntentToChoose();
        }

    }

    @Override
    public void sendIntentToChoose() {
        mView.startOnActivityResult();
    }

    @Override
    public void callModelToRefreshData(MainActivityContract.Presenter presenter) {
        model.refreshData(presenter);
    }

    @Override
    public void setDataInListView(List<Map> list) {
        mView.settingDataInListView(list);
    }

    @Override
    public void callModelToUploadDataToFirestore(List<Map> list) {
        model.uploadDataToFirestore(list);
    }

    @Override
    public void callHideProgressBar() {
        mView.hideProgressBar();
    }

    @Override
    public void callShowProgressBar() {
        mView.showProgressBar();
    }


}
