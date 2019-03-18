import java.util.regex.Pattern;
/* Item = 项,指导书上的"乘法"类
 * 由乘法相联因子组成( Multiply ), 因子 * 表达式因子,切出来可能是表达式因子
 * 例如： 2*x^2*sin((x^33)*cos(x)^6)
 * 4 * ((x^2 + sin(cos(3*x)) -2333 ) * x^7 - 9*x^9),
 * 若发现()当中仍有expression级别的，继续切割，直到只有一个Item为止
 */
public class Item {
    private String string;
    private String[] cutItem = new String[100]; // 貌似有一点像Tree Node的功能
    private String[] deriTmp; // 用来存放每个被切割元素的导数
    private int type = 0;
    //TreeNode[] nodes = new TreeNode[100];

    public Item(String content) {
        this.string = content;
    }

    private String substring;
    private int itemNum = 1;
    public void Parse() { // 拆分连乘项
        char[] charArray = this.string.toCharArray();
        int nestDepth = 0;
        int nextBeginIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '(') {
                nestDepth++;
            } else if (charArray[i] == ')') {
                nestDepth--;
                //judge expresion
            } else if (i != 0 && (charArray[i] == '*') ) { // 拆连乘项
                if (nestDepth == 0) {
                    endIndex = i; // 下面截取该项
                    substring = string.substring(nextBeginIndex, endIndex);
                    System.out.println(substring);
                    nextBeginIndex = i + 1;
                    cutItem[itemNum - 1] = substring;
                    itemNum++;
                } else {
                    continue;
                }
            }
        }
        if (itemNum == 1) {
            cutItem[0] = string;
            System.out.println("Only have one Item : " + string);
        } else { // 把上面剩余的最后Item也弄出来
            substring = string.substring(nextBeginIndex,string.length());
            cutItem[itemNum - 1] = substring;
            System.out.println(substring);
        }
        cutItem[itemNum + 1] = "END";
    }

    public void cut() {
        this.Parse(); // 解析，分成可能的表达式项或者
        Pattern notNestConst = Pattern.compile("[+-]?\\d+"); // 常数项
        Pattern notNestPow = Pattern.compile("[+-]?(\\d+\\*)?x(\\^[+-]?\\d+)?");
        Pattern notNestSin = Pattern.compile("[+-]?(sin)\\(+x\\)+(\\^[+-]?\\d+)?"); //
        Pattern notNestCos = Pattern.compile("[+-]?(cos)\\(+x\\)+(\\^[+-]?\\d+)?"); //
        //cutItem = string.split("\\*"); // 这个切割十分草率，里面可能是 (expression) * (factor)
        // 必须确保是nested == 0 才可以切割否则还要扔给Expression
        deriTmp = new String[itemNum];
        for (int i = 0; i < itemNum; i++) { //i < cutItem.length && cutItem[i] != "END"
            System.out.println("This cutItem is " + cutItem[i]);
            if (cutItem[i].startsWith("(") && cutItem[i].endsWith(")")) {
                Expression exp = new Expression(cutItem[i]);
                deriTmp[i] = exp.DeriExp();
            }
            else if (cutItem[i].contains("sin")) {
                if (!notNestSin.matcher(cutItem[i]).matches()) {
                    System.out.println("WRONG FORMAT!");
                } else {
                    type = 2;
                    SinFunc sin = new SinFunc(cutItem[i]);
                    deriTmp[i] = sin.DeriSin();
                }
            } else if (cutItem[i].contains("cos")) {
                if (!notNestCos.matcher(cutItem[i]).matches()) {
                    System.out.println("WRONG FORMAT!");
                } else {
                    type = 3;
                    CosFunc cos = new CosFunc(cutItem[i]);
                    deriTmp[i] = cos.DeriCos();
                }
            } else if (cutItem[i].contains("x")) {
                if (!notNestPow.matcher(cutItem[i]).matches()) {
                    System.out.println("WRONG FORMAT!");
                } else {
                    type = 1;
                    PowerFunc pow = new PowerFunc(cutItem[i]);
                    deriTmp[i] = pow.DeriPower();
                }
            } else {
                if (!notNestConst.matcher(cutItem[i]).matches()) {
                    System.out.println("WRONG FORMAT!");
                } else {
                    type = 0;
                    deriTmp[i] = "0";
                }
            }

            System.out.println("Corresponding deriTmp is " + deriTmp[i]);
        }

    }

    public String DeriItem() {
        String s = "";
        for (int i = 0; i < deriTmp.length; i++) {
            for (int j = 0; j < i; j++) {
                s = s + cutItem[j] + "*";
            }
            s = s + deriTmp[i] + "*";
            for (int j = i + 1; j < deriTmp.length; j++) {
                s = s + cutItem[j] + "*";
            }
            s = s.substring(0, s.length() - 1);
            s = s + "+";
            System.out.println("Now s is " + s);
        }
        s = s.substring(0, s.length() - 1); // 去掉末尾的+
        return s;
    }

}
