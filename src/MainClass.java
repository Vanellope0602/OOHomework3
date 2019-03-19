import java.util.Scanner;
import java.util.regex.Pattern;

// 这次要把所有的非法情况都考虑清楚

public class MainClass {

    public boolean CheckValid(String string) {
        String input = string;
        // Empty string
        if (string.isEmpty() || string.matches("\\s*")) { //空串判断
            System.out.println("WRONG FORMAT!");
            System.out.println("Empty");
            return false;
        }
        //invalid blank space
        String invalidSpace = "(\\d+\\s+\\d+)"  // 12 34
                + "|([+-]\\s*[+-]\\s*[+-]\\s+\\d+)"// +++ 12
                + "|(\\^\\s*[+-]\\s+\\d+)|(\\*\\s*[+-]\\s+\\d+)" // ^- 2, *+ 3
                + "|([a-z]\\s+[a-z])" // s in, co s
                + "|((?<=\\*|\\^)[+-]{2,}(\\s+\\d+)?)" // *或^后两个符号，*- 3，^
                + ""; //
        Pattern invSpace = Pattern.compile(invalidSpace);
        if (invSpace.matcher(input).find()) {
            System.out.println("WRONG FORMAT!");
            System.out.println("invalid space");
            return false;
        } //下面检测非法字符
        String invalidChar = "([^0-9xsinco\\(\\)(+)(\\-)(\\*)(\\^) \\t\\r])";
        Pattern invChar = Pattern.compile(invalidChar);
        if (invChar.matcher(input).find()) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Invalid char");
            return false;
        }
        // 必须确认没有非法空格和非法字符后，才能把头尾的空格去掉
        // 然后判断有无非法写法
        input = input.replaceAll(" ","");
        if (input.startsWith("*") || input.startsWith("^")
                || input.endsWith("^") || input.endsWith("+")
                || input.endsWith("*")
                || input.endsWith("-")) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Illegal head or tail");
            return false;
        }
        String wrongPattern = "([+-]{3,}[a-z])" // +++x, +++sin(x)
                + "|([+-]{4,})|(\\*[+-](?!\\d+))" // 连续4个以上符号， *-,*+后面非数字
                + "|(\\*[+-]x)|(\\^[+-]x)" // 2*+x, ^+x
                + "|(\\(\\))|(\\d+\\()|(\\)\\d+)"  //括号里是空的(),括号连数字
                + "|(\\d+\\^\\d+)" //eg. x^sin(x), 2^, 8^21
                + "|([\\^]{2,})|([\\*]{2,})|(x{2,})"  // ^^,**,xx
                + "|([+-]\\^)|([+-]\\*)" // +^, ^-, +*
                + "|(\\*\\^)|(\\^\\*)" // * ^, ^ *
                ;
        Pattern wrongP = Pattern.compile(wrongPattern);
        if (wrongP.matcher(input).find()) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Illegal pattern");
            return false;
        }
        int leftBracket = string.replace("(","").length();
        int rightBracket = string.replace(")","").length();
        if (leftBracket != rightBracket
                || input.startsWith(")") || input.endsWith("(")) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Bracket");
            return false;
        } //下面判断左右括号个数数否相同
        return true;
    }

    public String preTreatment(String input) {
        String fixedInput = input;
        // 去掉合法空格
        fixedInput = fixedInput.replaceAll("\\s+","");
        // ^+ , ^ , *+, *
        fixedInput = fixedInput
                .replaceAll("(\\^[+])","^")
                .replaceAll("(\\*[+])","*");

        // 归于正号
        fixedInput = fixedInput.replaceAll("([+][-][-])" +
                "|([-][-][+])" + // +--,--+
                "|([+]{2,3})|([-][-])|([-][+][-])","+"); // ++, +++ ,--, -+-
        //换符号,+-+,-++,++- 归于负号
        fixedInput = fixedInput.replaceAll("([-][+])|([-]{3})" + // -+， ---
                "|([+][-][+])|([-][+][+])|([+][+][-])","-");

        // 不是幂次的减号，不是在乘号后面的减号,不是左括号后的+-，换成-，反向否定预查
        fixedInput = fixedInput.replaceAll("(?<!\\^|\\*|\\+\\()[+][-]", "-");
        // 便于用 + 加号切割字符串
        if (fixedInput.startsWith("+")) {
            fixedInput = fixedInput.substring(1);
        }
        return fixedInput;

    }

    private TreeNode[] nodes = new TreeNode[100]; // 先给100个节点
    private int itemNum = 1;

    public void Distribute(String string) {
        String input = string; //顶层表达式
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
            } else if (i != 0 && (charArray[i] == '+' || charArray[i] == '-')
                      && (charArray[i - 1] != '*' && charArray[i - 1] != '^')) {
                if (nestDepth == 0) { // 是最外层的加减号，表明前面读完的是Item，多个factor的乘积
                    endIndex = i; // 下面截取该项
                    substring = input.substring(nextBeginIndex, endIndex);
                    //System.out.println(substring);
                    nextBeginIndex = i;
                    nodes[itemNum - 1] = new TreeNode(substring);
                    //System.out.println("Type: "
                    // + nodes[itemNum - 1].contentType());
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
            //System.out.println("Main :Only have one Item : " + input);
            //System.out.println("Type: " + nodes[itemNum - 1].contentType());
        } else { // 把上面剩余的最后Item也弄出来
            substring = input.substring(nextBeginIndex,input.length());
            nodes[itemNum - 1] = new TreeNode(substring);
            //System.out.println("Type: " + nodes[itemNum - 1].contentType());
            //System.out.println(substring);
        }
        //System.out.println("total nums of items: " + itemNum);
    }

    public void DeriMain() { //
        String toBePrint = "";
        for (int i = 0; i < itemNum; i++) {
            toBePrint = toBePrint + nodes[i].Deri() + "+";
        }
        System.out.print(toBePrint.substring(0, toBePrint.length() - 1));
        //System.out.println("\nDerivation done.");
    }

    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);
            if (!s.hasNextLine()) { // none line
                System.out.println("WRONG FORMAT!");
                System.exit(0);
            }
            String input = s.nextLine();
            MainClass newMain = new MainClass();
            if (!newMain.CheckValid(input)) {
                System.exit(0);
            }
            input = newMain.preTreatment(input); // pretreated the string
            //System.out.println("After pretreatment : " + input);
            newMain.Distribute(input);  // get the elements into the tree
            newMain.DeriMain();

            //System.out.println("All done.");
        } catch (Exception e) {
            System.out.println("WRONG FORMAT!");
        }

    }
}
