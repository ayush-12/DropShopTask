package com.example.dropshoptask.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dropshoptask.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Map> productsList;



    public ProductsAdapter(Context context,List<Map> productsList) {
        this.productsList=productsList;
        mContext = context;
    }


    @NonNull
    @Override
    public ProductsAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductViewHolder holder, int position) {
        Map<String, Object> product = productsList.get(position);
        for(String key : product.keySet()){
            if(key.contains("expiry")){
                holder.expiryTextView.setText(product.get(key).toString());
            }
            else if(key.contains("productId")){
                holder.nameTextView.setText(product.get(key).toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView expiryTextView;

        public ProductViewHolder(View view) {
            super(view);
            nameTextView=view.findViewById(R.id.product_name_textview);
            expiryTextView=view.findViewById(R.id.expiry_textview);
        }
    }

}
