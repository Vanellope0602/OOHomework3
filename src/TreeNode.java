// 项或表达式必须外面加上括号才可以嵌套到三角函数当中，否则WF
// 若发现裸露的项或表达式在sin (  )中，则为WF
// Tree node

public class TreeNode {
    private NodeContent content;
    private TreeNode[] children = new TreeNode[100];
    private int childNum = 0;

    private TreeNode father;

    public TreeNode() {

    }

    public TreeNode(String s) { // 构造器,字符串放到Content里面
        this.content = new NodeContent();
        this.content.setContent(s);
    }

    public void setChild(TreeNode child) {
        this.children[childNum + 1] = child;
        this.childNum++;
    }

    public String Deri() { // 一个content的求导后的字符串
        return content.DeriContent();
    }

    public int contentType() {
        return content.getType();
    }

}
