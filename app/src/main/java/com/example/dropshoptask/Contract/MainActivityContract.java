package com.example.dropshoptask.Contract;

import com.example.dropshoptask.View.MainActivity;

import java.util.List;
import java.util.Map;

public interface MainActivityContract {

    interface View{
        void initView();
        void startOnActivityResult();
        void settingDataInListView(List<Map> list);

        void hideProgressBar();
        void showProgressBar();
    }

    interface Model{
        void refreshData(MainActivityContract.Presenter presenter);

        void listForView(List<Map> list);

        List<Map> getSortedList(List<Map<String,Object>> list);

        void uploadDataToFirestore(List<Map> list);
    }

    interface Presenter{
        void onClick(android.view.View view);

        void sendIntentToChoose();

        void callModelToRefreshData(MainActivityContract.Presenter presenter);

        void setDataInListView(List<Map> list);

        void callModelToUploadDataToFirestore(List<Map> list);


        void callHideProgressBar();
        void callShowProgressBar();
    }
}
