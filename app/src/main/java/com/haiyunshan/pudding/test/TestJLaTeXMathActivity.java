package com.haiyunshan.pudding.test;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.mathjax.MathJaxView;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.utils.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class TestJLaTeXMathActivity extends AppCompatActivity {

    MathJaxView mFormulaView;
    TextView mSizeView;
    TextView mPathView;
    View mNextBtn;

    File[] mArray;

    String[] mLatex;
    int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_jlatexmath);

        this.mFormulaView = this.findViewById(R.id.mjv_formula);
        this.mSizeView = findViewById(R.id.tv_size);
        this.mPathView = findViewById(R.id.tv_path);
        this.mNextBtn = findViewById(R.id.btn_next);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object[] array = mLatex;
                if (array == null || array.length <= 1) {
                    return;
                }

                int pos = mPosition;
                ++pos;
                pos = pos % array.length;
                setFormula(pos);

            }
        });

        File folder = new File(Environment.getExternalStorageDirectory(), "测试");
        folder = new File(folder, "jlatexmath");

        folder = new File(folder, "maximsblog");
//        folder = new File(folder, "scilab");
//        folder = new File(folder, "xzz");



        this.mArray = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }

                String name = pathname.getName();
                if (name.startsWith("._")) {
                    return false;
                }

                String suffix = Utils.getExtension(pathname).toLowerCase();
                return suffix.equalsIgnoreCase("tex");
            }
        });
        mArray = (mArray == null)? new File[0]: mArray;

        this.mLatex = MaximsblogFormula.getFormulaArray();
        mLatex = XzzFormula.getFormulaArray();
//        mLatex = MathFormula.getFormulaArray();
        mLatex = this.getLatexFormula();

        this.mPosition = -1;
        this.setFormula(0);
    }

    void setFormula(int pos) {
        if (mPosition == pos) {
            return;
        }

        this.mPosition = pos;
        mPathView.setText(String.valueOf(pos));

        String str = mLatex[pos];
        mFormulaView.setText(str);
    }

    String[] getLatexFormula() {
        ArrayList<String> list = new ArrayList<>();

        if (false) {
            String[] array = DeFormula.getFormulaArray();
            for (String str : array) {
                list.add(str);
            }
        }

        if (true) {
            String[] array = XzzFormula.getFormulaArray();
            for (String str : array) {
                list.add(str);
            }
        }

        if (false) {
            String[] array = MaximsblogFormula.getFormulaArray();
            for (String str : array) {
                list.add("$" + str + "$");
            }
        }

        if (false) {
            String[] array = MathFormula.getFormulaArray();
            for (String str : array) {
                list.add("$" + str + "$");
            }
        }

        return list.toArray(new String[list.size()]);
    }


}

class DeFormula {
    public static String[] getFormulaArray(){
        return new String[]{
                "\\(\\int_{-\\infty}^{\\infty} e^{-x^2}\\, dx = \\sqrt{\\pi}\\)",
                "\\(\\sum_{n=1}^{\\infty}\\frac1{n^2}=\\frac{\\pi^2}6\\)",
                "\\(\\int_a^b f(x)\\, dx = F(b) - F(a)\\ \\text{where}\\ F\'(x)=f(x)\\)",
                "\\(\\left[\\begin{array}{cc}\\cos\\theta &amp; \\sin\\theta \\\\ \\sin\\theta &amp; -\\cos\\theta \\end{array}\\right]\\)",
                "\\(\\iint_D (\\vec\\nabla\\times\\vec F)\\cdot\\vec n\\ dS = \\oint_{\\partial D} \\vec F\\cdot d\\vec r\\)",
                "\\(x=\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}\\)",
                "\\(f(z_0)= \\frac1{2\\pi i}\\oint_\\gamma \\frac{f(z)}{z-z_0} dz\\)",
                "some text \\((\\infty)\\) some text",
        };
    }
}

class MaximsblogFormula {

