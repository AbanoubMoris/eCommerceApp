package com.example.ecommerce.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.Room.RoomFactory;
import com.example.ecommerce.asyncTasks.CustomerProducts.InsertAsyncTask;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;
import com.google.android.material.button.MaterialButton;


public class DetailsFragment extends Fragment {

    ImageView productIv;
    ImageView productQRIv;
    TextView titleTv;
    TextView detailsTv;
    TextView priceTv;
    TextView quantityTv;
    TextView deceptionTv;
    MaterialButton addToCartBtn;


    ProductModel productModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_product_details, container, false);

        productIv = v.findViewById(R.id.product_details_iv);
        productQRIv = v.findViewById(R.id.product_qr_iv);
        titleTv = v.findViewById(R.id.product_title_tv);
        detailsTv = v.findViewById(R.id.product_details_tv);
        priceTv = v.findViewById(R.id.product_price_tv);
        deceptionTv = v.findViewById(R.id.product_description_tv);
        quantityTv = v.findViewById(R.id.quantity_tv);
        addToCartBtn = v.findViewById(R.id.add_to_cart_btn);




        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        getClickedProductFromHomeFragment();
        setUpClickListeners();

    }
    private void init(){

    }

    private void setUpClickListeners() {
        String StrPrice = productModel.getProduct_price().replace(" EGP","");
        final double price = Double.parseDouble(StrPrice);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerProducts product = new CustomerProducts(
                        productModel.getProductName(),
                        1,
                        price
                );
                new InsertAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(product);
                Navigation.findNavController(view).navigate(R.id.action_detailsFragment_to_cartFragment);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void getClickedProductFromHomeFragment() {
        Bundle args = getArguments();
        assert args != null;
        productModel = (ProductModel) args.getSerializable("product");

        assert productModel != null;
        titleTv.setText(productModel.getProductName());
        detailsTv.setText(productModel.getProduct_description());
        priceTv.setText(productModel.getProduct_price() + " EGP");
//        quantityTv.setText(productModel.getProduct_quantity());

        Glide.with(requireContext()).load(productModel.getProduct_pic()).into(productIv);
        Glide.with(requireContext()).load(productModel.getQR()).into(productQRIv);
    }
}