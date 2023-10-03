package com.example.prm_teamproject_carracing;

import java.util.ArrayList;

public class InMemoryData {
    private static InMemoryData instance;
    private ArrayList<User> users;
    private int index = -1;

    private InMemoryData(){
    }

    public static InMemoryData getInstance(){
        if(instance == null){
            instance = new InMemoryData();
        }
        return instance;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }

    public void setIndex(User user){
        index = users.indexOf(user);
    }

    public void setMoney(int money){
        if(index >= 0){
            users.get(index).setMoney(money);
        }
        else{
            System.out.println("Index is less then 0, index: " + index);
        }
    }
}
