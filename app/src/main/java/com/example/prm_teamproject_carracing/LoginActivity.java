package com.example.prm_teamproject_carracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button loginBtn;
    InMemoryData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = InMemoryData.getInstance();

        Mapping();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User loginUser = null;
                for (User user : data.getUsers()) {
                    if(user.getUsername().equals(username.getText().toString())){
                        if(user.getPassword().equals(password.getText().toString())){
                            loginUser = user;
                            data.setIndex(user);
                            break;
                        }
                    }
                }
                if(loginUser == null){
                    Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(LoginActivity.this, DatCuocActivity.class);
                    intent.putExtra("user", loginUser);
                    startActivity(intent);
                }
            }
        });
    }

    private void Mapping(){
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        ArrayList<User> users = new ArrayList<User>();
        users.add(new User("user1", "1", 50));
        users.add(new User("user2", "1", 2000));
        users.add(new User("user3", "1", 3000));

        data.setUsers(users);
    }
}
