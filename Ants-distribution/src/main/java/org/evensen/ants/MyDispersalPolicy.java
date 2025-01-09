package org.evensen.ants;

public class MyDispersalPolicy implements DispersalPolicy {
    @Override
    public float[] getDispersedValue(final AntWorld w, final Position p) {
        float f = 0.93f; // Decay Factor
        float k = 0.5f; // How much it should spread
        float[] dispersedValues = addNeighborsValue(w, p);



        dispersedValues[0] = (1 - k) * dispersedValues[0] / 8 + k * w.getFoodStrength(p);
        dispersedValues[1] = (1 - k) * dispersedValues[1] / 8 + k * w.getForagingStrength(p);
        dispersedValues[0] *= f;
        dispersedValues[1] *= f;


        return dispersedValues;
    }

    private float[] addNeighborsValue(AntWorld w, Position p){
        int x = (int) p.getX();
        int y = (int) p.getY();

        float[] sum = new float[2];

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int nx = x + dx;
                int ny = y + dy;

                if (nx < 0) nx = 0;
                if (ny < 0) ny = 0;
                if (nx >= w.getWidth()) nx = w.getWidth() - 1;
                if (ny >= w.getHeight()) ny = w.getHeight() - 1;
                Position currentPosition = new Position(nx, ny);
                sum[0] += w.getFoodStrength(currentPosition);
                sum[1] += w.getForagingStrength(currentPosition);
            }
        }
        return sum;

    }

}

