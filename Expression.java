import java.util.Stack;

/* Expression = 表达式，表达式因子，指导书上的"加减法"类
 * (-1 + x^2 *sin(x) - cos((x)))
 * 这个作用貌似跟顶层mainClass是一样的
 */
public class Expression { // 外面应该是有最外层括号嵌套的
    private String string;
    private int itemNum = 1;
    //Stack<Character> stack = new Stack<>(); // 用栈分析，(...) 弹出
    TreeNode[] nodes = new TreeNode[100];

    public Expression(String content) {
        this.string = content;
        String input = string.substring(1,string.length()-1); //顶层表达式
        int nestDepth = 0;
        int nextBeginIndex = 0;
        int endIndex = 0;
        boolean bracket = false;
        String substring;
        char[] charArray = input.toCharArray();

        for (int i = 0; i < input.length(); i++) {
            if (charArray[i] == '(') {
                bracket = true;
                nestDepth++;
            } else if (charArray[i] == ')') {
                nestDepth--;
                if (nestDepth < 0) {
                    System.out.println("Found ')' when not in brackets !");  // (x+x+x) )  + sin(x*sin(x)
                    //return false;
                }
            } else if (i != 0 && (charArray[i] == '+' || charArray[i] == '-')
                    && (charArray[i - 1] != '*' && charArray[i - 1] != '^')) {
                if (nestDepth == 0) { // 是最外层的加减号，表明前面读完的是Item，多个factor的乘积
                    endIndex = i; // 下面截取该项
                    substring = input.substring(nextBeginIndex, endIndex);
                    System.out.println(substring);
                    nextBeginIndex = i;
                    nodes[itemNum - 1] = new TreeNode(substring);
                    System.out.println("Type: " + nodes[itemNum - 1].contentType());
                    itemNum++;
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        // if整个表达式只有一个项
        if (itemNum == 1) {
            nodes[0] = new TreeNode(input);
            System.out.println("Only have one Item : " + input);
            System.out.println("Type: " + nodes[itemNum - 1].contentType());
        } else { // 把上面剩余的最后Item也弄出来
            substring = input.substring(nextBeginIndex,input.length());
            nodes[itemNum - 1] = new TreeNode(substring);

            System.out.println("Type: " + nodes[itemNum - 1].contentType());
            System.out.println(substring);
        }
        System.out.println("total nums of items: " + itemNum);

    }

    public String DeriExp() {
        String s = "";
        for (int i = 0; i < itemNum; i++) {
            s = s + nodes[i].Deri() + "+";
        }
        s = s.substring(0,s.length() - 1);
        return "(" + s + ")";
    }
}
