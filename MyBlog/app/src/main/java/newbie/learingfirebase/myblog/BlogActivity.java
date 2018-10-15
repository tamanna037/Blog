package newbie.learingfirebase.myblog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogActivity extends AppCompatActivity {

    private boolean likeFlag=false;
    private RecyclerView recView;
    DatabaseReference database;
    DatabaseReference likeDatabase;
    DatabaseReference users;
    Query timeline;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);


        database=FirebaseDatabase.getInstance().getReference().child("Blog");
        likeDatabase=FirebaseDatabase.getInstance().getReference().child("Likes");
        users=FirebaseDatabase.getInstance().getReference().child("Users");
       //timeline= database.orderByChild("userid").equalTo(auth.getCurrentUser().getUid());
        recView=(RecyclerView)findViewById(R.id.recView);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));


        auth=FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent registerIntent=new Intent(BlogActivity.this,LoginActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);


                }
            }
        };

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        ImageButton likeBtn;
        DatabaseReference likeDatabase;
        FirebaseAuth auth;

        public BlogViewHolder(View view)
        {
            super(view);
            this.view=view;
            likeBtn=(ImageButton)view.findViewById(R.id.likeButton);
            auth=FirebaseAuth.getInstance();
            likeDatabase=FirebaseDatabase.getInstance().getReference().child("Likes");


        }

        public void setTitle(String title)
        {
            TextView postTitle=(TextView)view.findViewById(R.id.postTitle);
            postTitle.setText(title);
        }

        public void setDesc(String desc)
        {
            TextView postDesc=(TextView)view.findViewById(R.id.postDes);
            postDesc.setText(desc);
        }

        public void setUsername(String username)
        {
            TextView postUsername=(TextView)view.findViewById(R.id.usernameTextView);
            postUsername.setText(username);
        }

        public void setImage(Context context,String image)
        {
            ImageView postImage=(ImageView)view.findViewById(R.id.postImage);
            Picasso.with(context).load(image).fit().centerCrop().into(postImage);

        }

        public void setLikeBtn(final String postKey)
        {
            likeDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid()))
                    {

                        likeBtn.setImageResource(R.drawable.ic_green_thumbs_up);
                        Log.v("Thumb",postKey);
                    }

                    else
                    {

                        likeBtn.setImageResource(R.drawable.ic_thumbs_up);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        auth.addAuthStateListener(authStateListener);


        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,R.layout.blog_row,BlogViewHolder.class,database) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String postKey=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(postKey);
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BlogActivity.this, "You clicked on " + postKey, Toast.LENGTH_SHORT);
                        Log.v("Hi ", postKey + "");

                    }
                });

                viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {


                        likeFlag=true;

                            likeDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(likeFlag){
                                    if(dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid()))
                                    {
                                        likeDatabase.child(postKey).child(auth.getCurrentUser().getUid()).removeValue();
                                        likeFlag=false;
                                    }

                                    else {
                                            likeDatabase.child(postKey).child(auth.getCurrentUser().getUid()).setValue("random");
                                        likeFlag=false;

                                    }}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    }
                });
            }
        };

        recView.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_icon_xml, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if( item.getItemId()==R.id.action_add)
        {

            startActivity(new Intent(BlogActivity.this, PostActivity.class));
        }

        else if( item.getItemId()==R.id.action_logout)
        {

           logout();
            startActivity(new Intent(BlogActivity.this, LoginActivity.class));

        }

        else if(item.getItemId()==R.id.action_more)
        {


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }


    }

    public void logout() {

        auth.signOut();
    }
}
