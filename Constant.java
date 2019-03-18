import java.math.BigInteger;

public class Constant { // 常数项
    private BigInteger constant;
    public Constant(BigInteger num) { // 构造器
        this.constant = num;
    }
    public BigInteger getConstant() {
        return constant;
    }
}
