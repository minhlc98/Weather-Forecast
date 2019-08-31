package Model;

public class Weather {
    private String Date;
    private String Status;
    private String imgae;

    public String getDate() {
        return Date;
    }

    public String getStatus() {
        return Status;
    }

    public String getImgae() {
        return imgae;
    }
    public String getTemp(){
        return temp;
    }

    private String temp;

    public Weather(String date, String status, String imgae, String temp) {
        Date = date;
        Status = status;
        this.imgae = imgae;
        this.temp=temp;
    }


}
