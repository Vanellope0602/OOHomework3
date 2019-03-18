/* Factor = 因子， item切割掉乘法后的东西
 * 包括变量因子（幂函数、三角函数），常数因子，表达式因子（在括号里的东西），嵌套因子
 * 可以把JudgeType放在这里？
 * 如果无法区分是不是某种单一类型的函数则为nested，把字符串丢给nest处理
 * 最后nested也会拆成Factor（最小的因子，不要表达式）
 */
public class Factor {
    public Factor() {

    }
}
