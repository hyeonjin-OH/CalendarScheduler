package hyeonjin.calendar.domain.dto;

public enum Role {

    ADMIN(0), MANAGER(1), USER(2);
    private final int value;
    Role(int val){
        this.value = val;
    }
    public int intValue(){
        return value;
    }

    public static Role valueOf(int val){
        switch (val){
            case 1: return ADMIN;
            case 2: return MANAGER;
            case 3: return USER;
            default: throw new AssertionError("Unknown Role ");
        }
    }
}
