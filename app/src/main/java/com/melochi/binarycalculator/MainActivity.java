package com.melochi.binarycalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    //private Map<String, Integer> numberSystems = new HashMap<String, Integer>();

    private Spinner choicesSpinner;
    private EditText inputEditText;
    private Button convertBtn;
    private String itemSelected = "None";
    private int itemPosition = 0;
    private double numberToConvert;
    private String hexToConvert;

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("choicesSpinner", choicesSpinner.getSelectedItemPosition());
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            choicesSpinner.setSelection(savedInstanceState.getInt("choicesSpinner", 0));
//        }
        setContentView(R.layout.activity_main);

        addItemsToSpinner(); //adds list of number systems to spinner
        addListenerToSpinner(); //adds onItemSelectedListener to spinner

        inputEditText = (EditText) findViewById(R.id.inputEditText);
        inputEditText.setVisibility(View.INVISIBLE);
        convertBtn = (Button) findViewById(R.id.convertButton);
        convertBtn.setEnabled(false);

        //storing the user's input as the numberToConvert
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEmpty(inputEditText)) {
                    String str = inputEditText.getText().toString();
                    convertBtn.setEnabled(true);
                    if (itemSelected.equals("Hexadecimal (Base 16)")) {
                        hexToConvert = str;
                        convertBtn.setEnabled(true);
                    } else if (!itemSelected.equals("Hexadecimal (Base 16)")) {
                        if (!str.matches("[0-9.]+")) {
                            return;
                        }
                        //the user input is valid --> check if the user began the number with .__ instead of 0.___ to avoid NumberFormatException
                        numberToConvert = str.startsWith(".") ? Double.parseDouble("0" + str) : Double.parseDouble(str);
                    }
                } else {
                    convertBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        convertBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (itemSelected.equals("Binary (Base 2)") && !isBinaryNumber(inputEditText.getText().toString())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Invalid Binary Number");
                builder.setMessage("Your input is an invalid binary value. "
                        + "Remember that binary numbers can only contain digits from 0-1.");
                builder.setPositiveButton("Okay", null);
                AlertDialog alert = builder.create();
                alert.show();
            } else if (itemSelected.equals("Octal (Base 8)") && !isOctalNumber(inputEditText.getText().toString())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Invalid Octal Number");
                builder.setMessage("Your input is an invalid octal value. "
                        + "Remember that octal numbers can only contain digits from 0-7.");
                builder.setPositiveButton("Okay", null);
                AlertDialog alert = builder.create();
                alert.show();
            } else if (itemSelected.equals("Hexadecimal (Base 16)")) {
                if (!isHexNumber(hexToConvert)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Invalid Hexadecimal Number");
                    builder.setMessage("Your input is an invalid hexadecimal value. "
                            + "Remember that hexadecimal numbers can only contain digits from 0-9 and letters from A-F.");
                    builder.setPositiveButton("Okay", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Intent startIntent = new Intent(getApplicationContext(), ConversionActivity.class);
                    startIntent.putExtra("com.melochi.binarycalculator.BASE", itemSelected);
                    startIntent.putExtra("com.melochi.binarycalculator.HEX", hexToConvert);
                    startIntent.putExtra("com.melochi.binarycalculator.POSITION", itemPosition);
                    startActivity(startIntent);
                }
            } else {
                Intent startIntent = new Intent(getApplicationContext(), ConversionActivity.class);
                startIntent.putExtra("com.melochi.binarycalculator.BASE", itemSelected);
                startIntent.putExtra("com.melochi.binarycalculator.NUMBER", numberToConvert);
                startIntent.putExtra("com.melochi.binarycalculator.POSITION", itemPosition);
                startActivity(startIntent);
            }
            }
        });
    }

    public void addItemsToSpinner() {
        choicesSpinner = (Spinner) findViewById(R.id.choicesSpinner);

        ArrayAdapter<CharSequence> choicesSpinnerAdapter =
                ArrayAdapter.createFromResource(this,
                R.array.number_systems, android.R.layout.simple_spinner_item);

        choicesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choicesSpinner.setAdapter(choicesSpinnerAdapter);
    }

    public void addListenerToSpinner() {
        choicesSpinner = (Spinner) findViewById(R.id.choicesSpinner);
        choicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inputEditText.setVisibility(View.VISIBLE);
                itemSelected = parent.getItemAtPosition(position).toString();
                itemPosition = choicesSpinner.getSelectedItemPosition();

                if (itemSelected.equals("Hexadecimal (Base 16)")) {
                    inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /* SUPPORTING METHODS for validation*/

    private boolean isEmpty(EditText eText) {
        return eText.getText().toString().trim().length() == 0;
    }

    private boolean isHexNumber(String num) {
        return num.matches("[0-9A-Fa-f.]+");
    }

    private boolean isBinaryNumber(String num) {
        return num.matches("[01.]+");
    }

    private boolean isOctalNumber(String num) {
        return num.matches("[0-7.]+");
    }
}
