package server;

import com.google.gson.JsonElement;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static DefaultCard castToCard(String s){
        String[] data = s.split(",");
        DefaultCard result = new DefaultCard(
                data[0],data[1],data[2],data[3],data[4]
        );
        return result;
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
    public List<String> generateListView(){
        List<String> list = new ArrayList<>();
        list.add( String.format("%-20s", "Name  : ") + this.name);
        list.add(String.format("%-20s", "Height : ") + this.height);
        list.add(String.format("%-20s", "Skin_color :  ") + this.skin_color);
        list.add(String.format("%-20s", "Birth_year : ") + this.birth_year);
        list.add( String.format("%-20s","Gender : " ) + this.gender);
        return list;
    }


}
