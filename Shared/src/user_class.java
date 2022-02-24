/**
 * enums for the user classes
 */
public enum user_class {
    ADMIN(1, "ADMIN"),
    MEMBER(2, "MEMBER");

    private final int id;
    private final String name;

    user_class(int i, String s) {
        this.id=i;
        name = s;
    }

    public int getId() {
        return id;
    }
    public String print(){
        return name;
    }
    static public String[] array(){
        return new String[] {ADMIN.print(), MEMBER.print()};
    }
    static public int convert(String s){
        if (s.equals(ADMIN.print())){
            return ADMIN.getId();
        }    else if(s.equals(MEMBER.print())){
            return MEMBER.getId();
        }
        return 0;
    }
}
