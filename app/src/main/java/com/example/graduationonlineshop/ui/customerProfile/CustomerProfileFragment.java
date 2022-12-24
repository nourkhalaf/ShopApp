package com.example.graduationonlineshop.ui.customerProfile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.graduationonlineshop.MainActivity;
import com.example.graduationonlineshop.Model.Customer;
import com.example.graduationonlineshop.Prevalent.Prevalent;
import com.example.graduationonlineshop.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

public class CustomerProfileFragment extends Fragment {

    private Button logoutBtn, updateBtn;
    private ImageView editName, editImage, customerImage;
    private EditText customerName;
    private TextView customerPhone;

    private Uri ImageUri;
    private static final int GalleryPick = 1;
    private String  downloadImageUrl;

    private StorageReference storageProfilePictureRef;
    private DatabaseReference customerRef;
    private ProgressDialog progressDialog;

    private String checker = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        if (Paper.book().read(Prevalent.CustomerPhoneKey)!=null) {
            customerRef = FirebaseDatabase.getInstance().getReference().child("Customers").child(Paper.book().read(Prevalent.CustomerPhoneKey).toString());

        }

        customerInfoDisplay();

        progressDialog = new ProgressDialog(getContext());


        customerImage= (ImageView) root.findViewById(R.id.customer_profile_image);
        editImage= (ImageView) root.findViewById(R.id.customer_profile_edit_image);
        editName = (ImageView) root.findViewById(R.id.customer_profile_edit_name);
        customerName = (EditText) root.findViewById(R.id.customer_profile_name);
        customerPhone = (TextView) root.findViewById(R.id.customer_profile_phone);
        updateBtn = (Button) root.findViewById(R.id.customer_profile_update_btn);
        logoutBtn = (Button) root.findViewById(R.id.customer_profile_logout_btn);

        editName.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
                if (checker.isEmpty())
                checker = "data";
                customerName.setEnabled(true);
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "image";
                OpenGallery();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customerName.setEnabled(false);
                if (checker.equals("data")){
                    progressDialog.setTitle("Update Profile");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    SaveInfoToDatabase();}

                if (checker.equals("image")){
                    progressDialog.setTitle("Update Profile");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    uploadProfile();}

                checker= "";

            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                getString(R.string.logout),
                                getString(R.string.cancel)
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(  getString(R.string.logout));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0)
                        {

                            AuthUI.getInstance()
                                    .signOut(getActivity())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Paper.book().destroy();

                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    });
                        }
                        if (i == 1)
                        {}

                    }
                });
                builder.show();

            }
        });

        return root;
    }


    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            customerImage.setImageURI(ImageUri);
        }
    }


    private void uploadProfile()
    {
        if (ImageUri != null)
        {
            if (TextUtils.isEmpty(customerName.getText().toString()))
            {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            }
            else{

                final StorageReference filePath = storageProfilePictureRef.child(ImageUri.getLastPathSegment() + ".jpg");

                final UploadTask uploadTask = filePath.putFile(ImageUri);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageUrl = task.getResult().toString();
                                    SaveInfoToDatabase();
                                }
                            }
                        });
                    }
                });
            }

        }
        else
            Toast.makeText(getContext(), "Select Image...", Toast.LENGTH_SHORT).show();


    }


    private void customerInfoDisplay()
    {
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(customerImage);

                    }
                    Customer customerData = dataSnapshot.getValue(Customer.class);
                    if (customerData!=null) {
                        customerName.setText(customerData.getName());
                        customerPhone.setText(customerData.getPhone());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SaveInfoToDatabase()
    {
        HashMap<String, Object>map = new HashMap<>();
        if (downloadImageUrl!=null){
        map.put("image", downloadImageUrl);}
        map.put("name", customerName.getText().toString());

        customerRef.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {

                            Paper.book().write(Prevalent.CustomerNameKey,customerName.getText().toString());
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Profile updated successfully..", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {

                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}