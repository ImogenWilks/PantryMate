package com.example.pantrymate;

public class Items {
    private String itemName;
    private String itemInfo;
    private String expiryT;
    private String dateAdded;


    public Items(String text1, String text2, String text3, String text4)
    {
        itemName=text1;
        expiryT=text2;
        itemInfo = text3;
        dateAdded = text4;


    }

    public String getText1()
    {
        return itemName;
    }

    public String getText2()
    {
        return expiryT;
    }
    public String getText3()
    {
        return itemInfo;
    }
    public String getDateAdded() {return dateAdded;}
}
