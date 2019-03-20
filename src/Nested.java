import java.math.BigInteger;
import java.util.regex.Pattern;

/* Nested = 嵌套因子，指导书上的嵌套类
 * 例如： sin(sin(x^2)^3)^8
 * 从最外层检查是否有次方，记下来
 * 新建TreeNode，继续下一层判断是什么类型
 * sin(23*x^2+90) 这样输入是错的！里面的表达式因子必须要嵌套才可以使用！
 * 也就是说，sin( ),cos( ) 里面要么是cos(x),sin(x),要么就是括号装起来的表达式！
 */
public class Nested {
    private String content;
    private String inside;
    private BigInteger coeff = BigInteger.ONE;
    private BigInteger expo = BigInteger.ONE;
    private String num = "";
    private int triType = 0;

    public Nested(String string) { // 最外层就是三角函数，其括号里面还有一些奇奇怪怪的东西
        this.content = string;
        char[] charArray = content.toCharArray();
        //System.out.println("Put in nested string: " + content);
        int nestDepth = 0;
        int beginIndex = 0;
        int endIndex = 0;
        boolean triangleOccur = false;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '(') {
                nestDepth++;
                if (triangleOccur && nestDepth == 1) { // 提取括号内inside内容
                    beginIndex = i + 1;
                    if (charArray[i - 1] == 'n') { // 外面嵌套的是sin
                        triType = 1;
                    } else if (charArray[i - 1] == 's') { // 外面嵌套的是cos
                        triType = 2;
                    }
                }
            } else if (charArray[i] == ')') {
                nestDepth--;
                if (nestDepth < 0) {
                    System.out.println("WRONG FORMAT!");
                    System.out.println("Nested found ')' when outside");
                } else if (nestDepth == 0) {
                    endIndex = i;
                }
            } else if (charArray[i] == 's' && charArray[i + 1] == 'i'
                    && charArray[i + 2] == 'n' && nestDepth == 0) {
                triangleOccur = true;
                triType = 1;
            } else if (charArray[i] == 'c' && charArray[i + 1] == 'o'
                    && charArray[i + 2] == 's' && nestDepth == 0) {
                triangleOccur = true;
                triType = 2;
            } else if (charArray[i] == '^' && nestDepth == 0) {
                String tmp = content.substring(i + 1, content.length());
                expo = new BigInteger(tmp,10);
            } else if (charArray[i] == '-' && nestDepth == 0) {
                coeff = coeff.negate(); // 前面带有负号
            }
        }
        if (triType == 0) {
            System.out.println("WRONG FORMAT!");
            System.out.println("no triangle function");
        }
        inside = content.substring(beginIndex, endIndex);
        //System.out.println("Nested Inside: " + inside);
        BigInteger grand = new BigInteger("10000");
        if (expo.compareTo(grand) > 0) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Expo larger than a grand!");
        }
        if (!JudgeFactor()) {
            System.out.println("WRONG FORMAT!");
            System.out.println("inside not a factor");
        }
    }

    private BigInteger deriCoeff = BigInteger.ONE;
    private BigInteger deriExpo = BigInteger.ONE;

    public String DeriNest() { // 这个三角函数系数求导后还有一点问题哦！是数学问题哦！早上起来看吧
        deriCoeff = coeff.multiply(expo);
        deriExpo = expo.subtract(BigInteger.ONE);
        String s = "";
        TreeNode tmp = new TreeNode(inside);
        String tmpDeri = tmp.Deri();
        if (triType == 1) {  //sin(sin((2*x)))
            //System.out.println("outside is sinFunc");
            if (!deriCoeff.equals(BigInteger.ONE)) {
                s = deriCoeff + "*sin(" + inside + ")";
            } else {
                s = "sin(" + inside + ")";
            }
            if (!deriExpo.equals(BigInteger.ONE)) {
                s = s + "^" + deriExpo; // 次方不为1，可以加上
            }
            s = s + "*" + "cos(" + inside + ")";

        } else if (triType == 2) {
            deriCoeff = deriCoeff.negate();
            //System.out.println("Outside cosfunc, dericoeff is" + deriCoeff);
            if (!deriCoeff.equals(BigInteger.ONE)) {
                s = deriCoeff + "*cos(" + inside + ")";
            } else {
                s = "cos(" + inside + ")";
            }
            if (!deriExpo.equals(BigInteger.ONE)) {
                s = s + "^" + deriExpo; // 次方不为1，可以加上
            }
            s = s + "*" + "sin(" + inside + ")";
        }

        // sin(cos(x)^2)^9 这个返回的是 9*cos(cos(x)^2)^ 8套壳
        // 返回  9*cos(inside)^8
        s = s + "*" + tmpDeri;
        return s;
    }
    //准确的说，这些名字应该叫 "not factor pow, not factor sin"

    private Pattern notNestConst = Pattern.compile("[+-]?\\d+"); // 常数项
    private Pattern notNestPow = // +x^2 不算因子，可拆分成+1*x^2是一个项
            Pattern.compile("x(\\^[+-]?\\d+)?");
    private Pattern notNestSin =
            Pattern.compile("(sin)\\(+x\\)+(\\^[+-]?\\d+)?"); //
    private Pattern notNestCos =
            Pattern.compile("(cos)\\(+x\\)+(\\^[+-]?\\d+)?"); //
    private Pattern symbolLetter = Pattern.compile("[+-][a-z]");
    // -x^2, +sin(x)这些都不算因子，找到

    public boolean JudgeFactor() {
        //System.out.println("inside: " + inside);
        if (notNestConst.matcher(inside).matches()
                || notNestPow.matcher(inside).matches()
                || notNestSin.matcher(inside).matches()
                || notNestCos.matcher(inside).matches()) {
            return true;
        }
        char[] charArray = inside.toCharArray();
        int nestDepth = 0;
        int itemNum = 1;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '(') {
                nestDepth++;
                //System.out.println("Nestdepth++ =  " + nestDepth);
                if (nestDepth == 0 &&
                        symbolLetter.matcher(inside.substring(i)).find()) {
                    System.out.println("WRONG FORMAT!");
                    System.out.println("symbolletter");
                    return false;
                }
            } else if (charArray[i] == ')') {
                nestDepth--;
                //System.out.println("Nestdepth-- " + nestDepth);
                if (nestDepth == 0 &&
                        symbolLetter.matcher(inside.substring(i)).find()) {
                    System.out.println("WRONG FORMAT!");
                    System.out.println("symbolletter");
                    return false;
                }
                //judge expresion
            } else if (i != 0 && (charArray[i] == '*')) { // 拆连乘项
                if (nestDepth == 0) {
                    itemNum++;
                    return false;
                } else {
                    continue;
                }
            } else if (nestDepth == 0 &&
                    (charArray[i] == '-' || charArray[i] == '+')) {
                String tmp = Character.toString(charArray[i]);
                tmp = tmp + charArray[i + 1]; // 不是因子！
                if (symbolLetter.matcher(tmp).matches()) {
                    return false;
                }
            }

        }
        //System.out.println("inside itemNum : " + itemNum);
        if (itemNum == 1) {
            if (inside.startsWith("(") && inside.endsWith(")")) {
                return true;
            } else {
                return true;
            }
        } else {
            return true;
        }


    }

    //拆掉最外面那一层括号以后，括号内的东西提取出来，cos(x)^2丢给新的TreeNode
    // CSDN上有个博客：用正则表达式提取括号内的内容
}