    private static String mExample1 = "\\begin{array}{lr}\\mbox{\\textcolor{Blue}{Russian}}&\\mbox{\\textcolor{Melon}{Greek}}\\\\"
            + "\\mbox{"
            + "привет мир".toUpperCase()
            + "}&\\mbox{"
            + "γειά κόσμο".toUpperCase()
            + "}\\\\"
            + "\\mbox{привет мир}&\\mbox{γειά κόσμο}\\\\"
            + "\\mathbf{\\mbox{привет мир}}&\\mathbf{\\mbox{γειά κόσμο}}\\\\"
            + "\\mathit{\\mbox{привет мир}}&\\mathit{\\mbox{γειά κόσμο}}\\\\"
            + "\\mathsf{\\mbox{привет мир}}&\\mathsf{\\mbox{γειά κόσμο}}\\\\"
            + "\\mathtt{\\mbox{привет мир}}&\\mathtt{\\mbox{γειά κόσμο}}\\\\"
            + "\\mathbf{\\mathit{\\mbox{привет мир}}}&\\mathbf{\\mathit{\\mbox{γειά κόσμο}}}\\\\"
            + "\\mathbf{\\mathsf{\\mbox{привет мир}}}&\\mathbf{\\mathsf{\\mbox{γειά κόσμο}}}\\\\"
            + "\\mathsf{\\mathit{\\mbox{привет мир}}}&\\mathsf{\\mathit{\\mbox{γειά κόσμο}}}\\\\"
            + "&\\\\"
            + "\\mbox{\\textcolor{Salmon}{Bulgarian}}&\\mbox{\\textcolor{Tan}{Serbian}}\\\\"
            + "\\mbox{здравей свят}&\\mbox{Хелло уорлд}\\\\"
            + "&\\\\"
            + "\\mbox{\\textcolor{Turquoise}{Bielorussian}}&\\mbox{\\textcolor{LimeGreen}{Ukrainian}}\\\\"
            + "\\mbox{прывітаньне Свет}&\\mbox{привіт світ}\\\\"
            + "\\mbox{Исходный пересекаются}&\\mbox{проверка тся и сх}\\\\"
            + "\\end{array}";

    private static String mExample2 = "\\begin{array}{l}"
            + "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\"
            + "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\"
            + "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\"
            + "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\"
            + "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\"
            + "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\"
            + "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\"
            + "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\"
            + "\\end{array}";

    private static String mExample3 = "\\definecolor{gris}{gray}{0.9}"
            + "\\definecolor{noir}{rgb}{0,0,0}"
            + "\\definecolor{bleu}{rgb}{0,0,1}\\newcommand{\\pa}{\\left|}"
            + "\\begin{array}{c}"
            + "\\JLaTeXMath\\\\"
            + "\\begin{split}"
            + " &Тепловой\\ поток\\ \\mathrm{Тепловой\\ поток}\\ \\mathtt{Тепловой\\ поток}\\\\"
            + " &\\boldsymbol{\\mathrm{Тепловой\\ поток}}\\ \\mathsf{Тепловой\\ поток}\\\\"
            + "|I_2| &= \\pa\\int_0^T\\psi(t)\\left\\{ u(a,t)-\\int_{\\gamma(t)}^a \\frac{d\\theta}{k} (\\theta,t) \\int_a^\\theta c(\\xi) u_t (\\xi,t)\\,d\\xi\\right\\}dt\\right|\\\\"
            + "&\\le C_6 \\Bigg|\\pa f \\int_\\Omega \\pa\\widetilde{S}^{-1,0}_{a,-} W_2(\\Omega, \\Gamma_1)\\right|\\ \\right|\\left| |u|\\overset{\\circ}{\\to} W_2^{\\widetilde{A}}(\\Omega;\\Gamma_r,T)\\right|\\Bigg|\\\\"
            + "&\\\\"
            + "&\\textcolor{magenta}{\\mathrm{Produit\\ avec\\ Java\\ et\\ \\LaTeX\\ par\\ }\\mathscr{C}\\mathcal{A}\\mathfrak{L}\\mathbf{I}\\mathtt{X}\\mathbb{T}\\mathsf{E}}\\\\"
            + "&\\begin{pmatrix}\\alpha&\\beta&\\gamma&\\delta\\\\\\aleph&\\beth&\\gimel&\\daleth\\\\\\mathfrak{A}&\\mathfrak{B}&\\mathfrak{C}&\\mathfrak{D}\\\\\\boldsymbol{\\mathfrak{a}}&\\boldsymbol{\\mathfrak{b}}&\\boldsymbol{\\mathfrak{c}}&\\boldsymbol{\\mathfrak{d}}\\end{pmatrix}\\quad{(a+b)}^{\\frac{n}{2}}=\\sqrt{\\sum_{k=0}^n\\tbinom{n}{k}a^kb^{n-k}}\\quad \\Biggl(\\biggl(\\Bigl(\\bigl(()\\bigr)\\Bigr)\\biggr)\\Biggr)\\\\"
            + "&\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\"
            + "&\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\"
            + "&\\Delta f(x,y)=\\frac{\\partial^2f}{\\partial x^2}+\\frac{\\partial^2f}{\\partial y^2}\\qquad\\qquad \\fcolorbox{noir}{gris}{n!\\underset{n\\rightarrow+\\infty}{\\sim} {\\left(\\frac{n}{e}\\right)}^n\\sqrt{2\\pi n}}\\\\"
            + "&\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\xrightarrow[T]{n\\pm i-j}\\sideset{^t}{}A\\xleftarrow{\\overrightarrow{u}\\wedge\\overrightarrow{v}}\\underleftrightarrow{\\iint_{\\mathds{R}^2}e^{-\\left(x^2+y^2\\right)}\\,\\mathrm{d}x\\mathrm{d}y}"
            + "\\end{split}\\\\"
            + "\\rotatebox{30}{\\sum_{n=1}^{+\\infty}}\\quad\\mbox{Mirror rorriM}\\reflectbox{\\mbox{Mirror rorriM}}"
            + "\\end{array}";

