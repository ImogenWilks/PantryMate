package com.example.pantrymate;

public class Items {
    private String itemName;
    private String itemInfo;


    public Items(String text1, String text2)
    {
        itemName=text1;
        itemInfo=text2;

    }

    public String getText1()
    {
        return itemName;
    }

    public String getText2()
    {
        return itemInfo;
    }
}
