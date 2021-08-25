package com.abhishek.canteen;

public class model {
    private String name;
    private String price;
    private String imgurl;

    public model()
    {

    }

    public model(String name, String price,String imgurl){
        this.name=name;
        this.price=price;
        this.imgurl=imgurl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImgurl() {
        return imgurl;
    }
}
