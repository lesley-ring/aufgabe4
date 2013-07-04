package uni.prakinf.m4.server.protokoll;

import java.io.Serializable;

public class M4NachrichtKoordination extends M4Nachricht implements Serializable {
    public static final long serialVersionUID = 4000000003L;
    private int x;
    private int y;

    public M4NachrichtKoordination(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
