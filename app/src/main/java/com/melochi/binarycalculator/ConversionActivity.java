package com.melochi.binarycalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ConversionActivity extends AppCompatActivity {
    //TODO : put extras for the last selected system for againBtn onClickListener

    private double originalNum;
    private String type;
    private int position;
    private String originalHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        //The "convert again" button that returns to the Main Activity
        Button againBtn = (Button) findViewById(R.id.againBtn);
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goBackIntent);
            }
        });

        //shows the number that the user input
        TextView originalNumTextView = (TextView) findViewById(R.id.originalNumTextView);
        //formats up to 15 decimal places
        DecimalFormat fmt = new DecimalFormat("0.###############");


        if (getIntent().hasExtra("com.melochi.binarycalculator.NUMBER")) { //if number system was DECIMAL, BINARY, OR OCTAL
            originalNum = getIntent().getExtras().getDouble("com.melochi.binarycalculator.NUMBER");
            type = getIntent().getExtras().getString("com.melochi.binarycalculator.BASE");
            String originalNumText = "Conversions for " + (fmt.format(originalNum)) + ": ";
            originalNumTextView.setText(originalNumText);
        } else if (getIntent().hasExtra("com.melochi.binarycalculator.HEX")) { //if number system was HEXADECIMAL
            originalHex = getIntent().getExtras().getString("com.melochi.binarycalculator.HEX");
            type = getIntent().getExtras().getString("com.melochi.binarycalculator.BASE");
            String originalNumText = "Conversions for " + originalHex + ": ";
            originalNumTextView.setText(originalNumText);
        } else {
            String noNumText = "You did not input a number or an error occurred. Please try again.";
            originalNumTextView.setText(noNumText);
        }

        //TextViews for each conversion
        TextView firstConvTextView = (TextView) findViewById(R.id.firstConvTextView);
        TextView secondConvTextView = (TextView) findViewById(R.id.secondConvTextView);
        TextView thirdConvTextView = (TextView) findViewById(R.id.thirdConvTextView);

        int originalInt = (int) originalNum;

        if (type.equals("Decimal (Base 10)")) { //if the user chooses Base 10
            String binary = !hasDecimal(originalNum) ? "Binary: " + Integer.toBinaryString(originalInt)
                    : "Binary: " + Integer.toBinaryString(originalInt) + fractionConversion(originalNum, 2);
            firstConvTextView.setText(binary);

            String octal = !hasDecimal(originalNum) ? "Octal: " + Integer.toOctalString(originalInt)
                    : "Octal: " + Integer.toOctalString(originalInt) + fractionConversion(originalNum, 8);
            secondConvTextView.setText(octal);

            String hexa = !hasDecimal(originalNum) ? "Hexadecimal: " + Integer.toHexString(originalInt)
                    : "Hexadecimal: " + Integer.toHexString(originalInt) + fractionConversion(originalNum, 16);
            thirdConvTextView.setText(hexa);

        } else if (type.equals("Binary (Base 2)")) { //if the user chooses Base 2
            String originalStr = Integer.toString(originalInt);
            int dec = Integer.parseInt(originalStr, 2);
            String decimal = !hasDecimal(originalNum) ? "Decimal: " + dec
                    : "Decimal: " + (dec + convertToDecimaledDecimal(originalNum, 2));
            firstConvTextView.setText(decimal);

            String octal = !hasDecimal(originalNum) ? "Octal: " + Integer.toOctalString(dec)
                    : "Octal: " + Integer.toOctalString(dec) + binaryFractionConversion(originalNum, 3);
            secondConvTextView.setText(octal);

            String hexa = !hasDecimal(originalNum) ? "Hexadecimal: " + Integer.toHexString(dec)
                    : "Hexadecimal: " + Integer.toHexString(dec) + binaryFractionConversion(originalNum, 4);
            thirdConvTextView.setText(hexa);
        } else if (type.equals("Octal (Base 8)")) { //if the user chooses Base 8
            String originalStr = Integer.toString(originalInt);

            int dec = Integer.parseInt(originalStr, 8);
            String decimal = !hasDecimal(originalNum) ? "Decimal: " + dec
                    : "Decimal: " + (dec + convertToDecimaledDecimal(originalNum, 8));
            firstConvTextView.setText(decimal);

            String binary = !hasDecimal(originalNum) ? "Binary: " + Integer.toBinaryString(dec)
                    : "Binary: " + Integer.toBinaryString(dec) + octalFractionConversion(originalNum);
            secondConvTextView.setText(binary);

            double convertedBinary = Double.parseDouble(octalFractionConversion(originalNum));

            String hexa = !hasDecimal(originalNum) ? "Hexadecimal: " + Integer.toHexString(dec)
                    : "Hexadecimal: " + Integer.toHexString(dec) + binaryFractionConversion(convertedBinary, 4);
            thirdConvTextView.setText(hexa);
        } else if (type.equals("Hexadecimal (Base 16)")) { //if the user chooses Base 16
            if (!hasPeriod(originalHex)) { //and inputs an integer
                int dec = Integer.parseInt(originalHex, 16);
                String decimal = "Decimal: " + dec;
                firstConvTextView.setText(decimal);

                String binary = "Binary: " + Integer.toBinaryString(dec);
                secondConvTextView.setText(binary);

                String octal = "Octal: " + Integer.toOctalString(dec);
                thirdConvTextView.setText(octal);
            } else {
                String hexInt = getHexInteger(originalHex);
                int dec = Integer.parseInt(hexInt, 16);

                String decimal = "Decimal: " + (dec + convertToDecimaledDecimal(originalHex, 16));
                firstConvTextView.setText(decimal);

                String binary = "Binary: " + Integer.toBinaryString(dec) + hexFractionConversion(originalHex);
                secondConvTextView.setText(binary);

                double convertedBinary = Double.parseDouble(hexFractionConversion(originalHex));

                String octal = "Octal: " + Integer.toOctalString(dec) + binaryFractionConversion(convertedBinary, 3);
                thirdConvTextView.setText(octal);
            }
        }
    }

    /* SUPPORTING METHODS FOR FLOATING POINT CONVERSIONS */

    //returns true if the floating point has meaningful decimal values
    private boolean hasDecimal(double num) {
        return num != Math.floor(num);
    }

    //returns true if the String representation of the floating point has a decimal
    private boolean hasPeriod(String s) {
        return s.contains(".");
    }

    //converts from base _radix_ to base 10 (with fraction)
    private static double convertToDecimaledDecimal(double num, int radix) {
        String s = getDecimalPartWODecimal(num);
        double converted = 0;

        for (int i = 0; i < s.length(); i++) {
            int digit = Integer.parseInt(s.charAt(i) + "");
            converted += digit * (Math.pow(radix, -1*(i+1)));
        }
        return converted;
    }

    // converts to base 10 from base <radix>, given a String representation of the number to convert
    private static double convertToDecimaledDecimal(String num, int radix) {
        String s = getDecimalPartWODecimal(num);
        double converted = 0;

        for (int i = 0; i < s.length(); i++) {
            int digit = Character.getNumericValue(s.charAt(i));
            converted += digit * (Math.pow(radix, -1*(i+1)));
        }
        return converted;
    }

    // converts to base <radix> from base 10, given a double representation of the number to convert
    // process: multiply the post-decimal number by the radix, keep the resulting
    // pre-decimal number, repeat until the result = 0
    private static String fractionConversion(double num, int radix) {
        double oldFraction = getDecimalPart(num);

        int beforeDecimal = 0;
        double newFraction = oldFraction * radix;
        StringBuilder sb2 = new StringBuilder(".");
        String conv = "";

        while (newFraction != 0.0 && conv.length() < 10) {
            beforeDecimal = (int) newFraction;
            if (beforeDecimal >= 10 && beforeDecimal <= 15) {
                switch (beforeDecimal) {
                    case 10:
                        sb2.append("a");
                        break;
                    case 11:
                        sb2.append("b");
                        break;
                    case 12:
                        sb2.append("c");
                        break;
                    case 13:
                        sb2.append("d");
                        break;
                    case 14:
                        sb2.append("e");
                        break;
                    case 15:
                        sb2.append("f");
                        break;
                }
            } else {
                sb2.append(beforeDecimal);
            }
            newFraction = (newFraction - beforeDecimal) * radix;
            conv = sb2.toString();
        }

        return conv;
    }

    //returns the post-decimal portion of a number
    private static double getDecimalPart(double num) {
        StringBuilder sb = new StringBuilder("0.");
        String total = Double.toString(num);
        int dot = total.indexOf(".");
        //find the part after the decimal
        String fraction = total.substring(dot + 1, total.length());
        sb.append(fraction);
        return Double.parseDouble(sb.toString());
    }

    //converts to base 2^interval from base 2
    private static String binaryFractionConversion(double num, int interval) {
        //0.1101010 --> 11010100
        String adjustedInt = getDecimalPartWODecimal(num);
        StringBuilder sb = new StringBuilder(adjustedInt);
        String rawString = forceMultiple(sb, interval);

        StringBuilder converted = new StringBuilder(".");
        //takes a substring of the whole string in lengths defined by the interval,
        //converts that substring and adds it to the SB.
        for (int i = 0; i < rawString.length(); i+=interval) {
            //get correctly sized substring
            String sub = rawString.substring(i, i+interval);
            //convert the substring from binary to decimal & make it a string
            int number = Integer.parseInt(sub, 2);
            if (number >= 10 && number <= 15) {
                switch (number) {
                    case 10:
                        converted.append("a");
                        break;
                    case 11:
                        converted.append("b");
                        break;
                    case 12:
                        converted.append("c");
                        break;
                    case 13:
                        converted.append("d");
                        break;
                    case 14:
                        converted.append("e");
                        break;
                    case 15:
                        converted.append("f");
                        break;
                }
            } else {
                String conv = Integer.toString(number);
                //add it to the SB.
                converted.append(conv);
            }
            //stop if the length is over 10
            if (converted.length() >= 10) {
                break;
            }
        }
        return converted.toString();
    }

    // returns a String with enough leading zeros to fit the interval
    private static String forceMultiple(StringBuilder s, int interval) {
        if (s.length() % interval == 0) {
            return s.toString();
        } else {
            while (!(s.length() % interval == 0)) {
                s.append("0");
            }
            return s.toString();
        }
    }

    // converts to base 2 from base 8
    public static String octalFractionConversion(double num) {
        DecimalFormat leadZero = new DecimalFormat("000");

        String adjustedInt = getDecimalPartWODecimal(num);
        char[] digits = adjustedInt.toCharArray();
        StringBuilder sb = new StringBuilder(".");

        for (char digit : digits) {
            int current = Character.getNumericValue(digit);
            String binary = Integer.toString(current, 2);
            int binValue = Integer.parseInt(binary);
            String conv = leadZero.format(binValue);
            sb.append(conv);
            if (sb.length() >= 15) {
                break;
            }
        }
        return sb.toString();
    }

    // converts to base 2 from base 16
    public static String hexFractionConversion(String num) {
        DecimalFormat leadZero = new DecimalFormat("0000");

        String adjustedInt = getDecimalPartWODecimal(num);
        char[] digits = (adjustedInt).toCharArray();
        StringBuilder sb = new StringBuilder(".");

        for (char digit : digits) {
            int current = Character.getNumericValue(digit);
            String binary = Integer.toString(current, 2);
            int binValue = Integer.parseInt(binary);
            String conv = leadZero.format(binValue);
            sb.append(conv);
            if (sb.length() >= 15) {
                break;
            }
        }
        return sb.toString();
    }

    // returns the post-decimal number represented as a String
    private static String getDecimalPartWODecimal(double num) {
        String total = Double.toString(num);
        int dot = total.indexOf(".");
        //find the part after the decimal
        return total.substring(dot + 1, total.length());
    }

    // overloaded method that accepts a String parameter instead of a double
    private static String getDecimalPartWODecimal(String num) {
        int dot = num.indexOf(".");
        return num.substring(dot + 1, num.length());
    }

    // hexadecimal-specific method for returning the pre-decimal portion of a hex string
    public static String getHexInteger(String hex) {
        int dot = hex.indexOf(".");
        return hex.substring(0, dot);
    }
}