    private static String mExample4 = "\\lim_{x \\to \\infty} \\left(1 + \\frac{1}{n} \\right)^n = e ";

    private static String mExample5 = "\\begin{array}{|c|l|||r|c|}"
            + "\\hline"
            + "\\text{Matrix}&\\multicolumn{2}{|c|}{\\text{Multicolumns}}&\\text{Font sizes commands}\\cr"
            + "\\hline"
            + "\\begin{pmatrix}\\alpha_{11}&\\cdots&\\alpha_{1n}\\cr\\hdotsfor{3}\\cr\\alpha_{n1}&\\cdots&\\alpha_{nn}\\end{pmatrix}&\\Large \\text{Large Right}&\\small \\text{small Left}&\\tiny \\text{tiny Tiny}\\cr"
            + "\\hline"
            + "\\multicolumn{4}{|c|}{\\Huge \\text{Huge Multicolumns}}\\cr"
            + "\\hline"
            + "\\end{array}";

    private static String mExample6 = "\\begin{array}{cc}"
            + "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr"
            + "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr"
            + "\\end{array}";

    private static String mExample7 = "\\mbox{abc abc abc abc abc abc abc abc abc abc abc abc abc abc\\\\abc abc abc abc abc abc abc\\\\abc abc abc abc abc abc abc}\\\\1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1";

    private static String[] mFormulaArray = new String[] {
            mExample4,
            mExample7,
            mExample1,
            mExample2,
            mExample3,

            mExample5,
            mExample6,
            };

    public static String[] getFormulaArray(){
        return mFormulaArray;
    }
}

class XzzFormula {
    private static String mExample3 = "${\\frac{电梯上升10米里做的功\\phantom{电梯上升米里做的功}}{电梯上升10米花的时间\\phantom{电梯上升米花的时间}}}$";

    private static String mExample5 = "${\\rm CO_2+4H_2\\;\n" +
            "\\mathop{\\substack{-\\!-\\!-\\!-\\!-\\!\\rightharpoonup \\\\ \\leftharpoondown\\!-\\!-\\!-\\!-\\!-}}^{phantom}_{\\triangle}\\;\n" +
            "CH_4+2H_2O}$";

    private static String mExample1 = "${ \\buildrel{溶解}\\over\\longrightarrow }$黏液${\\rightarrow }$体壁${\\rightarrow }$毛细血管${\\rightarrow }$";

    private static String mExample4 = "${3Fe+2O _{2}   \\mathop{\\substack{=\\!=\\!=\\!=\\!=\\!=}}^{点燃} Fe _{3} O _{4}}$";

