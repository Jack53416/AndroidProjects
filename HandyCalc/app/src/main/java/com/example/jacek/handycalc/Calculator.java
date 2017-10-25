package com.example.jacek.handycalc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


class Calculator {
    private double mLeftOperand = Double.NaN;
    private double mRightOperand = 0.0;
    private Operation mCurrentOperation = Operation.NONE;
    private StringBuilder mCalculationHistory = new StringBuilder();
    private DecimalFormat mDecimalFormat;

    enum Operation {
        NONE(""),
        ADDITION(" + "),
        SUBTRACTION(" - "),
        DIVISION(" / "),
        MULTIPLICATION(" * "),
        SIGN_CHANGE("neg"),
        SINUS("sin"),
        COSINUS("cos"),
        TANGENT("tan"),
        LOG_NAT("ln"),
        SQRT("sqrt"),
        SQUARE(" ^ 2"),
        POW(" ^ "),
        LOG_10("log");

        private String mOperationSign;

        Operation(String opSign) {
            this.mOperationSign = opSign;
        }

        public String getOperationSign(){
            return this.mOperationSign;
        }
    }

    Calculator(){
        Locale currentLocale = Locale.getDefault();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        mDecimalFormat = new DecimalFormat("#.#####", otherSymbols);
    }

    void setRightOperand(double rightOperand) {
        this.mRightOperand = rightOperand;
    }

    void setCurrentOperation(Operation currentOperation) {
        this.mCurrentOperation = currentOperation;
    }

    void setLeftOperand(double leftOperand){ this.mLeftOperand = leftOperand; }

    double getLeftOperand() {
        return mLeftOperand;
    }


    String getCurrentOperationSymbol() { return mCurrentOperation.getOperationSign(); }

    Operation getCurrentOperation(){ return this.mCurrentOperation; }

    String getCalculationHistory(){ return mCalculationHistory.toString(); }

    void setCalculationHistory(String history){
        clearCalculationHistory();
        mCalculationHistory.append(history);
    }

    void clearCalculationHistory(){
        mCalculationHistory.setLength(0);
        mCurrentOperation = Operation.NONE;
        mLeftOperand = Double.NaN;
    }


    String calculate(){
        double result = 0.0;

        switch (mCurrentOperation){
            case ADDITION:
                mCalculationHistory.append(mCurrentOperation.getOperationSign()).append(mDecimalFormat.format(mRightOperand));
                result = mLeftOperand + mRightOperand;
                break;
            case SUBTRACTION:
                mCalculationHistory.append(mCurrentOperation.getOperationSign()).append(mDecimalFormat.format(mRightOperand));
                result = mLeftOperand - mRightOperand;
                break;
            case DIVISION:
                mCalculationHistory.append(mCurrentOperation.getOperationSign()).append(mDecimalFormat.format(mRightOperand));
                if(mRightOperand != 0)
                    result = mLeftOperand / mRightOperand;
                break;
            case MULTIPLICATION:
                mCalculationHistory.append(mCurrentOperation.getOperationSign()).append(mDecimalFormat.format(mRightOperand));
                result = mLeftOperand * mRightOperand;
                break;
            case POW:
                mCalculationHistory.insert(0,'(');
                mCalculationHistory.append(')');
                mCalculationHistory.append(mCurrentOperation.getOperationSign()).append(mDecimalFormat.format(mRightOperand));
                result = Math.pow(mLeftOperand, mRightOperand);
                break;
            default:
                mCalculationHistory.append(mDecimalFormat.format(mRightOperand));
                result = mRightOperand;
                break;
        }
        mLeftOperand = result;
        return mDecimalFormat.format(result);
    }

    String compute(double number, Operation operation)
    {
        double result;
        switch (operation){
            case SINUS:
                result = Math.cos(Math.toRadians(number));
                break;
            case COSINUS:
                result = Math.cos(Math.toRadians(number));
                break;
            case TANGENT:
                result = Math.tan(Math.toRadians(number));
                break;
            case LOG_NAT:
                result = Math.log(number);
                break;
            case SQRT:
                result = Math.sqrt(number);
                break;
            case SQUARE:
                result = number * number;
                break;
            case LOG_10:
                result = Math.log10(number);
                break;
            case SIGN_CHANGE:
                result = number * (-1);
                break;
            default:
                result = number;
        }
        return mDecimalFormat.format(result);
    }

}
