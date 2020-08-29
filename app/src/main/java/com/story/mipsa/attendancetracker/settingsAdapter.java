package com.story.mipsa.attendancetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class settingsAdapter extends RecyclerView.Adapter<settingsAdapter.settingsViewHolder>{

    private ArrayList<String> settingsNameList = new ArrayList<>();
    private ArrayList<Integer> settingsImageList = new ArrayList<>();
    private FragmentActivity context;
    MainActivity mainActivity;
    private GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseUser user;

    public settingsAdapter(ArrayList<String> settingsList, ArrayList<Integer> settingsImage, FragmentActivity context) {
        this.settingsNameList = settingsList;
        this.settingsImageList = settingsImage;
        this.context = context;
    }

    @NonNull
    @Override
    public settingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item, parent, false);
        settingsViewHolder holder = new settingsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final settingsViewHolder holder, final int position) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        
        
        
        holder.imageView.setImageResource(settingsImageList.get(position));
        holder.textView.setText(settingsNameList.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentSelection = settingsNameList.get(position);
                Toast.makeText(view.getContext(),"You clicked "+settingsNameList.get(position), Toast.LENGTH_SHORT).show();
                if(currentSelection.equalsIgnoreCase("Instructions")){
                    Intent intent = new Intent(view.getContext(),InstructionsPage.class);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    context.startActivity(intent);
                }
                else if(currentSelection.equalsIgnoreCase("Help/Support")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","harshppatel7@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Tracker - Support");
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
                else if(currentSelection.equalsIgnoreCase("Logout")){
                    logout();
                }

            }
        });
    }

    public void logout() {
        signOut();
        firebaseAuth.signOut();
        context.finish();
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        context.startActivity(new Intent(context.getBaseContext(), Login.class));
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       context.finish();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return settingsNameList.size();
    }

    public class settingsViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        RelativeLayout parentLayout;
        public settingsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.settingsIcon);
            textView = itemView.findViewById(R.id.settingsName);
            parentLayout = itemView.findViewById(R.id.settings_item_parent);
        }
    }
}
