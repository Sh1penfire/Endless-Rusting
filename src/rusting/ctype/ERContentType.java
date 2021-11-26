package rusting.ctype;
public class ERContentType {


    public int ordinal = 0;
    private static int nextFreeOrdinal = 0;

    public ERContentType(String name){
        this.name = name;
        this.ordinal = nextFreeOrdinal;
        nextFreeOrdinal++;
    }

    //how you find the ERContentType
    public String name = "UNUSED";

    public String name(){
        return name;
    }

    public String toString(){
        return "ERContentType#" + ordinal;
    }
}
