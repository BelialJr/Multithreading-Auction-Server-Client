package client.UxCardButtons;

public class DefaultCard {
    private String name ;
    private String height ;
    private String skin_color ;
    private String birth_year ;
    private String gender ;
    private boolean isDisable;

    public boolean isDisable() {
        return isDisable;
    }

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
    public String[] generateListView(){
        String[] res = new String[5];
        res[0] = String.format("%-15s","|")+String.format("%-25s", "Name  :") +    String.format("%-20s", this.name) ;
        res[1] =String.format("%-15s","|")+String.format("%-26s", "Height :")  +   String.format("%-20s", this.height );
        res[2] =String.format("%-15s","|")+String.format("%-25s", "Skin_color :") + String.format("%-20s", this.skin_color );
        res[3] =String.format("%-15s","|")+String.format("%-25s", "Birth_year :")  + String.format("%-20s",  this.birth_year ) ;
        res[4] =String.format("%-15s","|")+String.format("%-25s", "Gender :" ) +  String.format("%-20s", this.gender );
        return res;
    }


    public void setDisable(boolean b) {
        isDisable = b;
    }
}
