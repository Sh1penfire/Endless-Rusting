package rusting.interfaces;

//corner piece
public interface PulseCornerpiece extends PulseInstantTransportation{

    default float reflectedRotation(float buildingAngle, float inputAngle){
        return Math.abs(inputAngle + ((buildingAngle - (inputAngle + 180)) + 180) * 2) % 360;
    }

}
