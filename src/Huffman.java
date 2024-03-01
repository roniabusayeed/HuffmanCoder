import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;


// Custom comparator to compare to BinaryTree<Pair> nodes.
// (Used in the priority queue).
class NodeComparator implements Comparator<BinaryTree<Pair>> {
    @Override
    public int compare(BinaryTree<Pair> first, BinaryTree<Pair> second) {
        return first.getData().compareTo(second.getData());
    }
}

public class Huffman {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        // Prompt the user to enter the name of the file that contains
        // the characters and their associated probabilities.
        System.out.println("Huffman Coding");
        System.out.print("Enter the name of the file with letters and probability: ");
        String characterProbabilitiesFilename = keyboard.nextLine();

        try {
            // Build the frequency/probability table.
            PriorityQueue<BinaryTree<Pair>> characterProbabilitiesNodes
                = readCharacterProbabilities(characterProbabilitiesFilename);

            // Build the Huffman tree.
            System.out.println("\nBuilding the Huffman tree ...");
            BinaryTree<Pair> huffmanTree = buildHuffmanTree(characterProbabilitiesNodes);
            
            // Create a lookup table of characters and associated Huffman codes.
            HashMap<Character, String> encoding = findEncoding(huffmanTree);
            System.out.println("Huffman coding completed.\n");

            // Prompt the user to enter a line of text.
            System.out.print("Enter a line (uppercase letters only): ");
            String text = keyboard.nextLine();

            // Encode.
            String encodedText = encode(text, encoding);
            System.out.println("Here is the encoded line: " + encodedText);

            // Decode.
            String decodedText = decode(encodedText, huffmanTree);
            System.out.println("Here is the decoded line: " + decodedText);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    // Decodes a Huffman encoded string back to original text using the Huffman tree.
    public static String decode(String encodedText, BinaryTree<Pair> huffmanTree) {
        StringBuilder decodedTextStringBuilder = new StringBuilder();
        decode(encodedText, decodedTextStringBuilder, huffmanTree, huffmanTree);
        return decodedTextStringBuilder.toString();
    }

    // Helper recursive function for the decode method.
    private static void decode(String encodedText, StringBuilder decodedTextStringBuilder,
                               BinaryTree<Pair> currentNode, BinaryTree<Pair> huffmanTree) {
        // If this is the leaf node.
        if (currentNode.getLeft() == null && currentNode.getRight() == null) {
            decodedTextStringBuilder.append(currentNode.getData().getCharacter());
            decode(encodedText, decodedTextStringBuilder, huffmanTree, huffmanTree);
        } else if (! encodedText.isEmpty()) {
            if (encodedText.charAt(0) == '0') {
                decode(encodedText.substring(1), decodedTextStringBuilder,
                    currentNode.getLeft(), huffmanTree);
            } else if (encodedText.charAt(0) == '1') {
                decode(encodedText.substring(1), decodedTextStringBuilder,
                    currentNode.getRight(), huffmanTree);
            } else {
                decodedTextStringBuilder.append(encodedText.charAt(0));
                decode(encodedText.substring(1), decodedTextStringBuilder,
                    huffmanTree, huffmanTree);
            }
        }
    }

    // Encodes a piece of text using Huffman codes from a look-up table (generated from Huffman tree).
    public static String encode(String text, HashMap<Character, String> encoding) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            
            // For the purposes of this assignment, the character is assumed to
            // be only upper-case English letters or space.
            if (character == ' ') {
                stringBuilder.append(character);
            } else {
                stringBuilder.append(encoding.get(character));
            }
        }

        return stringBuilder.toString();
    }

    // Creates a lookup table from the Huffman Tree.
    public static HashMap<Character, String> findEncoding(BinaryTree<Pair> huffmanTree) {
        HashMap<Character, String> encoding = new HashMap<>();
        findEncoding(huffmanTree, encoding, "");
        return encoding;
    }

    // Helper recursive function to find the Huffman codes.
    private static void findEncoding(
        BinaryTree<Pair> huffmanTree,
        HashMap<Character, String> encoding,
        String prefix
    ) {
        if (huffmanTree.getLeft() == null && huffmanTree.getRight() == null) {
            encoding.put(huffmanTree.getData().getCharacter(), prefix);
        } else {
            findEncoding(huffmanTree.getLeft(), encoding, prefix + "0");
            findEncoding(huffmanTree.getRight(), encoding, prefix + "1");
        }
    }

    // Creates the Huffman tree from a priority queue (in ascending order) of
    // character-probability pairs.
    public static BinaryTree<Pair> buildHuffmanTree(
        PriorityQueue<BinaryTree<Pair>> characterProbabilitiesNodes) {
        while (characterProbabilitiesNodes.size() > 1) {
            
            // Get two nodes with the smallest probabilities. 
            
            // This works because the priority queue already enqueues them in the 
            // ascending order as defined by NodeComparator class and Comparable<Pair>
            // interface implementation.
            BinaryTree<Pair> left = characterProbabilitiesNodes.poll();
            BinaryTree<Pair> right = characterProbabilitiesNodes.poll();
            
            // Create a parent node with probability of sum of the probabilities
            // of the two children.
            BinaryTree<Pair> parent = new BinaryTree<>();

            double leftChildProbability = left.getData().getProbability();
            double rightChildProbability = right.getData().getProbability();
            double probabilitySum = leftChildProbability + rightChildProbability;
            
            parent.makeRoot(new Pair('\0', probabilitySum));
            parent.attachLeft(left);
            parent.attachRight(right);

            // Add the parent node back to the priority queue.
            characterProbabilitiesNodes.offer(parent);
        }

        return characterProbabilitiesNodes.poll();
    }

    // Reads the character probabilities from a file.
    public static PriorityQueue<BinaryTree<Pair>> readCharacterProbabilities(
        String inputFilename) throws FileNotFoundException {
        
        // Creating the frequency table.
        PriorityQueue<BinaryTree<Pair>> characterProbabilitiesNodes = 
            new PriorityQueue<>(new NodeComparator());
        
        // Open the input file for reading.
        try (Scanner reader = new Scanner(new File(inputFilename))) {
            while (reader.hasNextLine()) {
                
                // Extract and parse the character and its associated 
                // probability from each line.
                String line = reader.nextLine();
                String[] tokens = line.split("\t");
                char character = tokens[0].charAt(0);
                double probability = Double.parseDouble(tokens[1]);
                
                // Make a pair out of the character and its associated
                // probability.
                Pair pair = new Pair(character, probability);

                // Make a node out of the pair.
                BinaryTree<Pair> node = new BinaryTree<>();
                node.makeRoot(pair);
                
                // Add the node to the priority queue.
                characterProbabilitiesNodes.offer(node);
            }
        }

        return characterProbabilitiesNodes;
    }
}
