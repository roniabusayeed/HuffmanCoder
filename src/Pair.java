public class Pair implements Comparable<Pair> {
    private char character;
    private double probability;

    public Pair(char character, double probability) {
        this.character = character;
        this.probability = probability;
    }

    public char getCharacter() {
        return character;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return character + " " + probability;
    }

    public int compareTo(Pair other) {
        return Double.compare(this.getProbability(), other.getProbability());
    }
}
