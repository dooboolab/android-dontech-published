package org.hyochan.dontech.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class Gagebu implements Parcelable{

    private int _id;
    private String name;
    private String date;
    private String category;
    private int money;
    private String detail;
    private String address;
    private double locationX;
    private double locationY;
    private String img;

    public Gagebu() {
    }

    public Gagebu(int _id, String name, String date, String category, int money, String detail, String address, double locationX, double locationY, String img) {
        this._id = _id;
        this.name = name;
        this.date = date;
        this.category = category;
        this.money = money;
        this.detail = detail;
        this.address = address;
        this.locationX = locationX;
        this.locationY = locationY;
        this.img = img;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(category);
        dest.writeInt(money);
        dest.writeString(detail);
        dest.writeString(address);
        dest.writeDouble(locationX);
        dest.writeDouble(locationY);
        dest.writeString(img);
    }

    public Gagebu(Parcel in){
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in){
        this._id = in.readInt();
        this.name = in.readString();
        this.date= in.readString();
        this.category = in.readString();
        this.money = in.readInt();
        this.detail = in.readString();
        this.address = in.readString();
        this.locationX = in.readDouble();
        this.locationY = in.readDouble();
        this.img = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Gagebu createFromParcel(Parcel source) {
            return new Gagebu(source);
        }

        @Override
        public Gagebu[] newArray(int size) {
            return new Gagebu[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void updateAll(Gagebu gagebu){
        this._id = gagebu.get_id();
        this.name = gagebu.getName();
        this.date = gagebu.getDate();
        this.category = gagebu.getCategory();
        this.money = gagebu.getMoney();
        this.detail = gagebu.getDetail();
        this.address = gagebu.getAddress();
        this.locationX = gagebu.getLocationX();
        this.locationY = gagebu.getLocationY();
        this.img = gagebu.getImg();
    }

    @Override
    public String toString() {
        return "Gagebu{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", category='" + category + '\'' +
                ", money=" + money +
                ", detail='" + detail + '\'' +
                ", address='" + address + '\'' +
                ", locationX=" + locationX +
                ", locationY=" + locationY +
                ", img='" + img + '\'' +
                '}';
    }
}
