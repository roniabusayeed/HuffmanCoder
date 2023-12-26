import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
    private T data;
    private Node<T> left;
    private Node<T> right;

    public Node(T data, Node<T> left, Node<T> right) {
        setData(data);
        setLeft(left);
        setRight(right);
    }

    public Node(T data) {
        this(data, null, null);
    }

    public T getData() {
        return data;
    }

    public Node<T> getLeft() {
        return left;
    }

    public Node<T> getRight() {
        return right;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setLeft(Node<T> left) {
        this.left = left;
    }

    public void setRight(Node<T> right) {
        this.right = right;
    }

    @Override
    public int compareTo(Node<T> other) {
        return this.getData().compareTo(other.getData());
    }
}

class Pair<Key, Value extends Comparable<Value>> implements Comparable<Pair<Key, Value>> {
    private Key key;
    private Value value;

    public Pair(Key key, Value value) {
        setKey(key);
        setValue(value);
    }

    public Key getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public int compareTo(Pair<Key, Value> other) {
        return this.getValue().compareTo(other.getValue());
    }
}

class NodeComparator<T extends Comparable<T>> implements Comparator<Node<T>> {
    @Override
    public int compare(Node<T> first, Node<T> second) {
        return first.compareTo(second);
    }
}

public class Program {
    public static void main(String[] args) {
        byte[] bytes = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        byte[] encodedBytes = encode(bytes);
    }

    private static byte[] encode(byte[] data) {

        // Build frequency table.
        HashMap<Byte, Integer> frequencyTable = buildFrequencyTable(data);

        // Build Huffman tree.
        NodeComparator<Pair<Byte, Integer>> nodeComparator = new NodeComparator<>();
        PriorityQueue<Node<Pair<Byte, Integer>>> priorityQueue = buildPriorityQueue(
            frequencyTable, nodeComparator);
        Node<Pair<Byte, Integer>> huffmanTree = buildHuffmanTree(priorityQueue);


        return null;
    }

    private static Node<Pair<Byte, Integer>> buildHuffmanTree(
        PriorityQueue<Node<Pair<Byte, Integer>>> nodePriorityQueue) {

        while (nodePriorityQueue.size() > 1) {
            Node<Pair<Byte, Integer>> left = nodePriorityQueue.poll();
            Node<Pair<Byte, Integer>> right = nodePriorityQueue.poll();

            int leftFrequency = left.getData().getValue();
            int rightFrequency = right.getData().getValue();
            int frequencySum = leftFrequency + rightFrequency;

            Pair<Byte, Integer> data = new Pair<>(null, frequencySum);
            Node<Pair<Byte, Integer>> parent = new Node<>(data, left, right);

            nodePriorityQueue.offer(parent);
        }

        return nodePriorityQueue.poll();
    }

    private static HashMap<Byte, Integer> buildFrequencyTable(byte[] data) {
        HashMap<Byte, Integer> frequencyTable = new HashMap<>();
        for (byte b : data) {
            int count = frequencyTable.getOrDefault(b, 0) + 1;
            frequencyTable.put(b, count);
        }

        return frequencyTable;
    }

    private static PriorityQueue<Node<Pair<Byte, Integer>>> buildPriorityQueue(
        HashMap<Byte, Integer> frequencyTable,
        NodeComparator<Pair<Byte, Integer>> nodeComparator)
    {
        PriorityQueue<Node<Pair<Byte, Integer>>> priorityQueue = new PriorityQueue<>(nodeComparator);
        for (Map.Entry<Byte, Integer> entry : frequencyTable.entrySet()) {

            // Create a pair out of each entry.
            Byte key = entry.getKey();
            Integer value = entry.getValue();
            Pair<Byte, Integer> pair = new Pair<>(key, value);

            // Create a node out of each pair.
            Node<Pair<Byte, Integer>> node = new Node<>(pair);

            // Insert the node into the priority queue.
            priorityQueue.offer(node);
        }

        return priorityQueue;
    }
}
