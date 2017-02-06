package com.example.tim_w.theswitchgame;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private HashSet<Button> enabledBtns;
    private HashSet<Button> disabledBtns;
    private HashSet<Button> origEnabledBtns;
    private Button[] btn_array;
    private HashMap<String, HashSet<Button>> switches;
    private boolean finished = false;
    private String switchStr = "ABCDEFGHIJ";
    private int moveCount = 0;
    private String seqStr = "";
    private String solnSeq = "";
    private boolean hasSoln = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn_array = new Button[16];
        this.switches = new HashMap<String, HashSet<Button>>();
        this.enabledBtns = new HashSet<Button>();
        this.disabledBtns = new HashSet<Button>();
        this.origEnabledBtns = new HashSet<Button>();

        initializeButtons();
        initializeSwitches();
        randomizeButtons(null);
    }

    private HashSet<Button> addValsToSet(Button[] btns){
        HashSet<Button> newSet = new HashSet<Button>();

        for(Button btn : btns){
            newSet.add(btn);
        }
        return newSet;
    }

    private void initializeSwitches(){
        Button btn0 = this.btn_array[0];
        Button btn1 = this.btn_array[1];
        Button btn2 = this.btn_array[2];
        Button btn3 = this.btn_array[3];
        Button btn4 = this.btn_array[4];
        Button btn5 = this.btn_array[5];
        Button btn6 = this.btn_array[6];
        Button btn7 = this.btn_array[7];
        Button btn8 = this.btn_array[8];
        Button btn9 = this.btn_array[9];
        Button btn10 = this.btn_array[10];
        Button btn11 = this.btn_array[11];
        Button btn12 = this.btn_array[12];
        Button btn13 = this.btn_array[13];
        Button btn14 = this.btn_array[14];
        Button btn15 = this.btn_array[15];

        this.switches.put("A", addValsToSet(new Button[] {btn0,btn1,btn2}));
        this.switches.put("B", addValsToSet(new Button[] {btn3,btn7,btn9,btn11}));
        this.switches.put("C", addValsToSet(new Button[] {btn4,btn10,btn14,btn15}));
        this.switches.put("D", addValsToSet(new Button[] {btn0,btn4,btn5,btn6,btn7}));
        this.switches.put("E", addValsToSet(new Button[] {btn6,btn7,btn8,btn10,btn12}));
        this.switches.put("F", addValsToSet(new Button[] {btn0,btn2,btn14,btn15}));
        this.switches.put("G", addValsToSet(new Button[] {btn3,btn14,btn15}));
        this.switches.put("H", addValsToSet(new Button[] {btn4,btn5,btn7,btn14,btn15}));
        this.switches.put("I", addValsToSet(new Button[] {btn1,btn2,btn3,btn4,btn5}));
        this.switches.put("J", addValsToSet(new Button[] {btn3,btn4,btn5,btn9,btn13}));
    }

    private void initializeButtons(){
        for(int i = 0; i < this.btn_array.length; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button currBtn = (Button) findViewById(id);
            this.btn_array[i] = currBtn;
            currBtn.setBackgroundResource(R.drawable.btn_white);
            currBtn.setSelected(false);
            this.disabledBtns.add(currBtn);
        }
    }

    public void randomizeButtons(View v){
        HashSet<Character> toSwitch = new HashSet<Character>();

        //Make sure enabled/disabled sets are empty
        this.enabledBtns.clear();
        this.disabledBtns.clear();
        this.origEnabledBtns.clear();

        //Initialize buttons
        initializeButtons();

        //Reset sequence string
        updateSeqStringView("");

        //Reset solution view
        resetSolnView();

        //Reset move count text view
        updateMoveCountView(0);

        /**
         * List of varying probabilities
         * of possible random switch presses to create new
         * always solvable puzzle
         */
        Random r = new Random();
        int rNumToPress = r.nextInt(100);
        int numToSwitch = 0;
        if(rNumToPress < 2){
            numToSwitch = 1;
        }else if(rNumToPress < 6){
            numToSwitch = 2;
        }else if(rNumToPress < 16){
            numToSwitch = 3;
        }else if(rNumToPress < 25){
            numToSwitch = 4;
        }else if(rNumToPress < 45){
            numToSwitch = 5;
        }else if(rNumToPress < 55){
            numToSwitch = 6;
        }else if(rNumToPress < 70){
            numToSwitch = 7;
        }else if(rNumToPress < 80){
            numToSwitch = 8;
        }else if(rNumToPress < 90){
            numToSwitch = 9;
        }else{
            numToSwitch = 10;
        }

        for(int i = 0; i < numToSwitch; i++){
            int index = r.nextInt(10);
            toSwitch.add(this.switchStr.charAt(index));
        }

        //Reset switch states
        resetSwitches();

        /**
         * Iterate switchess
         * Simulate the switch press to generate board
         * Will ensure always possible board
         */
        for(char currSwitch : toSwitch){
            HashSet<Button> changeBtns = this.switches.get(Character.toString(currSwitch));
            simulateSwitches(changeBtns);
            this.origEnabledBtns = (HashSet<Button>) this.enabledBtns.clone();
        }
    }

    private void simulateSwitches(HashSet<Button> btns){
        for(Button btn : btns){
            changeBtnState(btn);
        }
    }

    public void resetSwitches(){
        for(int i = 0; i < switchStr.length(); i++){
            int id = getResources().getIdentifier("switch" + this.switchStr.charAt(i), "id", getPackageName());
            ToggleButton tBtn = (ToggleButton) findViewById(id);
            tBtn.setChecked(false);
        }
    }

    public void resetSolnView(){
        TextView sView = (TextView) findViewById(R.id.solnField);
        this.solnSeq = "";
        sView.setText("");
    }

    public void resetBoard(View v){
        //Make sure enabled/disabled sets are empty
        this.enabledBtns.clear();
        this.disabledBtns.clear();

        for(int i = 0; i < this.btn_array.length; i ++){
            Button currBtn = this.btn_array[i];
            if(this.origEnabledBtns.contains(currBtn)){
                this.enabledBtns.add(currBtn);
                currBtn.setBackgroundResource(R.drawable.btn_black);
                currBtn.setSelected(true);
            }else{
                this.disabledBtns.add(currBtn);
                currBtn.setBackgroundResource(R.drawable.btn_white);
                currBtn.setSelected(false);
            }
        }
        resetSwitches();
        resetSolnView();
        updateSeqStringView("");
        updateMoveCountView(0);
    }

    public void autoSolve(View v){
        this.hasSoln = false;
        computeCombinations("ABCDEFGHIJ", new StringBuffer(), 0);
        if(!this.hasSoln){
            resetSolnView();
            Toast.makeText(this, "NO SOLUTION", Toast.LENGTH_SHORT).show();
        }
    }

    private void computeCombinations(String allSwitches, StringBuffer currCombo, int index){

        for(int i = index; i < allSwitches.length(); i++){
            currCombo.append(allSwitches.charAt(i));
            checkCombination(currCombo.toString());
            computeCombinations(allSwitches, currCombo, i + 1);
            currCombo.deleteCharAt(currCombo.length() - 1);
        }

    }

    private void checkCombination(String combo){
        HashSet<Button> white = (HashSet<Button>)this.disabledBtns.clone(); //this.disabled
        HashSet<Button> black = (HashSet<Button>)this.enabledBtns.clone(); //this.enabled

        for(int i = 0; i < combo.length(); i++){
            HashSet<Button> btnsToSwitch = this.switches.get(Character.toString(combo.charAt(i)));

            for(Button btn_num : btnsToSwitch){
                if(white.contains(btn_num)){ //if btn not selected, select it
                    white.remove(btn_num);
                    black.add(btn_num);
                }else{ //if btn not disabled, then has to be enabled
                    black.remove(btn_num);
                    white.add(btn_num);
                }
            }
        }

        //When one set is empty, current sequence of switches is success
        if(white.isEmpty() || black.isEmpty()){
            this.solnSeq = combo;
            this.hasSoln = true;
            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
            TextView sView = (TextView) findViewById(R.id.solnField);
            String copySoln = "";

            for(int i = 0; i < combo.length(); i++){
                if(i == 0){
                    copySoln += combo.charAt(i);
                }else{
                    copySoln = copySoln + "," + combo.charAt(i);
                }
            }
            sView.setText("Requires " + combo.length() + " Moves " + "{" + copySoln + "}");
            Log.d(TAG, combo);
            Log.d(TAG, "****************************************************************************************");
        }
    }

    public void changeBtnState(View v){
        if (v.isSelected()) {
            this.disabledBtns.add((Button)v);
            this.enabledBtns.remove(v);
            v.setBackgroundResource(R.drawable.btn_white);
            v.setSelected(false);
        } else {
            this.enabledBtns.add((Button)v);
            this.disabledBtns.remove(v);
            v.setBackgroundResource(R.drawable.btn_black);
            v.setSelected(true);
        }
    }

    public void evaluateSwitch(View v){
        String label = v.getTag().toString();
        HashSet<Button> changeBtns = this.switches.get(label);
        updateSeqStringView(label);

        simulateSwitches(changeBtns);

        if(this.enabledBtns.isEmpty() || this.disabledBtns.isEmpty()){
            Toast.makeText(this, "Puzzle Solved!", Toast.LENGTH_SHORT).show();
        }

        updateMoveCountView(this.moveCount + 1);
    }

    private void updateSeqStringView(String label){
        if(label.equalsIgnoreCase("")){
            this.seqStr = "";
        }else{
            if(this.seqStr.length() == 0){
                this.seqStr += label;
            } else {
                this.seqStr = this.seqStr + "," + label;
            }
        }

        TextView seqText = (TextView) findViewById(R.id.seqText);
        seqText.setText(this.seqStr);
    }

    private void updateMoveCountView(int count){
        TextView mvCount = (TextView) findViewById(R.id.countField);
        mvCount.setText(String.valueOf(count));
        this.moveCount = count;
    }
}
