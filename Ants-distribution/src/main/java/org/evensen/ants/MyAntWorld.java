package org.evensen.ants;



public class MyAntWorld implements AntWorld {
    public static final float AMOUNT = 0.1f;
    private final int width;
    private final int height;
//    private final Position foodSource;
    public float[][] foodPhermones;
    private float[][] foragingPhermones;
    private FoodSource[] foodSources;
    private final int NUM_FOOD_SOURCES;
    boolean[][] foodMatrix;
    private final DispersalPolicy dispersalPolicy;



    public MyAntWorld(int width, int height, final int food_Num, DispersalPolicy dispersalPolicy) {
        this.width = width;
        this.height = height;
        this.NUM_FOOD_SOURCES = food_Num;
        this.dispersalPolicy = dispersalPolicy;
        this.foodPhermones = new float[this.width][this.height];
        this.foragingPhermones = new float[this.width][this.height];


        this.foodSources = new FoodSource[this.NUM_FOOD_SOURCES];
        this.foodMatrix = new boolean[this.width][this.height];

        for (int j = 0; j < this.NUM_FOOD_SOURCES; j++) {
            this.foodSources[j] = FoodSource.newFoodSource(this.width,this.height);
        }
        updateFoodMatrix();
    }

    void updateFoodMatrix() {
        for (int i = 0; i < this.foodMatrix.length; i++) {
            for (int j = 0; j < this.foodMatrix[i].length; j++) {
                this.foodMatrix[i][j] = false;
                for (int k = 0; k < this.foodSources.length; k++) {
                    if (this.foodSources[k].isFood(new Position(i,j), 15)) {
                        this.foodMatrix[i][j] = true;
                        break;
                    }
                }
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }



    @Override
    public boolean isObstacle(final Position p) {
        if (!p.isInBounds(this.width, this.height)) {
            return true;
        }
        return false;
    }

    @Override
    public void dropForagingPheromone(final Position p, final float amount) {
        int x = (int) p.getX();
        int y = (int) p.getY();

       if (x>= 0 && x < this.width && y >= 0 && y < this.height) {
           this.foragingPhermones[x][y] = Math.min(this.foragingPhermones[x][y] += amount, 1.0f);
       }

    }

    @Override
    public void dropFoodPheromone(final Position p, final float amount) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        if (x>= 0 && x < this.width && y >= 0 && y < this.height) {
            this.foodPhermones[x][y] = Math.min(this.foodPhermones[x][y] += amount, 1.0f);
        }

    }

    @Override
    public void dropFood(final Position p) {

    }

    @Override
    public void pickUpFood(final Position p) {
        for (int i = 0;i < this.foodSources.length; i++) {
            if (this.foodSources[i].isFood(p, 16)) {
                this.foodSources[i].takeFood();
                if (!this.foodSources[i].containsFood()) {
                    this.foodSources[i] = FoodSource.newFoodSource(this.width, this.height);
                }
                updateFoodMatrix();
            }
        }
    }

    @Override
    public float getDeadAntCount(final Position p) {
        return 0;
    }

    @Override
    public float getForagingStrength(final Position p) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        if (x>= 0 && x < this.width && y >= 0 && y < this.height) {
            return this.foragingPhermones[x][y];
        } else {
            return 0.0f;
        }
    }

    @Override
    public float getFoodStrength(final Position p) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        return this.foodPhermones[x][y];
    }

    @Override
    public boolean containsFood(final Position p) {
        return this.foodMatrix[(int)p.getX()][(int)p.getY()];
    }

    @Override
    public long getFoodCount() {
        return 0;
    }

    @Override
    public boolean isHome(final Position p) {
        Position home = new Position(this.width - 1, this.height / 2.0f);
        return p.isWithinRadius(home, 20);
    }
    public void selfContainedDisperse() {
        for(FoodSource source : this.foodSources) {
            dropFoodPheromone(source.getPosition(), AMOUNT);
        }
    }

    @Override
    public void dispersePheromones() {
        float[][] tmpP = new float[this.width][this.height];
        float[][] tmpF = new float[this.width][this.height];

        for(int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                float[] phermonesLevel = this.dispersalPolicy.getDispersedValue(this,new Position(x,y));
                tmpP[x][y] = phermonesLevel[0];
                tmpF[x][y] = phermonesLevel[1];
            }
        }
        this.foodPhermones = tmpP;
        this.foragingPhermones = tmpF;

        selfContainedDisperse();
    }

    @Override
    public void setObstacle(final Position p, final boolean add) {
    }

    @Override
    public void hitObstacle(final Position p, final float strength) {
    }
}


