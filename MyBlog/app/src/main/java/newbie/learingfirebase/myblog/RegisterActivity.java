package newbie.learingfirebase.myblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText,emailEditText,passwordEditText;
    private Button signUpButton;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);
        usernameEditText=(EditText)findViewById(R.id.usernameEditText);
        emailEditText=(EditText)findViewById(R.id.emailEditText);
        passwordEditText=(EditText)findViewById(R.id.passwordEditText);
        signUpButton=(Button)findViewById(R.id.signUpButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startRegister();

            }
        });
    }

    private void startRegister() {

        final String username=usernameEditText.getText().toString().trim();
        String email=emailEditText.getText().toString();
        final String password=passwordEditText.getText().toString();

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            progressDialog.setMessage("Signing up...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Sign Up Failed",
                                Toast.LENGTH_SHORT).show();
                    }

                    else {

                        String userId=auth.getCurrentUser().getUid();
                        DatabaseReference user=database.child(userId);
                        user.child("username").setValue(username);
                        user.child("image").setValue("default");

                        //user.child("email").setValue(email);
                        //user.child("password").setValue(password);

                        Intent MainIntent=new Intent(RegisterActivity.this, BlogActivity.class);
                        MainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(MainIntent);

                    }
                    progressDialog.dismiss();

                }
            });
        }
    }
}