    private static String mExample2 = "${a+b\\boldsymbol{i}}$ 和 ${a+b\\pmb{i}}$";
    public static String mExample21 = "${\\% \\Delta price = 100\\times [\\exp (a\\mathord{\\buildrel{\\lower0pt\\hbox{$\\scriptscriptstyle\\frown$}}\\over g} e) - 1] = 100\\times [\\exp ( - 0.008) - 1] =  - 0.8\\%}$";
    private static String mExample6 = "${\\mathop{\\substack{-\\!-\\!-\\!-\\!-\\!\\rightharpoonup \\\\ \\leftharpoondown\\!-\\!-\\!-\\!-\\!-}}^{催化剂}_{\\triangle}}$ ";

    private static String mExample7 = "解：∵${A\\cup B= \\{1,\\, 2\\}}$，则${A}$，${B}$均为${\\{1,\\, 2\\}}$的子集，" +
            "<br />即${A}$，${B\\in \\{\\varnothing ,\\, \\{1\\},\\, \\{2\\},\\, \\{1,\\, 2\\}\\}}$，" +
            "<br />当${A= \\varnothing }$时，${B= \\{1,\\, 2\\}}$，<br />当${A= \\{1\\}}$时，${B= \\{1,\\, 2\\}}$或${B= \\{2\\}}$，<br />当${A= \\{2\\}}$时，${B= \\{1,\\, 2\\}}$或${B= \\{1\\}}$，<br />${A= \\{1,\\, 2\\}}$时，${B= \\{1,\\, 2\\}}$，或${B= \\{1\\}}$，或${B= \\{2\\}}$，或${B= \\varnothing }$，<br />共${9}$种情况，<br />故选：${D}$";
    private static String mExample0 = "${CO _{2} +H _{2} O  \\mathrel{\\mathop{\\kern{0pt}\\longrightarrow}\\limits_{叶绿素}^{日光}}}$";
    private static String mExample03 = "${CuSO _{4} \\cdot 5H _{2} O}$";
    private static String mExample01 = "用${\\rm Pt}$电极电解${\\rm CuSO_{4}}$溶液，发生${\\rm 2CuSO_{4}+ 2H_{2}O \\mathop{\\substack{= \\!= \\!= \\!= \\!= \\!= }}^{电解\\phantom{电解}}2Cu+ O_{2}\\uparrow + 2H_{2}SO_{4}}$，只在阳极产生气体，由阴阳两极产生相同体积的气体，还发生${\\rm 2H_{2}O \\mathop{\\substack{= \\!= \\!= \\!= \\!= \\!= }}^{电解\\phantom{电解}}2H_{2}\\uparrow + O_{2}\\uparrow }$，结合转移的电子数来计算解答．";
    private static String mExample02 = "解：由${\\rm 2H_{2}O \\mathop{\\substack{= \\!= \\!= \\!= \\!= \\!= }}^{电解\\phantom{电解}}2H_{2}\\uparrow + O_{2}\\uparrow \\sim 4e^{-}}$，<br />${\\rm  2 1 4}$<br />${\\rm  0.2mol 0.1mol 0.4mol}$<br />则${\\rm 2CuSO_{4}+ 2H_{2}O \\mathop{\\substack{= \\!= \\!= \\!= \\!= \\!= }}^{电解\\phantom{电解}}2Cu+ O_{2}\\uparrow + 2H_{2}SO_{4}}$，<br />${\\rm  0.1mol 0.2mol}$<br />即当电路中通过${\\rm 0.4mol}$电子时，阴阳两极产生相同体积的气体时，生成${\\rm n(H_{2}SO_{4})= 0.2mol}$，<br />所以${\\rm n(H^{+ })= 0.4mol}$，<br />则${\\rm c(H^{+ })=  \\dfrac{0.4mol}{4L}= 0.1mol/L}$，<br />所以${\\rm pH= 1}$，故选${\\rm D}$．";
    private static String mExample8 = "${Cu  \\mathrel{\\mathop{\\kern{0pt}\\longrightarrow}\\limits_{\\triangle }^{H_{2}SO_{4}(浓)}} CuSO _{4}   \\stackrel{NaOH溶液}\\longrightarrow Cu (OH) _{2}}$";
    private static String[] mFormulaArray = new String[]{
            mExample21, 
            mExample0,
            mExample01, mExample02, mExample03, mExample21, mExample8, mExample1, mExample2, mExample3, mExample4, mExample5, mExample6, mExample7};
//    private static String[] mFormulaArray = new String[]{mExample2};

