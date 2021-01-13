package me.cyberdie22.apilibrary.mojang;

public enum Status {
    GREEN,
    YELLOW,
    RED
    ;

    public static Status fromString(String status)
    {
        switch (status)
        {
            case "green":
                return GREEN;
            case "yellow":
                return YELLOW;
            default:
                return RED;
        }
    }
}
