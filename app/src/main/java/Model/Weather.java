package Model;

public class Weather {
    protected String Date;
    protected String Status;
    protected String imgae;

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

    protected String temp;

    public Weather(String date, String status, String imgae, String temp) {
        Date = date;
        Status = status;
        this.imgae = imgae;
        this.temp=temp;
    }


}
