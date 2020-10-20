package client;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

public class CustomCell  extends TableCell<TableRow,String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        if (!empty) {
            if (item.equals("SOLD")) {
                setStyle("-fx-background-color: " + "#6bf633");
            } else if (item.equals("BOUGHT")) {
                setStyle("-fx-background-color: " + "#f65933");
            }
        }
        this.setText(item);
    }

}
