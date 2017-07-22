package sg.edu.rp.c346.eventful_organiser;

/**
 * Created by 15017523 on 7/5/2017.
 */

public class EVENT {

    private String title;
    private String description;
    private String image;
    private String location;
    private Double lat;
    private Double lng;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String head_chief;
    private String organiser;
    private String pax;
    private String status;
    private String timeStamp;
    private String eventType;

    public EVENT() {

    }

    public EVENT(String pax,
                 Double lat,
                 Double lng,
                 String title,
                 String image,
                 String status,
                 String endDate,
                 String endTime,
                 String location,
                 String startDate,
                 String startTime,
                 String organiser,
                 String timeStamp,
                 String eventType,
                 String head_chief,
                 String description) {

        this.pax = pax;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.image = image;
        this.status = status;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
        this.organiser = organiser;
        this.timeStamp = timeStamp;
        this.eventType = eventType;
        this.head_chief = head_chief;
        this.description = description;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getHead_chief() {
        return head_chief;
    }

    public void setHead_chief(String head_chief) {
        this.head_chief = head_chief;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
