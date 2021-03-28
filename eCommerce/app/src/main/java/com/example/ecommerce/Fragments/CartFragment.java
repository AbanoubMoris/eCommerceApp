package com.example.ecommerce.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Checkout;
import com.example.ecommerce.Login;
import com.example.ecommerce.R;
import com.example.ecommerce.Room.RoomFactory;
import com.example.ecommerce.adapters.CartRvAdapter;
import com.example.ecommerce.asyncTasks.CustomerProducts.DeleteAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.DeleteProductAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.GetAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.UpdateAsyncTask;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CartFragment extends Fragment {


    private RecyclerView cartRv;
    private List<ProductModel> productRVList = new ArrayList<>();
    private List<ProductModel> RealproductList = new ArrayList<>();
    private List<CustomerProducts> customerProducts = new ArrayList<>();
    private CartRvAdapter cartRvAdapter;
    private static double Total_price=0.0f;

    private MaterialButton clearBtn;
    private MaterialButton goToCheckoutBtn;
    private TextView totalPrice;

    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
       // savedInstanceState.putDouble("price",Total_price);
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
        totalPrice= view.findViewById(R.id.total_price_tv);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //put code on the onStart Method to be updated when purchase

    }

    @Override
    public void onStart() {
        super.onStart();
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
                new DeleteAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute();
                productRVList.clear();
                customerProducts.clear();
                cartRvAdapter.notifyDataSetChanged();
            }
        });
        goToCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    final String uid = user.getUid();
                    //i.putExtra("Total_Price",Total_price);

                    DatabaseReference mProductDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    mProductDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("username").getValue().toString();
                            String email = snapshot.child("email").getValue().toString();

                            Intent i = new Intent(getActivity(), Checkout.class);
                            i.putExtra("Uid",uid);
                            i.putExtra("Total_Price",Total_price);
                            i.putExtra("username",username);
                            i.putExtra("email",email);
                            startActivity(i);
                            Toast.makeText(requireContext(), uid, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else{
                    startActivity(new Intent(getActivity(), Login.class));
                }
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
            Total_price = 0.0f;
            for (CustomerProducts c : customerProducts){
                Total_price+=(c.getProduct_quantity()*c.getProduct_price());
            }
            totalPrice.setText(Total_price+" EGP");
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
                    c.setProduct_price(price);
                    customerProducts.set(position,c);
                   // c.setProduct_price(price);
                    //****************************
                    // PriceCalculator.CalcNewPrice(requireContext(),productModel,'+');
                    //*****************************
                    new UpdateAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(c);
                    cartRvAdapter.notifyDataSetChanged();


                    View v = cartRv.getLayoutManager().findViewByPosition(position);
                    TextView price_tv = v.findViewById(R.id.price_cart_tv);
                   // String StrPrice1 = price_tv.getText().toString().replace(" EGP","");
                   // double price1 = Double.parseDouble(StrPrice1);
                    Total_price-=(price*(quantity-1));
                    Total_price+=(price*(quantity));
                    totalPrice.setText(Total_price+" EGP");

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
                    c.setProduct_price(price);
                    customerProducts.set(position,c);

                    new UpdateAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(c);
                    cartRvAdapter.notifyDataSetChanged();

                    Total_price-=(price*(quantity));
                    Total_price+=(price*(quantity-1));
                    totalPrice.setText(Total_price+" EGP");
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
                Total_price-=(productModel.getProduct_price()*productModel.getProduct_quantity());
                totalPrice.setText(Total_price+" EGP");
                new DeleteProductAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(productModel);
                productRVList.remove(position);
                customerProducts.remove(position);
                cartRvAdapter.notifyItemRemoved(position);

            }
        };
    }
}