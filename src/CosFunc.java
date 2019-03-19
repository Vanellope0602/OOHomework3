import java.math.BigInteger;
//余弦函数

public class CosFunc {
    private BigInteger cosCoeff = BigInteger.ONE; // 有可能是1！
    private BigInteger cosExpo = BigInteger.ZERO;
    private BigInteger grand = new BigInteger("10000");
    //提取出cos的系数和和次数，貌似只用次数就可以了，把系数全都堆到幂函数那里去
    // 然后再乘上 -sin(x), 系数变为负的，sinExpo++；

    public BigInteger getCosExpo() {
        return cosExpo;
    }

    public BigInteger getCosCoeff() {
        return cosCoeff;
    }

    public CosFunc(String string) { //提取次数
        String s = string;
        if (s.startsWith("-")) {
            cosCoeff = BigInteger.ZERO.subtract(BigInteger.ONE);
            s = s.substring(1); // 弄掉负号
        }
        if (s.contains("^")) {
            s = s.replace("cos(x)^", "");
            cosExpo = new BigInteger(s,10);
        } else {
            cosExpo = BigInteger.ONE;
        }
        if (cosExpo.compareTo(grand) > 0) {
            System.out.println("WRONG FORMAT!");
            System.out.println("Expo larger than a grand!");
        }
    }

    private BigInteger deriCoeff = BigInteger.ONE;
    private BigInteger deriExpo = BigInteger.ZERO;

    public String DeriCos() {
        String s = "";
        deriCoeff = cosCoeff.multiply(cosExpo).negate(); // cos(x)求导后系数变为相反数
        if (!cosExpo.equals(BigInteger.ZERO)) { // 指数!=0
            deriExpo = cosExpo.subtract(BigInteger.ONE);
        } else {
            deriExpo = BigInteger.ZERO;
        }

        s = deriCoeff + "*cos(x)^" + deriExpo;
        if (deriCoeff.equals(BigInteger.ONE)) {
            s = s.replace("1*","");
        }
        if (deriExpo.equals(BigInteger.ONE)) {
            s = s.replace("^1","");
        }
        s = s + "*sin(x)";
        //System.out.println("Cos deri : " + s + "deriCOeff is " + deriCoeff);
        return s;
    }
}
