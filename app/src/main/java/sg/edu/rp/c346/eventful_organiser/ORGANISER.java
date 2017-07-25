package sg.edu.rp.c346.eventful_organiser;

/**
 * Created by 15017420 on 3/6/2017.
 */

public class ORGANISER {

    String email;
    String image;
    String password;
    String status;
    String user_name;
    String acra;
    String site;
    String description;
    String business_type;
    String address;
    Integer contact_num;
    private Double lat;
    private Double lng;

    public ORGANISER() {

    }

    public ORGANISER(String email, String image, String password, String status, String user_name, String acra, String site, String description, String business_type, String address, Double lat, Double lng, Integer contact_num) {
        this.email = email;
        this.image = image;
        this.password = password;
        this.status = status;
        this.user_name = user_name;
        this.acra = acra;
        this.site = site;
        this.description = description;
        this.business_type = business_type;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.contact_num = contact_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAcra() {
        return acra;
    }

    public void setAcra(String acra) {
        this.acra = acra;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(String business_type) {
        this.business_type = business_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getContact_num() {
        return contact_num;
    }

    public void setContact_num(Integer contact_num) {
        this.contact_num = contact_num;
    }
}