    public static String[] getFormulaArray() {
        return mFormulaArray;
    }
}

class MathFormula {
    public static String[] getFormulaArray() {
        String[] array = new String[] {
                Example1.getText(),
                Example2.getText(),
                Example3.getText(),
                Example4.getText(),
                Example5.getText(),


        };

        return array;
    }
}

/**
 * A class to test LaTeX rendering.
 **/
class Example1 {
    public static String getText() {

        String latex = "\\begin{array}{lr}\\mbox{\\textcolor{Blue}{Russian}}&\\mbox{\\textcolor{Melon}{Greek}}\\\\";
        latex += "\\mbox{" + "привет мир".toUpperCase() + "}&\\mbox{" + "γειά κόσμο".toUpperCase() + "}\\\\";
        latex += "\\mbox{привет мир}&\\mbox{γειά κόσμο}\\\\";
        latex += "\\mathbf{\\mbox{привет мир}}&\\mathbf{\\mbox{γειά κόσμο}}\\\\";
        latex += "\\mathit{\\mbox{привет мир}}&\\mathit{\\mbox{γειά κόσμο}}\\\\";
        latex += "\\mathsf{\\mbox{привет мир}}&\\mathsf{\\mbox{γειά κόσμο}}\\\\";
        latex += "\\mathtt{\\mbox{привет мир}}&\\mathtt{\\mbox{γειά κόσμο}}\\\\";
        latex += "\\mathbf{\\mathit{\\mbox{привет мир}}}&\\mathbf{\\mathit{\\mbox{γειά κόσμο}}}\\\\";
        latex += "\\mathbf{\\mathsf{\\mbox{привет мир}}}&\\mathbf{\\mathsf{\\mbox{γειά κόσμο}}}\\\\";
        latex += "\\mathsf{\\mathit{\\mbox{привет мир}}}&\\mathsf{\\mathit{\\mbox{γειά κόσμο}}}\\\\";
        latex += "&\\\\";
        latex += "\\mbox{\\textcolor{Salmon}{Bulgarian}}&\\mbox{\\textcolor{Tan}{Serbian}}\\\\";
        latex += "\\mbox{здравей свят}&\\mbox{Хелло уорлд}\\\\";
        latex += "&\\\\";
        latex += "\\mbox{\\textcolor{Turquoise}{Bielorussian}}&\\mbox{\\textcolor{LimeGreen}{Ukrainian}}\\\\";
        latex += "\\mbox{прывітаньне Свет}&\\mbox{привіт світ}\\\\";
        latex += "\\end{array}";

        return latex;
    }
}

/**
 * A class to test LaTeX rendering.
 **/
class Example2 {
    public static String getText() {

        String latex = "\\begin{array}{l}";
        latex += "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\";
        latex += "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\";
        latex += "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\";
        latex += "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\";
        latex += "\\end{array}";

        return latex;
    }
}

/**
 * A class to test LaTeX rendering.
 **/
