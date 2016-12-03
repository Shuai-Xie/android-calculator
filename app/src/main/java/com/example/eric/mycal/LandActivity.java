package com.example.eric.mycal;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandActivity extends AppCompatActivity {

    //choose precision, extends NumberPicker
    @BindView(R.id.precisionPicker)
    MyNumberPicker precisionPicker;

    //tvNow,tvPast to show math expression
    //tvRad,tvDeg to switch between Deg and Rad
    @BindView(R.id.tv_past)
    TextView tvPast;
    @BindView(R.id.tv_now)
    AutoScaleTextView tvNow; //extends textView to auto scale the width and decrease the font size
    @BindView(R.id.tv_deg)
    TextView tvDeg;
    @BindView(R.id.tv_rad)
    TextView tvRad;

    //to save, copy and clear past math expressions
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_copy)
    Button btnCopy;
    @BindView(R.id.btn_clear)
    Button btnClear;

    //common number
    @BindView(R.id.btn_7)
    Button btn7;
    @BindView(R.id.btn_8)
    Button btn8;
    @BindView(R.id.btn_9)
    Button btn9;
    @BindView(R.id.btn_4)
    Button btn4;
    @BindView(R.id.btn_5)
    Button btn5;
    @BindView(R.id.btn_6)
    Button btn6;
    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;
    @BindView(R.id.btn_0)
    Button btn0;
    @BindView(R.id.btn_dot)
    Button btnDot;

    //basic opers: + - × / ( ) =
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_sub)
    Button btnSub;
    @BindView(R.id.btn_mul)
    Button btnMul;
    @BindView(R.id.btn_div)
    Button btnDiv;
    @BindView(R.id.btn_bracket)
    Button btnBracket;
    @BindView(R.id.btn_equal)
    Button btnEqual;

    //scienceCalculator calculation
    // sin  cos  tan
    // √x    e    π
    // 1/x   ln  log
    // x^2  e^x  x^y
    @BindView(R.id.btn_sin)
    Button btnSin;
    @BindView(R.id.btn_cos)
    Button btnCos;
    @BindView(R.id.btn_tan)
    Button btnTan;
    @BindView(R.id.btn_sqrt)
    Button btnSqrt;
    @BindView(R.id.btn_e)
    Button btnE;
    @BindView(R.id.btn_pi)
    Button btnPi;
    @BindView(R.id.btn_1x)
    Button btn1x;
    @BindView(R.id.btn_ln)
    Button btnLn;
    @BindView(R.id.btn_log)
    Button btnLog;
    @BindView(R.id.btn_x2)
    Button btnX2;
    @BindView(R.id.btn_ex)
    Button btnEx;
    @BindView(R.id.btn_xy)
    Button btnXy;

    //basic calculator function: delete a char,clear the current math
    @BindView(R.id.btn_del)
    Button btnDel;
    @BindView(R.id.btn_clc)
    Button btnClc;

    private String mathPast = "";
    private String mathNow = "";
    private int precision = 6;
    private int equal_flag = 0;

    //加入角度运算
    private final int DEG = 0;
    private final int RAD = 1;
    private int angle_metric = DEG; //默认的度量为角度DEG

    //一个科学计算器
    private ScienceCalculator scienceCalculator = new ScienceCalculator();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(LandActivity.this, MainActivity.class);
            intent.putExtra("land", tvPast.getText().toString());
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_land);
        ButterKnife.bind(this);

        //设置控件属性
        initTvPast();
        initNumBtns();
        initBaseOpers();
        initScienceOpers();
        initDegRad();
        initPrecisionPicker();
        initThreeFunctions();
        initBaseCalculatorFunction();
    }

    //初始化tvPast
    public void initTvPast() {

        //设置tvPast一些属性
        tvPast.setMovementMethod(ScrollingMovementMethod.getInstance()); //内容自动滚动到最新的一行
        tvPast.setTextIsSelectable(true); //长按复制

        //获取界面切换的tvPast的内容
        Intent intent = this.getIntent();
        String tvPastContent = intent.getStringExtra("main");

        //如果当前的界面是启动界面，不是从MainActivity切换来的，上面的mathPast就为null了，要处理这种异常
        if (tvPastContent == null) {
            tvPast.setText("");
        } else {
            String[] maths = tvPastContent.split("\n");
            int i;
            for (i = 0; i < maths.length - 1; i++) {
                tvPast.append(maths[i] + "\n");
            }
            tvPast.append(maths[i]); //最后一个math不用加换行
        }

    }

    //初始化数字btns
    public void initNumBtns() {

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果flag=1，表示要输入新的运算式，清空mathNow并设置flag=0
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }

                if (mathNow.length() == 0) {                    //1.mathNow为空，+0
                    mathNow += "0";
                } else if (mathNow.length() == 1) {             //2.mathNow 长度为1

                    if (mathNow.charAt(0) == '0') {                 //2.1 如果该字符为0，不加
                        mathNow += "";
                    } else if (isNum(mathNow.charAt(0))) {          //2.2 如果该字符为1-9，+0
                        mathNow += "0";
                    }

                } else if (!isNum(mathNow.charAt(mathNow.length() - 2)) && mathNow.charAt(mathNow.length() - 1) == '0') {
                    mathNow += "";                              //3.属于2.1的一般情况，在math中间出现 比如：×0 +0
                } else {                                        //4.除此之外，+0
                    mathNow += "0";
                }
                tvNow.setText(mathNow);
            }
        });

        //btn 1-9 输入条件相同
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }

                if (mathNow.length() == 0) {
                    mathNow += "1";
                } else {

                    //math的最后一个字符是：1-9, oper, (, .
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "1";
                }
                tvNow.setText(mathNow);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "2";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "2";
                }
                tvNow.setText(mathNow);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "3";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "3";
                }
                tvNow.setText(mathNow);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "4";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "4";
                }
                tvNow.setText(mathNow);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "5";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "5";
                }
                tvNow.setText(mathNow);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "6";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "6";
                }
                tvNow.setText(mathNow);
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "7";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "7";
                }
                tvNow.setText(mathNow);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "8";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "8";
                }
                tvNow.setText(mathNow);
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "9";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || isOper(ch) || ch == '(' || ch == '.')
                        mathNow += "9";
                }
                tvNow.setText(mathNow);
            }
        });

        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {                                //1.mathNow为空，+0.
                    mathNow += "0.";
                } else if (isOper(mathNow.charAt(mathNow.length() - 1))) {  //2.mathNow的最后一个字符为oper，+0.
                    mathNow += "0.";
                } else if (isNum(mathNow.charAt(mathNow.length() - 1))) {   //3.mathNow的最后一个字符为num，+.
                    mathNow += ".";
                } else {                                                    //4.除此之外，不加
                    mathNow += "";
                }
                tvNow.setText(mathNow);
            }
        });
    }

    //初始化基本的运算符
    public void initBaseOpers() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mathNow.length() == 0) {
                    mathNow += "+";
                } else {
                    if (isNum(mathNow.charAt(mathNow.length() - 1))
                            || mathNow.charAt(mathNow.length() - 1) == ')'
                            || mathNow.charAt(mathNow.length() - 1) == '('
                            || mathNow.charAt(mathNow.length() - 1) == 'π'
                            || mathNow.charAt(mathNow.length() - 1) == 'e')
                        mathNow += "+";
                }
                tvNow.setText(mathNow);
                equal_flag = 0; //可能用运算结果直接运算，flag直接设0
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mathNow.length() == 0) {
                    mathNow += "-";
                } else {
                    if (isNum(mathNow.charAt(mathNow.length() - 1))
                            || mathNow.charAt(mathNow.length() - 1) == ')'
                            || mathNow.charAt(mathNow.length() - 1) == '('
                            || mathNow.charAt(mathNow.length() - 1) == 'π'
                            || mathNow.charAt(mathNow.length() - 1) == 'e')
                        mathNow += "-";
                }
                tvNow.setText(mathNow);
                equal_flag = 0;
            }
        });

        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mathNow.length() != 0) {
                    if (isNum(mathNow.charAt(mathNow.length() - 1))
                            || mathNow.charAt(mathNow.length() - 1) == ')'
                            || mathNow.charAt(mathNow.length() - 1) == 'π'
                            || mathNow.charAt(mathNow.length() - 1) == 'e')
                        mathNow += "×";
                }
                tvNow.setText(mathNow);
                equal_flag = 0;
            }
        });

        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mathNow.length() != 0) {
                    if (isNum(mathNow.charAt(mathNow.length() - 1))
                            || mathNow.charAt(mathNow.length() - 1) == ')'
                            || mathNow.charAt(mathNow.length() - 1) == 'π'
                            || mathNow.charAt(mathNow.length() - 1) == 'e')
                        mathNow += "/";
                }
                tvNow.setText(mathNow);
                equal_flag = 0;
            }
        });

        btnBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {                                //1.mathNow为空，+(
                    mathNow += "(";
                } else if (isOper(mathNow.charAt(mathNow.length() - 1))) {  //2.mathNow最后一个字符是oper，+(
                    mathNow += "(";
                } else if (isNum(mathNow.charAt(mathNow.length() - 1))      //3.mathNow最后一个字符是num, π, e
                        || mathNow.charAt(mathNow.length() - 1) == 'π'
                        || mathNow.charAt(mathNow.length() - 1) == 'e') {
                    if (!hasLeftBracket(mathNow))                               //3.1 没有(, 加 ×(
                        mathNow += "×(";
                    else                                                        //3.2 已有(, 加 )
                        mathNow += ")";
                } else if (mathNow.charAt(mathNow.length() - 1) == ')') {   //4.mathNow最后一个字符是)，说明用户是在补全右括号，+)
                    mathNow += ')';
                }
                tvNow.setText(mathNow);
            }
        });


        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //右括号自动补全
                int leftNum = 0;
                int rightNum = 0;
                for (int i = 0; i < mathNow.length(); i++) {
                    if (mathNow.charAt(i) == '(')
                        leftNum++;
                    if (mathNow.charAt(i) == ')')
                        rightNum++;
                }
                int missingNum = leftNum - rightNum; //缺失的 ) 数量
                while (missingNum > 0) {
                    mathNow += ')';
                    missingNum--;
                }
                tvNow.setText(mathNow);

                mathPast = "\n" + mathNow; //使得呈现的mathPast自动换行

                double result = scienceCalculator.cal(mathNow, precision, angle_metric); //调用科学计算器
                if (result == Double.MAX_VALUE)
                    mathNow = "Math Error";
                else {
                    mathNow = String.valueOf(result);
                    System.out.println(mathNow);
                    if (mathNow.charAt(mathNow.length() - 2) == '.' && mathNow.charAt(mathNow.length() - 1) == '0') {
                        mathNow = mathNow.substring(0, mathNow.length() - 2);
                    }
                }

                mathPast = mathPast + "=" + mathNow;

                //用tvPast.set(mathPast)不能实现自动滚动到最新运算过程
                tvPast.append(mathPast); //添加新的运算过程

                //tvPast滚动到最新的运算过程
                int offset = tvPast.getLineCount() * tvPast.getLineHeight();
                if (offset > tvPast.getHeight()) {
                    tvPast.scrollTo(0, offset - tvPast.getHeight());
                }
                tvNow.setText(mathNow);

                equal_flag = 1; //设置flag=1
            }
        });
    }

    //初始化科学运算符
    public void initScienceOpers() {
        btnSin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "sin(";
                } else {
                    //oper, (, 加 sin(
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "sin(";
                }
                tvNow.setText(mathNow);
            }
        });

        btnCos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "cos(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "cos(";
                }
                tvNow.setText(mathNow);
            }
        });

        btnTan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "tan(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "tan(";
                }
                tvNow.setText(mathNow);
            }
        });

        btnSqrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "√(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "√(";
                }
                tvNow.setText(mathNow);
            }
        });


        btnPi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "π";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "π";
                }
                tvNow.setText(mathNow);
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "e";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "e";
                }
                tvNow.setText(mathNow);
            }
        });

        btnLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "ln(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "ln(";
                }
                tvNow.setText(mathNow);
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "log(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "log(";
                }
                tvNow.setText(mathNow);
            }
        });

        btn1x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "1/";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "1/";
                }
                tvNow.setText(mathNow);
            }
        });

        btnEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equal_flag == 1) {
                    mathNow = "";
                    equal_flag = 0;
                }
                if (mathNow.length() == 0) {
                    mathNow += "e^(";
                } else {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isOper(ch) || ch == '(')
                        mathNow += "e^(";
                }
                tvNow.setText(mathNow);
            }
        });

        btnX2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //要求mathNow不为空并且最后一个字符：num, ), e, π
                if (mathNow.length() > 0) {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || ch == ')' || ch == 'e' || ch == 'π')
                        mathNow += "^2";
                }
                tvNow.setText(mathNow);
            }
        });

        btnXy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //条件同btnX2
                if (mathNow.length() > 0) {
                    char ch = mathNow.charAt(mathNow.length() - 1);
                    if (isNum(ch) || ch == ')' || ch == 'e' || ch == 'π')
                        mathNow += "^(";
                }
                tvNow.setText(mathNow);
            }
        });

    }

    //初始化tvDeg,tvRad
    public void initDegRad() {
        tvDeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDeg.setTextColor(Color.parseColor("#3FA2F0"));
                tvRad.setTextColor(Color.parseColor("#AAAAAA"));
                angle_metric = DEG;
            }
        });

        tvRad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvRad.setTextColor(Color.parseColor("#3FA2F0"));
                tvDeg.setTextColor(Color.parseColor("#AAAAAA"));
                angle_metric = RAD;
            }
        });
    }

    //初始化精度选择器
    public void initPrecisionPicker() {
        precisionPicker.setMaxValue(12); //最多保留12位
        precisionPicker.setMinValue(0);
        precisionPicker.setValue(6);
        precisionPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                precision = newVal;
            }
        });
    }

    //保存，复制，清空
    public void initThreeFunctions() {
        //保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //保存文件到sd卡 manifest文件中也要添加2个permission
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/math.txt"; //设置保存路径和文件名
                    try {
                        FileOutputStream outputStream = new FileOutputStream(path);
                        outputStream.write(tvPast.getText().toString().getBytes()); //写字节
                        outputStream.close(); //关闭输出流
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LandActivity.this, "保存到" + path, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //复制
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); //采用ClipboardManager类
                cm.setText(tvPast.getText());
                Toast.makeText(LandActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });

        //清空
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mathPast = "";
                tvPast.setText(mathPast);
                Toast.makeText(LandActivity.this, "计算过程已经清空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化计算器器基本Button
    public void initBaseCalculatorFunction() {
        btnClc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mathNow = "";
                tvNow.setText(mathNow);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mathNow.length() != 0) {
                    mathNow = mathNow.substring(0, mathNow.length() - 1);
                    tvNow.setText(mathNow);
                }
            }
        });
    }

    //判断当前字符是否为数字
    private boolean isNum(char c) {
        char num[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int i = 0;
        for (; i < num.length; i++) {
            if (num[i] == c)
                break;
        }
        return i != num.length;
    }

    //判断当前字符是否为运算符
    private boolean isOper(char c) {
        char oper[] = {'+', '-', '×', '/'};
        int i = 0;
        for (; i < oper.length; i++) {
            if (oper[i] == c)
                break;
        }
        return i != oper.length;
    }

    //判断当前math是否有')'
    private boolean hasLeftBracket(String s) {
        int i = 0;
        for (; i < s.length(); i++) {
            if (s.charAt(i) == '(')
                break;
        }
        return i != s.length();
    }
}
