package com.example.ecommerce.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.Room.RoomFactory;
import com.example.ecommerce.adapters.CartRvAdapter;
import com.example.ecommerce.asyncTasks.CustomerProducts.DeleteProductAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.GetAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.UpdateAsyncTask;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CartFragment extends Fragment {


    RecyclerView cartRv;
    List<ProductModel> productRVList = new ArrayList<>();
    List<ProductModel> RealproductList = new ArrayList<>();
    List<CustomerProducts> customerProducts = new ArrayList<>();
    CartRvAdapter cartRvAdapter;

    MaterialButton clearBtn;
    MaterialButton goToCheckoutBtn;

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartRv = view.findViewById(R.id.cart_rv);
        clearBtn = view.findViewById(R.id.clear_btn);
        goToCheckoutBtn = view.findViewById(R.id.checkout_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllProducts();
        setUpRecyclerView();
        setUpClickListeners();

        setRvItemSwipe();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(cartRv);
    }

    private void setUpClickListeners() {

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new DeleteAsyncTask(RoomFactory.getProductsDb(requireContext()).getProductDao()).execute();
                productRVList.clear();
                cartRvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getProducts(final String filter){
        productRVList.clear();

        for(CustomerProducts product : customerProducts) {

            DatabaseReference mProductDB = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("products")
                    .child(product.getProductName());
            mProductDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProductModel product = snapshot.getValue(ProductModel.class);
                        if (filter.equals("All")) RealproductList.add(product);
                        else if (product.getProductName().startsWith(filter))
                            RealproductList.add(product);

                        productRVList.add(product);
                        CustomerProducts customerProduct = customerProducts.get(productRVList.size()-1);

                        ProductModel newProduct = new ProductModel(product);
                        //newProduct = (product);

                        int quantity = customerProduct.getProduct_quantity();
                        double price = Double.parseDouble(newProduct.getProduct_price());
                        newProduct.setProduct_price((quantity*price)+" EGP");
                        newProduct.setProduct_quantity(customerProduct.getProduct_quantity()+"");
                        productRVList.set(productRVList.size()-1,newProduct);


                    cartRvAdapter.notifyDataSetChanged();
                    //progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
    private void getAllProducts() {

        customerProducts.clear();

        try {
            customerProducts.addAll(new GetAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute().get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getProducts("All");
        
        

    }

    private void setUpRecyclerView() {

        cartRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        cartRv.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        cartRvAdapter = new CartRvAdapter(productRVList, requireContext(), new CartRvAdapter.OnIncClick() {
            @Override
            public void onInc(View view, int position) {

                ProductModel productModel = RealproductList.get(position);
                if(Integer.parseInt(productModel.getProduct_quantity())>0) {

                    ProductModel customerProduct = productRVList.get(position);
                    customerProduct.setProduct_quantity((Integer.parseInt(customerProduct.getProduct_quantity()) + 1)+"");
                    //RealproductList.get(position).setProduct_quantity((Integer.parseInt(productModel.getProduct_quantity()) - 1) + "");

                    int quantity = Integer.parseInt(customerProduct.getProduct_quantity());

                    String StrPrice = productModel.getProduct_price().replace(" EGP","");
                    double price = Double.parseDouble(StrPrice);
                    productRVList.get(position).setProduct_price((quantity*price)+" EGP");
                    productRVList.get(position).setProduct_quantity(customerProduct.getProduct_quantity());
                    CustomerProducts c = new CustomerProducts();
                    c.setProductName(customerProduct.getProductName());
                    c.setProduct_quantity(quantity);
                    //****************************
                    // PriceCalculator.CalcNewPrice(requireContext(),productModel,'+');
                    //*****************************
                    new UpdateAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(c);
                    cartRvAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(requireContext(), "Sorry there is not enough products", Toast.LENGTH_SHORT).show();
                }

            }
        }, new CartRvAdapter.OnDecClick() {
            @Override
            public void onDec(View view, int position) {
                ProductModel productModel = RealproductList.get(position);
                ProductModel customerProduct = productRVList.get(position);
                if(Integer.parseInt(customerProduct.getProduct_quantity())>1) {


                    customerProduct.setProduct_quantity((Integer.parseInt(customerProduct.getProduct_quantity()) - 1)+"");
                    //RealproductList.get(position).setProduct_quantity((Integer.parseInt(productModel.getProduct_quantity()) + 1) + "");

                    int quantity = Integer.parseInt(customerProduct.getProduct_quantity());

                    String StrPrice = productModel.getProduct_price().replace(" EGP","");
                    double price = Double.parseDouble(StrPrice);
                    productRVList.get(position).setProduct_price((quantity*price)+" EGP");
                    productRVList.get(position).setProduct_quantity(customerProduct.getProduct_quantity());
                    CustomerProducts c = new CustomerProducts();
                    c.setProductName(customerProduct.getProductName());
                    c.setProduct_quantity(quantity);
                    //****************************
                    // PriceCalculator.CalcNewPrice(requireContext(),productModel,'+');
                    //*****************************
                    new UpdateAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(c);
                    cartRvAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(requireContext(), "item quantity cannot be less than 1 :)", Toast.LENGTH_SHORT).show();
                }


            }
        }, new CartRvAdapter.OnItemClick() {
            @Override
            public void onProductClick(View view, int position) {

                ProductModel productModel = productRVList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", productModel);

                Navigation.findNavController(view).navigate(R.id.action_cartFragment_to_detailsFragment, bundle);

            }
        });

        cartRv.setAdapter(cartRvAdapter);

    }


    private void setRvItemSwipe() {


        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(requireContext(), "I'm moving", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();

                CustomerProducts productModel = customerProducts.get(position);
                new DeleteProductAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(productModel);
                productRVList.remove(position);
                cartRvAdapter.notifyItemRemoved(position);

            }
        };

    }


}