class Example3 {
    public static String getText() {

        String latex = "\\definecolor{gris}{gray}{0.9}";
        latex += "\\definecolor{noir}{rgb}{0,0,0}";
        latex += "\\definecolor{bleu}{rgb}{0,0,1}\\newcommand{\\pa}{\\left|}";
        latex += "\\begin{array}{c}";
        latex += "\\JLaTeXMath\\\\";
        latex += "\\begin{split}";
        latex += " &Тепловой\\ поток\\ \\mathrm{Тепловой\\ поток}\\ \\mathtt{Тепловой\\ поток}\\\\";
        latex += " &\\boldsymbol{\\mathrm{Тепловой\\ поток}}\\ \\mathsf{Тепловой\\ поток}\\\\";
        latex += "|I_2| &= \\pa\\int_0^T\\psi(t)\\left\\{ u(a,t)-\\int_{\\gamma(t)}^a \\frac{d\\theta}{k} (\\theta,t) \\int_a^\\theta c(\\xi) u_t (\\xi,t)\\,d\\xi\\right\\}dt\\right|\\\\";
        latex += "&\\le C_6 \\Bigg|\\pa f \\int_\\Omega \\pa\\widetilde{S}^{-1,0}_{a,-} W_2(\\Omega, \\Gamma_1)\\right|\\ \\right|\\left| |u|\\overset{\\circ}{\\to} W_2^{\\widetilde{A}}(\\Omega;\\Gamma_r,T)\\right|\\Bigg|\\\\";
        latex += "&\\\\";
        latex += "&\\textcolor{magenta}{\\mathrm{Produit\\ avec\\ Java\\ et\\ \\LaTeX\\ par\\ }\\mathscr{C}\\mathcal{A}\\mathfrak{L}\\mathbf{I}\\mathtt{X}\\mathbb{T}\\mathsf{E}}\\\\";
        latex += "&\\begin{pmatrix}\\alpha&\\beta&\\gamma&\\delta\\\\\\aleph&\\beth&\\gimel&\\daleth\\\\\\mathfrak{A}&\\mathfrak{B}&\\mathfrak{C}&\\mathfrak{D}\\\\\\boldsymbol{\\mathfrak{a}}&\\boldsymbol{\\mathfrak{b}}&\\boldsymbol{\\mathfrak{c}}&\\boldsymbol{\\mathfrak{d}}\\end{pmatrix}\\quad{(a+b)}^{\\frac{n}{2}}=\\sqrt{\\sum_{k=0}^n\\tbinom{n}{k}a^kb^{n-k}}\\quad \\Biggl(\\biggl(\\Bigl(\\bigl(()\\bigr)\\Bigr)\\biggr)\\Biggr)\\\\";
        latex += "&\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "&\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "&\\Delta f(x,y)=\\frac{\\partial^2f}{\\partial x^2}+\\frac{\\partial^2f}{\\partial y^2}\\qquad\\qquad \\fcolorbox{noir}{gris}{n!\\underset{n\\rightarrow+\\infty}{\\sim} {\\left(\\frac{n}{e}\\right)}^n\\sqrt{2\\pi n}}\\\\";
        latex += "&\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\xrightarrow[T]{n\\pm i-j}\\sideset{^t}{}A\\xleftarrow{\\overrightarrow{u}\\wedge\\overrightarrow{v}}\\underleftrightarrow{\\iint_{\\mathds{R}^2}e^{-\\left(x^2+y^2\\right)}\\,\\mathrm{d}x\\mathrm{d}y}";
        latex += "\\end{split}\\\\";
        latex += "\\rotatebox{30}{\\sum_{n=1}^{+\\infty}}\\quad\\mbox{Mirror rorriM}\\reflectbox{\\mbox{Mirror rorriM}}";
        latex += "\\end{array}";

        return latex;
    }
}

/**
 * A class to test LaTeX rendering.
 **/
class Example4 {
    public static String getText() {

        String latex = "\\mbox{An image from the \\LaTeX3 project }\\includegraphics{src/test/resources/lion.png}";

        return latex;
    }
}

/**
 * A class to test LaTeX rendering.
 **/
class Example5 {
    public static String getText() {

        String latex = "\\begin{array}{|c|l|||r|c|}";
        latex += "\\hline";
        latex += "\\text{Matrix}&\\multicolumn{2}{|c|}{\\text{Multicolumns}}&\\text{Font sizes commands}\\cr";
        latex += "\\hline";
        latex += "\\begin{pmatrix}\\alpha_{11}&\\cdots&\\alpha_{1n}\\cr\\hdotsfor{3}\\cr\\alpha_{n1}&\\cdots&\\alpha_{nn}\\end{pmatrix}&\\Large \\text{Large Right}&\\small \\text{small Left}&\\tiny \\text{tiny Tiny}\\cr";
        latex += "\\hline";
        latex += "\\multicolumn{4}{|c|}{\\Huge \\text{Huge Multicolumns}}\\cr";
        latex += "\\hline";
        latex += "\\end{array}";

        return latex;
    }
}
