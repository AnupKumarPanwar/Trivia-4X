package com.quiz.triviazq;

/**
 * Created by Asus on 2/13/2018.
 */

public class Question {
    String q, op1, op2, op3;
    int ans, ch1, ch2,ch3;
    Question(String q, String op1, String op2, String op3, int ans, int ch1, int ch2, int ch3)
    {
        this.q=q;
        this.op1=op1;
        this.op2=op2;
        this.op3=op3;
        this.ans=ans;
        this.ch1=ch1;
        this.ch2=ch2;
        this.ch3=ch3;
    }
}
