package com.example.ecommerce.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.CategoriesRvAdapter;
import com.example.ecommerce.models.CategoriesModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {


    RecyclerView categoriesRv;
    CategoriesRvAdapter categoriesRvAdapter;
    List<CategoriesModel> categoriesList = new ArrayList<>();
    EditText search_result;
    ProgressDialog progressDialog;

    private FloatingActionButton add_new_category_btn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        categoriesRv = v.findViewById(R.id.category_rv);
        add_new_category_btn = v.findViewById(R.id.add_new_category_btn);
        initProgressDialog("Loading Categories","please wait a while...");
        progressDialog.show();
        IsAdmin();
        return v;
    }

    private void IsAdmin() {
        final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences;
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        String username = sharedpreferences.getString("email", "Welcome!");
        String password = sharedpreferences.getString("password", "Enter your Account");
        if(username.equals("admin")&&password.equals("admin")){
            //isAdmin=true;
            add_new_category_btn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Welcome Admin", Toast.LENGTH_SHORT).show();
        }else {
            //isAdmin=false;
            add_new_category_btn.setVisibility(View.GONE);

        }
    }
    private void initProgressDialog(String Title,String Message) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add_new_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putCharSequence("sourceFragment","category");
                Navigation.findNavController(v).navigate(R.id.action_categoryFragment_to_newCategoryFragment,b);
            }
        });

        getCategories("All");
        setUpRecyclerView();

        View v = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView();
        search_result = v.findViewById(R.id.search_et);
        Button cart_btn = v.findViewById(R.id.cart_btn);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_cartFragment);
            }
        });
        search_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = String.valueOf(s);
                categoriesList.clear();
                getCategories(s1);
                progressDialog.show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getCategories(final String filter) {
        DatabaseReference categoryDB = FirebaseDatabase.getInstance().getReference().child("categories");
        categoryDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(ds.getValue());
                    CategoriesModel cat =  ds.getValue(CategoriesModel.class);
                    if(cat.getCategory_Name().startsWith(filter))
                        categoriesList.add(cat);
                    else if(filter.startsWith("All"))
                        categoriesList.add(cat);

                }
                categoriesRvAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void setUpRecyclerView() {

        categoriesRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        categoriesRv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(14), true));

        categoriesRvAdapter = new CategoriesRvAdapter(categoriesList, requireContext(), new CategoriesRvAdapter.OnCategoryClick() {
            @Override
            public void onClick(View view, int position) {
                Bundle i = new Bundle();
                i.putCharSequence("filter",categoriesList.get(position).getCategory_Name());
                Navigation.findNavController(view).navigate(R.id.action_categoryFragment_to_homeFragment,i);
            }
        });
        categoriesRv.setAdapter(categoriesRvAdapter);

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}