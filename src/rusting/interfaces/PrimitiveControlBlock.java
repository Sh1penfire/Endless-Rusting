package rusting.interfaces;

import mindustry.gen.Building;
import mindustry.logic.LAccess;

public interface PrimitiveControlBlock {

    default void primitiveControl(LAccess type, double p1, double p2, double p3, double p4){

    }

    default void primitiveControl(LAccess type, Object p1, double p2, double p3, double p4){

    }

    default void rawControl(double p1, double p2, double p3, double p4){

    }

    default void exportInformationDefault(Building build){

    }

    default void exportInformation(PrimitiveControlBlock build, double p1, double p2, double p3, double p4){
        build.rawControl(p1, p2, p3, p4);
    }

}
