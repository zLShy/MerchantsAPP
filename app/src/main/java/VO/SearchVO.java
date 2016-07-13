package VO;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/28.
 */
public class SearchVO implements Serializable{
    private String count;
    private String time;
    private String type;
    private String details;
    private String buyer;
    private String phone;
    private String email;

    public SearchVO(String count, String time, String type, String details, String buyer, String phone, String email) {
        this.count = count;
        this.time = time;
        this.type = type;
        this.details = details;
        this.buyer = buyer;
        this.phone = phone;
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SearchVO{" +
                "count='" + count + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", details='" + details + '\'' +
                ", buyer='" + buyer + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
