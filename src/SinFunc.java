import java.math.BigInteger;
//正弦函数

public class SinFunc {
    private BigInteger sinCoeff = BigInteger.ONE; // 初始化为1,貌似不需要这个东西
    private BigInteger sinExpo = BigInteger.ZERO;
    private BigInteger grand = new BigInteger("10000");
    //提取出sin的系数和和次数，貌似只用次数就可以了，把系数全都堆到幂函数那里去
    // 然后再乘上 cos(x)，即系数不变，cosExpo++

    public BigInteger getSinExpo() {
        return sinExpo;
    }

    public BigInteger getSinCoeff() {
        return sinCoeff;
    }

    public SinFunc(String string) { // 提取次数给sinExpo
        String s = string;
        if (s.startsWith("-")) {
            sinCoeff = BigInteger.ZERO.subtract(BigInteger.ONE); // -sin(x)
            s = s.substring(1); // 弄掉负号
        }
        if (string.contains("^")) {
            s = s.replace("sin(x)^", "");
            sinExpo = new BigInteger(s,10);
        } else {
            sinExpo = BigInteger.ONE;
        }
        if (sinExpo.compareTo(grand) > 0) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Expo larger than a grand!");
        }
    }

    private BigInteger deriCoeff = BigInteger.ONE;
    private BigInteger deriExpo = BigInteger.ZERO;

    public String DeriSin() {
        String s = "";
        deriCoeff = sinCoeff.multiply(sinExpo);
        if (!sinExpo.equals(BigInteger.ZERO)) { // 指数!=0
            deriExpo = sinExpo.subtract(BigInteger.ONE);
        } else {
            deriExpo = BigInteger.ZERO;
        }

        s = deriCoeff + "*sin(x)^" + deriExpo;
        if (deriCoeff.equals(BigInteger.ONE)) {
            s = s.replace("1*","");
        }
        if (deriExpo.equals(BigInteger.ONE)) {
            s = s.replace("^1","");
        }
        s = s + "*cos(x)";
        return s;
    }

}
