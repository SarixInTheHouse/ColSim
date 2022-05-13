

public class MouseData {

    public int button, mx, my, col, offSetX, offSetY, offCordX, offCordY;

    public MouseData() {

    }

    public MouseData(int button, int mx, int my, int col, int offSetX, int offSetY, int offCordX, int offCordY) {
        this.button = button;
        this.mx = mx;
        this.my = my;
        this.col = col;
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.offCordX = offCordX;
        this.offCordY = offCordY;
    }

    public void setbutton(int arg) {button = arg;}
    public void setmx(int arg) {mx = arg;}
    public void setmy(int arg) {my = arg;}
    public void setcol(int arg) {col = arg;}
    public void setoffSetX(int arg) {offSetX = arg;}
    public void setoffSetY(int arg) {offSetY = arg;}
    public void setoffCordX(int arg) {offCordX = arg;}
    public void setoffCordY(int arg) {offCordY = arg;}

}