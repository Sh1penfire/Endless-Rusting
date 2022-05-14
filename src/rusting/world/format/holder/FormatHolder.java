package rusting.world.format.holder;

import arc.func.Func2;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import rusting.ctype.DataFormat;

//used to hold multiple data formats
public class FormatHolder {

    public Seq<DataFormat> formats = Seq.with(
        new DataFormat("directional") {{

            convert = new Func2<DataFormat, double[], double[]>() {

                @Override
                public double[] get(DataFormat param1, double[] param2) {
                    double[] returnArray = new double[]{0.0, 0.0, 0.0, 0.0};
                    switch (param1.name) {
                        case "rotational":
                            float x = (float) param2[0], y = (float) param2[1];
                            returnArray[0] = Mathf.dst(x, y);
                            returnArray[1] = Mathf.angle(x, y);
                            returnArray[2] = param2[2];
                            returnArray[3] = param2[3];
                            break;
                        default:
                            returnArray = param2;
                    }
                    return returnArray;
                }
            };
        }},
        new DataFormat("rotational") {{

            convert = new Func2<DataFormat, double[], double[]>() {

                @Override
                public double[] get(DataFormat param1, double[] param2) {
                    double[] returnArray = new double[]{0.0, 0.0, 0.0, 0.0};
                    switch (param1.name) {
                        case "directional":
                            Tmp.v1.trns((float) param2[1], (float) param2[0]);
                            returnArray[0] = Tmp.v1.x;
                            returnArray[1] = Tmp.v1.y;
                            returnArray[2] = param2[2];
                            returnArray[3] = param2[3];
                            break;
                        default:
                            returnArray = param2;
                    }
                    return returnArray;
                }
            };
        }}
    );

    public DataFormat getByName(String name){
        DataFormat[] returnObject = {null};
        formats.each(e -> {
            if(e.name.equals(name)) returnObject[0] = e;
        });
        return returnObject[0];
    }

    public void load(){
        formats.each(d -> d.load());
    }
}
