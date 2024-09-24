import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//Daron Baltazar - Git Hub Assignment 
// Enumeration for card validation results
enum ValidationResult { //make life easier - learned how helpful these are while working on the unity project
    VALID, INVALID_NAME, INVALID_COST, Daron_is_The_best
}

public class Main {
    public static void main(String[] args) {
        // Initialize variables
        int totalEnergyCost = 0;
        int cardCount = 0;
        int deckID = (int) (Math.random() * 900000000); // Generate a random 9-digit deck ID
        Map<Integer, Integer> costHistogram = new HashMap<>(); // To keep track of energy costs
        List<String> invalidCards = new ArrayList<>(); // To store invalid cards

        //get user input
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path for the deck (e.g., deck.txt): ");
        String filePath = scanner.nextLine();

        // Try to read the deck file given by user
        try {
            File deckFile = new File(filePath);
            Scanner fileReader = new Scanner(deckFile);

            // Read each line in the file
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] parts = line.split(":"); // Split line into card name and cost

                // Check if the line has both card name and cost
                if (parts.length == 2) {
                    String cardName = parts[0].trim(); //trim takes away the dead space like spaces & tab
                    try {
                        int cost = Integer.parseInt(parts[1].trim()); // Parse cost
                        // Makes sure  card name and cost are good using switch
                        switch (validateCard(cardName, cost)) {
                            case VALID -> { //when the card is valid, each of these things will happen
                                totalEnergyCost += cost; // Add to total cost
                                cardCount++; // Increment card count
                                costHistogram.put(cost, costHistogram.getOrDefault(cost, 0) + 1); // Update histogram
                            }
                            case INVALID_NAME, INVALID_COST -> invalidCards.add(line); // Add to invalid cards if invalid
                        }
                    } catch (NumberFormatException e) {
                        invalidCards.add(line); // Add to invalid cards if cost is not a number
                    }
                } else {
                    invalidCards.add(line); // Add to invalid cards if format is wrong
                }
            }
            fileReader.close(); // Close the file reader

            // Generate report based on the number of cards
            if (cardCount > 1000 || invalidCards.size() > 10) { //bounds
                generateVoidReport(deckID, invalidCards);
            } else {
                generateReport(deckID, totalEnergyCost, costHistogram, invalidCards);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        }

        // Example histogram to show how it works
        int[] sampleCosts = {1, 2, 1, 3, 2, 5, 4, 3, 6};
        int m = 7; // There are 7 possible cost values (0 to 6)
        System.out.println(Arrays.toString(histogram(sampleCosts, m))); // Print histogram of sample costs
    }


////////////////////////////////////////////////////////////////////////////////


    // Validate card name and cost
    public static ValidationResult validateCard(String cardName, int cost) {
        if (cardName.isEmpty()) {
            return ValidationResult.INVALID_NAME; // Empty card name
        } else if (cost >= 0 && cost <= 6) { // Valid costs
            return ValidationResult.VALID;
        } else {
            return ValidationResult.INVALID_COST; // Invalid cost
        }
    }


////////////////////////////////////////////////////////////////////////////////


    // Create a histogram of card costs
    public static int[] histogram(int[] a, int m) {
        int[] histoArray = new int[m]; // Create an array for the histogram
        for (int cost : a) {
            if (cost >= 0 && cost < m) {
                histoArray[cost] += 1; // Count occurrences of each cost
            }
        }
        return histoArray; // Return the histogram
    }


////////////////////////////////////////////////////////////////////////////////


    // Generate a report for valid cards
    public static void generateReport(int deckID, int totalEnergyCost, Map<Integer, Integer> costHistogram, List<String> invalidCards) {
        String fileName = "SpireDeck" + deckID + ".txt"; // Report file name

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Deck ID: " + deckID + "\n");
            writer.write("Total Energy Cost: " + totalEnergyCost + "\n");

            // Write the histogram to the report
            writer.write("Energy Cost Histogram:\n"); //header
            for (int cost = 0; cost <= 6; cost++) { //goes through all energy costs 0-6
                //for each energy, write a line with its value  of how many times it was used.
                //getOrDefault returns 0 for cards with none of that energy
                writer.write(cost + " energy: " + costHistogram.getOrDefault(cost, 0) + "\n");

            }

            // Write invalid cards if there are any
            if (!invalidCards.isEmpty()) { //checks to see if it is not empty
                writer.write("Invalid Cards:\n");
                for (String invalidCard : invalidCards) { //for-each loop to parse through invalid cards
                    writer.write(invalidCard + "\n");
                }
            }

            System.out.println("Report generated: " + fileName); //tells user report was generated
        } catch (IOException e) {          //If it couldn't write it
            System.out.println("Error writing the report.");
            e.printStackTrace();
        }
    }


////////////////////////////////////////////////////////////////////////////////


    // Generate a void report if the deck is invalid
    public static void generateVoidReport(int deckID, List<String> invalidCards) {
        String fileName = "SpireDeck" + deckID + "(VOID).txt"; // Void report file name

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("VOID\n");
            writer.write("Invalid Cards: " + invalidCards.size() + "\n");

            System.out.println("Void report generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing the void report.");
            e.printStackTrace();
        }
    }
}
