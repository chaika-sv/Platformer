package ui;

public enum UrmType {

    UNPAUSE(0),
    REPLAY(1),
    MENU(2);

    private int id;

    UrmType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
