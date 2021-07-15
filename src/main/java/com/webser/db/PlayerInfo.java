package com.webser.db;

import com.webser.annotation.GeneratedValue;
import com.webser.annotation.Id;
import com.webser.annotation.Table;

@Table(name = "player_info")
public class PlayerInfo extends BaseEntity<Long,Long>{
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private int age;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        id = aLong;
    }

    @Override
    public Long splitId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
