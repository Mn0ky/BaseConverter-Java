package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main
{
    private static final Map<Character, Integer> CHAR_TO_HEX = new HashMap<Character, Integer>() {{
        put('A', 10);
        put('B', 11);
        put('C', 12);
        put('D', 13);
        put('E', 14);
        put('F', 15);
    }};
    private static final Map<Integer, Character> HEX_TO_CHAR = new HashMap<Integer, Character>() {{
        put(10, 'A');
        put(11, 'B');
        put(12, 'C');
        put(13, 'D');
        put(14, 'E');
        put(15, 'F');
    }};

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;

        while (continueProgram)
        {
            runConverter(scanner); // Main logic

            System.out.println("\nContinue? Y/N");
            String continueValue = scanner.nextLine().toLowerCase();

            continueProgram = continueValue.equals("yes") || continueValue.equals("y"); // Stops if false, otherwise loops back
            System.out.println();
        }
    }

    private static void runConverter(Scanner scanner)
    {
        System.out.println("Enter the number to convert:");
        String numberToConvert = scanner.nextLine().toUpperCase(); // The original numerical value

        System.out.println("Convert from (binary, octal, decimal, or hex):");
        String baseTypeOriginalStr = scanner.nextLine().toUpperCase(); // The original base type (hex, octal, or etc.)

        System.out.println("Convert to (binary, octal, decimal, or hex):");
        String baseTypeNew = scanner.nextLine().toUpperCase(); // The new, wanted base type (hex, octal, or etc.)

        String finalNumber;
        int originalBase = getBaseFromType(baseTypeOriginalStr);
        int newBase = getBaseFromType(baseTypeNew);

        if (originalBase == Integer.MAX_VALUE || newBase == Integer.MAX_VALUE) // If invalid base specified, state that and exit
        {
            System.out.println("Invalid base specified, exiting...");
            System.exit(1);
        }

        boolean usingHex = originalBase == 16 || newBase == 16; // Whether hex chars will be needed

        if (originalBase == 10) // Decimal to binary, octal, and hex
        {
            finalNumber = convertFromDecimal(numberToConvert, newBase, usingHex);
        }
        else if (newBase == 10) // Binary, octal, and hex to decimal
        {
            finalNumber = convertToDecimal(numberToConvert, originalBase, usingHex);
        }
        else // Use decimal as intermediary, convert binary/octal/hex directly to hex/octal/binary
        {
            finalNumber = convertToDecimal(numberToConvert, originalBase, usingHex);
            finalNumber = convertFromDecimal(finalNumber, newBase, usingHex);
        }

        System.out.println("Converted " + baseTypeOriginalStr + " to " + baseTypeNew + ": " + finalNumber);
    }

    // Multiply each digit by the originalBase^currentIndex and sum them up-- gives us the decimal value
    private static String convertToDecimal(String numberToConvert, int originalBase, boolean usingHex)
    {
        long finalNumber = 0;

        for (int i = 0; i < numberToConvert.length(); i++)
        {
            long multiplier = (long) Math.pow(originalBase, i); // Don't need a double's precision
            char digitChar = numberToConvert.charAt(numberToConvert.length() - i - 1);

            if (usingHex && CHAR_TO_HEX.containsKey(digitChar)) // Check if char is HEX alphabetic value
            {
                finalNumber += CHAR_TO_HEX.get(digitChar) * multiplier; // Parse digit (hex letter) then multiply
                continue;
            }

            finalNumber += Character.getNumericValue(digitChar) * multiplier; // If not hex, parse digit then multiply
        }

        return Long.toString(finalNumber); // The final decimal value
    }

    /*
    Set decimal value as quotient, continuously divide by wanted base until it reaches 0.
    Each new digit is the remainder of an operation
    */
    private static String convertFromDecimal(String numberToConvert, int newBase, boolean usingHex)
    {
        String finalNumber = " "; // Have 1 filler character so first string insertion works
        long quotient = Long.parseLong(numberToConvert);

        while (quotient != 0)
        {
            int newDigit = (int) (quotient % newBase); // A new digit, it's the remainder of the operation

            if (usingHex && HEX_TO_CHAR.containsKey(newDigit)) // Check if number is alphabetic HEX value
            {
                String newHexCharDigit = HEX_TO_CHAR.get(newDigit).toString(); // Get HEX char from number

                finalNumber = stringInsert(finalNumber, newHexCharDigit, 0);
                quotient = quotient / newBase; // Set new, lower quotient
                continue;
            }

            finalNumber = stringInsert(finalNumber, Integer.toString(newDigit), 0);
            quotient = quotient / newBase; // Set new, lower quotient
        }

        return finalNumber.trim(); // The final value, remove's initial space character
    }

    private static int getBaseFromType(String originalType)
    {
        switch (originalType)
        {
            case "BINARY":
                return 2;
            case "OCTAL":
                return 8;
            case "DECIMAL":
                return 10;
            case "HEX":
                return 16;
            default:
                return Integer.MAX_VALUE; // If no valid base was found, return max int value to stop the program
        }
    }

    // Inserts one string into another at target index
    public static String stringInsert(String string1, String string2, int index)
    {
        return string1.substring(0, index + 1) + string2 + string1.substring(index + 1);
    }
}
