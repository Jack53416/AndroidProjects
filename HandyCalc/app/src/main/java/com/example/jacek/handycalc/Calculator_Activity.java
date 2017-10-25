package com.example.jacek.handycalc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Calculator_Activity extends AppCompatActivity implements OnClickListener {

    private ArrayList<Button> mNumericButtons = new ArrayList<>();

    private TextView mTextViewResult;
    private TextView mTextViewHistory;
    private Calculator mCalculator = new Calculator();
    private boolean mCleanableResultField = false;

    private static final String EXTRA_ENHANCED_MODE = "com.example.jacek.handycalc.enhancedMode";

    private static final String BUNDLE_HISTORY_KEY = "com.example.jacek.handycalc.historyKey";
    private static final String BUNDLE_LEFT_OP_VAL_KEY = "com.example.jacek.handycalc.resultLeftOp";
    private static final String BUNDLE_CLEANABLE_RESULT_KEY = "com.example.jacek.handycalc.cleanableResultKey";
    private static final String BUNDLE_RESULT_TEXT_KEY = "com.example.jacek.handycalc.resultTextKey";
    private static final String BUNDLE_CURRENT_OP_KEY = "com.example.jacek.handycalc.currentOpKey";

    public static Intent newIntent(Context packageContext, boolean enhancedMode){
        Intent iCalc = new Intent(packageContext, Calculator_Activity.class);
        iCalc.putExtra(EXTRA_ENHANCED_MODE, enhancedMode);
        return iCalc;
    }
    private void appendResultField(String textToAppend)
    {
        if(mCleanableResultField) {
            mTextViewResult.setText(textToAppend);
            mCleanableResultField = false;
        }
        else
            mTextViewResult.append(textToAppend);
    }

    private void clearAll(){
        mTextViewHistory.setText("");
        mCalculator.clearCalculationHistory();
        mCleanableResultField = true;
    }

    private void updateHistoryField()
    {
        String historyString = mCalculator.getCalculationHistory() + mCalculator.getCurrentOperationSymbol();
        mTextViewHistory.setText(historyString);
    }

    private double parseUserInput(){
        String currentResultText = mTextViewResult.getText().toString();
        double result = Double.NaN;
        try{
            result = Double.parseDouble(currentResultText);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, R.string.parse_error , Toast.LENGTH_SHORT).show();
        }
        return result;
    }
    private void computeResult(){
        double userInput = parseUserInput();
        if(!mCleanableResultField || Double.isNaN(mCalculator.getLeftOperand())) {
                if(!Double.isNaN(userInput)) {
                    mCalculator.setRightOperand(userInput);
                    mTextViewResult.setText(mCalculator.calculate());
                }
        }
        else
        {
            updateHistoryField();
        }
        mCleanableResultField = true;
    }

    private void initNumericButtons(){
        mNumericButtons.add((Button) findViewById(R.id.button0));
        mNumericButtons.add((Button) findViewById(R.id.button1));
        mNumericButtons.add((Button) findViewById(R.id.button2));
        mNumericButtons.add((Button) findViewById(R.id.button3));
        mNumericButtons.add((Button) findViewById(R.id.button4));
        mNumericButtons.add((Button) findViewById(R.id.button5));
        mNumericButtons.add((Button) findViewById(R.id.button6));
        mNumericButtons.add((Button) findViewById(R.id.button7));
        mNumericButtons.add((Button) findViewById(R.id.button8));
        mNumericButtons.add((Button) findViewById(R.id.button9));

        mNumericButtons.add((Button) findViewById(R.id.button_clear));
        mNumericButtons.add((Button) findViewById(R.id.button_bksp));
        mNumericButtons.add((Button) findViewById(R.id.button_changeSign));
        mNumericButtons.add((Button) findViewById(R.id.button_dot));

        for(Button button : mNumericButtons){
            button.setOnClickListener(this);
        }
    }

    private void initBasicFunctionality(){
        initNumericButtons();

        mTextViewResult = (TextView) findViewById(R.id.textView_result);
        mTextViewHistory = (TextView) findViewById(R.id.textView_history);
        Button addButton = (Button) findViewById(R.id.button_Add);
        Button subtractButton = (Button) findViewById(R.id.button_subtract);
        Button multiplyButton = (Button) findViewById(R.id.button_multiply);
        Button divideButton = (Button) findViewById(R.id.button_divide);
        Button equalButton = (Button) findViewById(R.id.button_equals);
        Button changeSignButton = (Button) findViewById(R.id.button_changeSign);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeResult();
                mCalculator.setCurrentOperation(Calculator.Operation.ADDITION);
                updateHistoryField();
            }
        });

        subtractButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                computeResult();
                mCalculator.setCurrentOperation(Calculator.Operation.SUBTRACTION);
                updateHistoryField();
            }
        });

        multiplyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                computeResult();
                mCalculator.setCurrentOperation(Calculator.Operation.MULTIPLICATION);
                updateHistoryField();
            }
        });

        divideButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                computeResult();
                mCalculator.setCurrentOperation(Calculator.Operation.DIVISION);
                updateHistoryField();
            }
        });

        equalButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                computeResult();
                clearAll();
            }
        });

        changeSignButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if (!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.SIGN_CHANGE));
            }
        });
    }

    private void initEnhancedFunctionality(){
        Button sinButton = (Button) findViewById(R.id.button_sin);
        Button cosButton = (Button) findViewById(R.id.button_cos);
        Button tanButton = (Button) findViewById(R.id.button_tan);
        Button lnButton = (Button) findViewById(R.id.button_ln);
        Button sqrtButton = (Button) findViewById(R.id.button_sqrt);
        Button powButton = (Button) findViewById(R.id.button_pow);
        Button squareButton = (Button) findViewById(R.id.button_square);
        Button log10Button = (Button) findViewById(R.id.button_log10);

        sinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.SINUS));
            }
        });

        cosButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.COSINUS));
            }
        });

        tanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.TANGENT));
            }
        });

        lnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.LOG_NAT));
            }
        });

        sqrtButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.SQRT));
            }
        });

        squareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.SQUARE));
            }
        });

        log10Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double userInput = parseUserInput();
                if(!Double.isNaN(userInput))
                    mTextViewResult.setText(mCalculator.compute(userInput, Calculator.Operation.LOG_10));
            }
        });

        powButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                computeResult();
                mCalculator.setCurrentOperation(Calculator.Operation.POW);
                updateHistoryField();
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean enhancedMode = getIntent().getBooleanExtra(EXTRA_ENHANCED_MODE, false);


        if(enhancedMode){
            setContentView(R.layout.activity_enhanced_calculator);
            initEnhancedFunctionality();
        }
        else{
            setContentView(R.layout.activity_simple_calculator);
        }
        initBasicFunctionality();

        if(savedInstanceState != null){
            mCalculator.setCalculationHistory(savedInstanceState.getString(BUNDLE_HISTORY_KEY, ""));
            mCalculator.setLeftOperand(savedInstanceState.getDouble(BUNDLE_LEFT_OP_VAL_KEY, Double.NaN));
            mCalculator.setCurrentOperation((Calculator.Operation) savedInstanceState.getSerializable(BUNDLE_CURRENT_OP_KEY));
            mTextViewResult.setText(savedInstanceState.getString(BUNDLE_RESULT_TEXT_KEY, "0"));
            mCleanableResultField = savedInstanceState.getBoolean(BUNDLE_CLEANABLE_RESULT_KEY, true);

            updateHistoryField();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance){
        super.onSaveInstanceState(savedInstance);
        savedInstance.putString(BUNDLE_HISTORY_KEY, mCalculator.getCalculationHistory());
        savedInstance.putBoolean(BUNDLE_CLEANABLE_RESULT_KEY, mCleanableResultField);
        savedInstance.putDouble(BUNDLE_LEFT_OP_VAL_KEY, mCalculator.getLeftOperand());
        savedInstance.putString(BUNDLE_RESULT_TEXT_KEY, mTextViewResult.getText().toString());
        savedInstance.putSerializable(BUNDLE_CURRENT_OP_KEY, mCalculator.getCurrentOperation());
    }

    @Override
    public void onClick(View v) {
        String currentText;

        switch(v.getId()){
            case R.id.button0:
                appendResultField("0");
                break;
            case R.id.button1:
                appendResultField("1");
                break;
            case R.id.button2:
                appendResultField("2");
                break;
            case R.id.button3:
                appendResultField("3");
                break;
            case R.id.button4:
                appendResultField("4");
                break;
            case R.id.button5:
                appendResultField("5");
                break;
            case R.id.button6:
                appendResultField("6");
                break;
            case R.id.button7:
                appendResultField("7");
                break;
            case R.id.button8:
                appendResultField("8");
                break;
            case R.id.button9:
                appendResultField("9");
                break;
            case R.id.button_clear:
                clearAll();
                mTextViewResult.setText("0");
                break;
            case R.id.button_bksp:
                currentText = mTextViewResult.getText().toString();
                if(currentText.length() > 0)
                {
                    currentText = currentText.substring(0, currentText.length() - 1);
                    mTextViewResult.setText(currentText);
                }
                if(currentText.length() == 0)
                {
                    mTextViewResult.setText("0");
                    mCleanableResultField = true;
                }
                break;
            case R.id.button_dot:
                appendResultField(".");
                break;
        }
    }
}
