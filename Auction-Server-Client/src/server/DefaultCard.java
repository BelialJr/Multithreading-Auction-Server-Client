package server;

import com.google.gson.JsonElement;

import java.util.Arrays;

public class DefaultCard {
    private String name ;
    private String height ;
    private String skin_color ;
    private String birth_year ;
    private String gender ;

    public DefaultCard(String name, String height, String skin_color, String birth_year, String gender) {
        this.name = name;
        this.height = height;
        this.skin_color = skin_color;
        this.birth_year = birth_year;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSkin_color() {
        return skin_color;
    }

    public void setSkin_color(String skin_color) {
        this.skin_color = skin_color;
    }

    public String getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(String birth_year) {
        this.birth_year = birth_year;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "DefaultCard{" +
                "name='" + name + '\'' +
                ", height='" + height + '\'' +
                ", skin_color='" + skin_color + '\'' +
                ", birth_year='" + birth_year + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String toStringV2() {
        return
                name +  "," +
                height + "," +
                skin_color +  "," +
                birth_year +  "," +
                gender ;

    }
    public static DefaultCard castToCard(String s){
        String[] data = s.split(",");
        System.out.println(s);
        System.out.println(Arrays.toString(data));
        DefaultCard result = new DefaultCard(
                data[0],data[1],data[2],data[3],data[4]
        );
        return result;
    }
}
