package rusting.type;

import arc.func.Func2;
import arc.struct.Seq;
import rusting.Varsr;
import rusting.ctype.UnlockableERContent;
import rusting.ctype.DataFormat;

public class PrimitiveDataFormat extends UnlockableERContent {

    public PrimitiveDataFormat(String name) {
        super(name);
    }

    //default name
    public String name = "defaultformat";
    //name displayed in ui
    public String localizedName;
    //Data formats which you can convert to. Method of preventing converting to an incompatible datatype.
    public Seq<PrimitiveDataFormat> convertibleTo;
    //used for conditional conversion into other data formats
    public Func2<DataFormat, double[], double[]> convert;

    public String toString(){
        return localizedName == null ? name : localizedName;
    }

    public double[] convert(String param1, double[] param2){
        return convert.get(Varsr.formats.getByName(param1), param2);
    }
}
