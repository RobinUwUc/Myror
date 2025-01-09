package org.evensen.ants;

import java.util.Random;

public class FoodSource {
    public Position getPosition() {
        return this.position;


    }

    public Position position;
        public int radie;
        private static Random rand = new Random();
        private int myrmumsbitar;

        FoodSource(Position position, int radie, int myrmumsbitar) {
            this.position = position;
            this.radie = radie;
            this.myrmumsbitar = myrmumsbitar;
        }

        public void takeFood() {
            this.myrmumsbitar--;
        }

        public boolean containsFood() {
            return this.myrmumsbitar > 0;
        }

        public boolean isFood(Position p, int radie) {
            return this.position.isWithinRadius(p, radie);
        }

        public static FoodSource newFoodSource(int width, int height) {
            int x = rand.nextInt(0,width);
            int y = rand.nextInt(0,height);
            return new FoodSource(new Position(x,y), 15, 1000);
        }
}
