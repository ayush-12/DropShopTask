package com.example.dropshoptask.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dropshoptask.Adapters.ProductsAdapter;
import com.example.dropshoptask.Contract.MainActivityContract;
import com.example.dropshoptask.Model.MainActivityModel;
import com.example.dropshoptask.Presenter.MainActivityPresenter;
import com.example.dropshoptask.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE = 1;

    private MainActivityContract.Presenter mPresenter;
    private MainActivityContract.Model mModel;

    private RecyclerView recyclerView;

    private Button chooseButton;

    private ProductsAdapter productsAdapter;

    private ProgressBar loadingBar;

    private String fileName = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainActivityPresenter(this, MainActivity.this);
        mModel = new MainActivityModel(MainActivity.this,this, mPresenter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.callModelToRefreshData(mPresenter);

    }

    @Override
    public void initView() {
        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chooseButton = findViewById(R.id.choose_file_button);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClick(view);
            }
        });

        loadingBar=findViewById(R.id.loading_bar);
    }

    @Override
    public void startOnActivityResult() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/octet-stream");
        Intent i = Intent.createChooser(intent, "View Default File Manager");
        startActivityForResult(i, 1);
    }

    @Override
    public void settingDataInListView(List<Map> list) {
        productsAdapter = new ProductsAdapter(MainActivity.this, list);
        recyclerView.setAdapter(productsAdapter);

    }

    @Override
    public void hideProgressBar() {
        loadingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showProgressBar() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        Toast.makeText(this, "file loaded ", Toast.LENGTH_SHORT).show();
        showProgressBar();
        fileName = getFileName(data.getData());
        Log.d(TAG, "onActivityResult: filename " + fileName);
        loadJsonFromUri(data.getData());
    }


    public void loadJsonFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            getJsonObject(new String(buffer, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getJsonObject(String json) {

            List<Map> productList = new ArrayList<>();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "getJsonObject: error " + e.getMessage());
            }
            try {
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    Map<String, Object> product = new HashMap<>();

                    String key = keys.next();
                    Log.d(TAG, "getJsonObject: key " + key);
                    JSONObject innerJObject = jsonObject.getJSONObject(key);
                    Iterator<String> innerKeys = innerJObject.keys();
                    while (innerKeys.hasNext()) {
                        String innerKey = innerKeys.next();
                        String value = innerJObject.getString(innerKey);
                        Log.d("key = " + innerKey, "value = " + value);
                        product.put(innerKey, value);
                    }

                    productList.add(product);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "getJsonObject: Size " + productList.size());

           mPresenter.callModelToUploadDataToFirestore(productList);
    }

    private String getFileName(Uri uri) throws IllegalArgumentException {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }

        cursor.moveToFirst();

        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

        cursor.close();

        return fileName;
    }
}



