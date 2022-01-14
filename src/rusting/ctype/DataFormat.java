package rusting.ctype;

import arc.func.Func2;
import arc.struct.Seq;
import rusting.Varsr;

//A format for information, and is readMount based on the type of format. Load method used to define actual name, and icon for ui.
public class DataFormat extends UnlockableERContent{

    //Data formats which you can convert to. Method of preventing converting to an incompatible datatype.
    public Seq<DataFormat> convertibleTo;
    //used for conditional conversion into other data formats
    public Func2<DataFormat, double[], double[]> convert;

    public DataFormat(String name) {
        super(name);
    }

    public String toString(){
        return localizedName == null ? name : localizedName;
    }

    public double[] convert(String param1, double[] param2){
        return convert.get(Varsr.formats.getByName(param1), param2);
    }

}
