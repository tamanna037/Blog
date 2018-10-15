package newbie.learingfirebase.myblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT = 1 ;
    private TextView titleTextView,desTextView;
    private ImageButton imageButton;
    private Button submitButton;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private StorageReference storage;
    private DatabaseReference database;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        auth=FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance().getReference();
        titleTextView=(TextView)findViewById(R.id.titleTextView);
        desTextView=(TextView)findViewById(R.id.desTextView);
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        submitButton=(Button)findViewById(R.id.submitButton);
        progressDialog=new ProgressDialog(this);
        currentUser=auth.getCurrentUser();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent( Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_INTENT);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=titleTextView.getText().toString().trim();
                final String des=desTextView.getText().toString().trim();

                if(!(TextUtils.isEmpty(title)==true || TextUtils.isEmpty(des)==true || imageUri==null )    )
                {

                    progressDialog.setMessage("Posting...");
                    progressDialog.show();

                          StorageReference filepath = storage.child("Blog Images").child(imageUri.getLastPathSegment());
                            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {



                                    final DatabaseReference blogPost = database.child("Blog").push();


                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            blogPost.child("title").setValue(title);
                                            blogPost.child("description").setValue(des);
                                            blogPost.child("userid").setValue(currentUser.getUid());
                                            blogPost.child("image").setValue(downloadUrl.toString());
                                           // blogPost.child("username").setValue();


                                    database.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            blogPost.child("username").setValue(dataSnapshot.child("Users").child(currentUser.getUid()).child("username").getValue());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Toast.makeText(PostActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(PostActivity.this,BlogActivity.class));
                    }});




                }



            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {


            imageUri = data.getData();
            imageButton.setImageURI(imageUri);
        }
    }
}
