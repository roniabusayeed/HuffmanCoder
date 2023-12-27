import java.nio.charset.StandardCharsets;
import java.util.*;

public class Program {
    public static void main(String[] args) {
        byte[] bytes = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        byte[] encodedBytes = encode(bytes);

        System.out.println(Arrays.toString(encodedBytes));
    }

    private static void generateCode(Node<Pair<Byte, Integer>> huffmanTree,
                                     HashMap<Byte, String> huffmanCodes) {

        // Recursive helper function.
        generateCode(huffmanTree, huffmanCodes, "");
    }

    private static void generateCode(Node<Pair<Byte, Integer>> huffmanTree,
                                     HashMap<Byte, String> huffmanCodes,
                                     String huffmanCode) {

        if (huffmanTree.isLeaf()) {
            huffmanCodes.put(huffmanTree.getData().getKey(), huffmanCode);
        } else {
            generateCode(huffmanTree.getLeft(), huffmanCodes, huffmanCode + "0");
            generateCode(huffmanTree.getRight(), huffmanCodes, huffmanCode + "1");
        }
    }

    private static byte[] encode(byte[] data) {

        // Build frequency table.
        HashMap<Byte, Integer> frequencyTable = buildFrequencyTable(data);

        // Build Huffman tree.
        NodeComparator<Pair<Byte, Integer>> nodeComparator = new NodeComparator<>();
        PriorityQueue<Node<Pair<Byte, Integer>>> nodePriorityQueue = buildPriorityQueue(
            frequencyTable, nodeComparator);
        Node<Pair<Byte, Integer>> huffmanTree = buildHuffmanTree(nodePriorityQueue);

        // Generate Huffman codes.
        HashMap<Byte, String> huffmanCodes = new HashMap<>();
        generateCode(huffmanTree, huffmanCodes);

        StringBuilder encodedData = new StringBuilder();
        for (byte b : data) {
            encodedData.append(huffmanCodes.get(b));
        }

        ArrayList<Byte> encodedBytes = new ArrayList<>();
        int length = encodedData.length() / 8;
        int lastByteBitCount = encodedData.length() % 8;

        // The first byte encodes the count of encoded bits in the last byte.
        encodedBytes.add((byte)lastByteBitCount);

        // Encode the complete bytes.
        for (int i = 0; i < length; i++) {
            int startIndex = 8 * i;
            int endIndex = startIndex + 8;
            String byteString = encodedData.substring(startIndex, endIndex);
            encodedBytes.add((byte)Integer.parseInt(byteString, 2));
        }

        // Encode the last/incomplete byte (if any)
        if (lastByteBitCount > 0) {
            int startIndex = length * 8;
            String byteString = encodedData.substring(startIndex);
            encodedBytes.add((byte)Integer.parseInt(byteString, 2));
        }

        // Convert ArrayList of Bytes into byte[].
        byte[] encodedBytesArray = new byte[encodedBytes.size()];
        for (int i = 0; i < encodedBytes.size(); i++) {
            encodedBytesArray[i] = encodedBytes.get(i);
        }
        return encodedBytesArray;
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

    @Override
    public String toString() {
        return getData().toString();
    }

    public boolean isLeaf() {
        return getLeft() == null && getRight() == null;
    }

    public static <T extends Comparable<T>> void leverOrderTraversal(Node<T> root) {
        Queue<Node<T>> queue = new LinkedList<>();

        if (root != null) {
            queue.offer(root);
        }

        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            System.out.println(node);
            if (node.getLeft() != null) {
                queue.offer(node.getLeft());
            }
            if (node.getRight() != null) {
                queue.offer(node.getRight());
            }
        }
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

    @Override
    public String toString() {
        return "(" + getKey() + ", " + getValue() + ")";
    }
}

class NodeComparator<T extends Comparable<T>> implements Comparator<Node<T>> {
    @Override
    public int compare(Node<T> first, Node<T> second) {
        return first.compareTo(second);
    }
}
