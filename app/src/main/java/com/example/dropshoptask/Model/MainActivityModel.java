package com.example.dropshoptask.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dropshoptask.Adapters.ProductsAdapter;
import com.example.dropshoptask.Contract.MainActivityContract;
import com.example.dropshoptask.View.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivityModel implements MainActivityContract.Model{


    private static final String TAG="MainModel";

    private Context context;
    private MainActivityContract.View view;
    private MainActivityContract.Presenter presenter;

    private FirebaseFirestore db;;

    public MainActivityModel(Context mContext, MainActivityContract.View mView,MainActivityContract.Presenter presenter) {
        this.context=mContext;
        view= mView;
        this.presenter=presenter;


    }


    public MainActivityModel(Context mContext) {
        this.context=mContext;
    }

    @Override
    public void refreshData(MainActivityContract.Presenter presenter) {
        this.presenter=presenter;
        final List<Map<String, Object>> productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Test")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Log.d(TAG, "DATA " + document.getId() + " => " + document.getData());
                            Map<String, Object> product = document.getData();
                            productList.add(product);
                        }

                        listForView(getSortedList(productList));
                    }
                });
    }

    @Override
    public void listForView(List<Map> list) {
        presenter.setDataInListView(list);
    }

    @Override
    public List<Map> getSortedList(List<Map<String,Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {

                Log.d(TAG, "compare: " + stringObjectMap.get("expiry"));
                Log.d(TAG, "compare: " + stringObjectMap.get("expiry").
                        toString().compareTo(t1.get("expiry").toString()));

                return (stringObjectMap.get("expiry")).toString().compareTo(t1.get("expiry").toString());
            }
        });


        List<Map> listTmp = new ArrayList<>();
        for (Map<String, Object> hashMap : list) {
            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                hashMap.put(entry.getKey(), entry.getValue());
            }
            listTmp.add(hashMap);
        }

        return listTmp;
    }

    @Override
    public void uploadDataToFirestore(final List<Map> productList) {
        for (int i = 0; i < productList.size(); i++) {
            db = FirebaseFirestore.getInstance();
            db.document("Test/"+productList.get(i).get("productId"))
                    .set(productList.get(i), SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: success");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ");
                            ((MainActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

            if(i == productList.size()-1){
                presenter.callHideProgressBar();
            }
        }
    }
}
