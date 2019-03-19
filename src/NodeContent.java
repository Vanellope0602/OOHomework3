import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 树节点中存的内容, 可能是一个Item，Expression，Factor（Power,sin, cos, nested）
 * NodeContent有Judge Type， sorter的功能，识别计算种类（加减法，乘法，嵌套），单个项的函数种类
 * Content 可以嵌套Content，在这里递归建立链式求导
 * 如果是一个表达式因子，则这个content系数不能大于1，丢给Expression处理
 * 若没有嵌套，（sin(x)， sin(((x)))不算嵌套），则丢给Item处理，Item切割后丢给Factor处理
 * 该类相当于放在TreeNode中使用，A TreeNode has a NodeContent
 */
public class NodeContent {
    private BigInteger expo = BigInteger.ONE;
    private BigInteger coeff = BigInteger.ZERO; //若有三角函数嵌套，则需要看有无Base和指数
    private String string;
    private boolean nested = false;
    private int type = 0;
    // type 0 : 常数，1：幂函数，2：正弦sin因子，3：余弦cos因子，4：连乘Item，5：嵌套因子，6：表达式因子
    private Pattern notNestConst = Pattern.compile("[+-]?\\d+"); // 常数项
    private Pattern notNestPow =
            Pattern.compile("[+-]?(\\d+\\*)?x(\\^[+-]?\\d+)?");
    private Pattern notNestSin =
            Pattern.compile("[+-]?(sin)\\(+x\\)+(\\^[+-]?\\d+)?"); //
    private Pattern notNestCos =
            Pattern.compile("[+-]?(cos)\\(+x\\)+(\\^[+-]?\\d+)?"); //

    public void setContent(String s) { //还要判断表达式因子有无嵌套，有则WF
        this.string = s;
        // "sin(" 后面不是x的"左括号"，sin( 1 + cos(x)), cos(sin(x^2))
        // 或"sin(x" 后面不是")", 而有一些奇怪的东西潜入
        Pattern nestBracket = Pattern.compile("(sin\\((?!x))|(cos\\((?!x))" +
                "|(sin\\(x(?!\\)))|(cos\\(x(?!\\)))");
        Matcher nest = nestBracket.matcher(string);
        if (nest.find()) {  // 接下来看sin,cos中嵌套
            // 要先判断最外层是不是带括号扩起来或者是"表达式因子"和其他因子的连乘项，如果是连乘，则type == 4
            if (JudgeExp(string)) { // 还有可能是Type4连乘，现在想不出好的判断方法
                type = 6;
            } else if (JudgeItem(string)) {
                type = 4;
            } else {
                type = 5; // nested;
            }
            nested = true;
        } else { // 单个因子？（因子连乘的）项？
            if (JudgeExp(string)) { // 表达式？
                type = 6;
            } else if (string.contains("*")) { // 这就是一个连乘的Item
                if (notNestPow.matcher(string).matches()) {
                    type = 1;
                } else if (JudgeItem(string)) {
                    type = 4;
                }
            } else if (string.contains("sin")) {
                if (!notNestSin.matcher(string).matches()) {
                    System.out.println("WRONG FORMAT!");
                    //System.out.println("content have sin but not sinfunc");
                } else {
                    type = 2;
                }
            } else if (string.contains("cos")) {
                if (!notNestCos.matcher(string).matches()) {
                    System.out.println("WRONG FORMAT!");
                    //System.out.println("content have cos but ont cosfunc");
                } else {
                    type = 3;
                }
            } else if (string.contains("x")) {
                if (!notNestPow.matcher(string).matches()) {
                    System.out.println("WRONG FORMAT!");
                    //System.out.println("Content have x but not powerfunc");
                } else {
                    type = 1;
                }
            } else {
                if (!notNestConst.matcher(string).matches()) {
                    System.out.println("WRONG FORMAT!");
                    System.out.println("Not anything including constant ");
                } else {
                    type = 0;
                }
            }

        }
        if ((string.startsWith("(") || string.startsWith("-(")
                || string.startsWith("+(")) && string.endsWith(")")) {
            //String tmp = string.substring(1, string.length() - 1);
            if (JudgeExp(string)) { // 去掉最外层括号如果还发现是表达式
                type = 6;
            }  // 丢给expression？
            else if (JudgeItem((string))) {
                type = 4;
            }
        }
        //System.out.println("The content " + string + " type is: " + type);
    }

    public int getType() {
        return type;
    }

    public String DeriContent() {
        if (this.type == 0) { // constant
            return "0"; // string 0
        } else if (this.type == 1) { // powerFunc
            PowerFunc p = new PowerFunc(string);
            return p.DeriPower(); // string

        } else if (this.type == 2) { // Sinfunc
            SinFunc sin = new SinFunc(string);
            return sin.DeriSin();

        } else if (this.type == 3) { // CosFunc
            CosFunc cos = new CosFunc(string);
            return cos.DeriCos();

        } else if (this.type == 4) { // 连乘Item（有上述因子/表达式因子）不会有嵌套
            Item item = new Item(string);
            item.cut();
            return item.DeriItem();
        } else if (this.type == 5) { // nested
            Nested nest = new Nested(string);
            return nest.DeriNest();
        } else { // type == 6
            Expression exp = new Expression(string);
            //System.out.println("Content: deriExp " + exp.DeriExp());
            return exp.DeriExp();
        }
    }

    public boolean JudgeExp(String string) { // 判断是否是"表达式因子"
        int itemNum = 1;
        String input = string; //顶层表达式
        int nestDepth = 0;
        char[] charArray = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            if (charArray[i] == '(') {
                nestDepth++;
            } else if (charArray[i] == ')') {
                nestDepth--;
                if (nestDepth < 0) {
                    System.out.println("WRONG FORMAT!");
                    System.out.println("content Found ')' not in brackets !");
                    return false;
                }
            } else if (i != 0 && (charArray[i] == '+' || charArray[i] == '-')
                    && (charArray[i - 1] != '*' && charArray[i - 1] != '^')) {
                if (nestDepth == 0) { // 是最外层的加减号，表明前面读完的是Item，多个factor的乘积
                    //System.out.println("More items!");
                    itemNum++;
                } else {
                    continue;
                }
            } else if (i != 0 && (charArray[i] == '+' || charArray[i] == '-')
                    && charArray[i + 1] == '(') { // -(x) maybe
                if (nestDepth == 0) {
                    // wait
                }

            } else {
                continue;
            }
        }
        // if整个表达式只有一个项
        if (itemNum == 1) { //这个地方有问题，它依然可能是Item类 (x^2+x^3) * cos(x)，误判成了表达式
            //System.out.println("this content Only have one Item : " + input);
            if (((string.startsWith("(") || string.startsWith("-(")
                    || string.startsWith("+("))) && input.endsWith(")")) {
                if (JudgeItem(input)) {
                    return false; // 应该是Item
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else { // 把上面剩余的最后Item也弄出来
            return true;
        }
    }

    public boolean JudgeItem(String string) { // 判断是否应该是Item类型
        char[] charArray = string.toCharArray();
        int nestDepth = 0;
        int itemNum = 1;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '(') {
                nestDepth++;
            } else if (charArray[i] == ')') {
                nestDepth--;
                //judge expresion
            } else if (i != 0 && (charArray[i] == '*')) { // 拆连乘项
                if (nestDepth == 0) {
                    itemNum++;
                    return true;
                } else {
                    continue;
                }
            }
        }
        if (itemNum == 1) {
            return false;
        } else {
            return true;
        }

    }

}

