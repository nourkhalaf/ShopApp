package com.example.graduationonlineshop.ui.profile;

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
import com.example.graduationonlineshop.Model.StoreOwner;
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

import java.security.acl.Owner;
import java.util.HashMap;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private Button logoutBtn, updateBtn;
    private ImageView editName, editImage, editStoreName, editEmail, ownerImage;
    private EditText ownerName, ownerStoreName, ownerEmail;
    private TextView ownerPhone;

    private Uri ImageUri;
    private static final int GalleryPick = 1;
    private String  downloadImageUrl;

    private StorageReference storageProfilePictureRef;
    private DatabaseReference storeOwnersRef;
    private ProgressDialog progressDialog;

    private String checker = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        Paper.init(getContext());

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Store pictures");

        if (Paper.book().read(Prevalent.OwnerPhoneKey)!=null) {
            storeOwnersRef = FirebaseDatabase.getInstance().getReference().child("Stores Owners").child(Paper.book().read(Prevalent.OwnerPhoneKey).toString());


        }

        ownerInfoDisplay();

        progressDialog = new ProgressDialog(getContext());


        ownerImage= (ImageView) root.findViewById(R.id.owner_profile_image);
        editImage= (ImageView) root.findViewById(R.id.owner_profile_edit_image);
        ownerName = (EditText) root.findViewById(R.id.owner_profile_name);
        editName = (ImageView) root.findViewById(R.id.owner_profile_edit_name);
        ownerStoreName = (EditText) root.findViewById(R.id.owner_profile_store_name);
        editStoreName = (ImageView) root.findViewById(R.id.owner_profile_edit_store_name);
        ownerEmail = (EditText) root.findViewById(R.id.owner_profile_email);
        editEmail = (ImageView) root.findViewById(R.id.owner_profile_edit_email);
        ownerPhone = (TextView) root.findViewById(R.id.owner_profile_phone);
        updateBtn = (Button) root.findViewById(R.id.owner_profile_update_btn);
        logoutBtn = (Button) root.findViewById(R.id.owner_profile_logout_btn);

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.isEmpty())
                    checker = "data";
                ownerName.setEnabled(true);
            }
        });

        editStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.isEmpty())
                    checker = "data";
                ownerStoreName.setEnabled(true);
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.isEmpty())
                    checker = "data";
                ownerEmail.setEnabled(true);
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

                ownerName.setEnabled(false);
                ownerStoreName.setEnabled(false);
                ownerEmail.setEnabled(false);

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
            ownerImage.setImageURI(ImageUri);
        }
    }






    private void uploadProfile()
    {

        if (ImageUri != null) {
            if (TextUtils.isEmpty(ownerName.getText().toString())) {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(ownerStoreName.getText().toString())) {
                Toast.makeText(getContext(), "Please enter store name", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(ownerEmail.getText().toString())) {
                Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            }
            else {
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




    private void ownerInfoDisplay()
    {
        storeOwnersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(ownerImage);

                    }
                    StoreOwner ownerData = dataSnapshot.getValue(StoreOwner.class);
                    if (ownerData!=null) {
                        ownerName.setText(ownerData.getName());
                        ownerStoreName.setText(ownerData.getStoreName());
                        ownerEmail.setText(ownerData.getEmail());
                        ownerPhone.setText(ownerData.getPhone());

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
        HashMap<String, Object> map = new HashMap<>();
        if (downloadImageUrl!=null){
            map.put("image", downloadImageUrl);}
        map. put("name",ownerName.getText().toString());
        map. put("storeName",ownerStoreName.getText().toString());
        map. put("email",ownerEmail.getText().toString());



        storeOwnersRef.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {


                            Paper.book().write(Prevalent.OwnerNameKey,ownerName.getText().toString());


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