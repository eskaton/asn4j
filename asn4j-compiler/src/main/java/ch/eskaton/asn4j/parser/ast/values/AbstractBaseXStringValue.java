package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.commons.utils.StringUtils;

import java.math.BigInteger;

public class AbstractBaseXStringValue implements Value {

    private int base;

    protected String value;

    public AbstractBaseXStringValue(String value, int base) {
        this.value = value;
        this.base = base;
    }

    public byte[] getBytes() {
        return new BigInteger(value, base).toByteArray();
    }

    public BitStringValue toBitString() {
        return new BitStringValue(Integer.parseInt(value, base));
    }

    public OctetStringValue toOctetString() {
        int unitLength = (int) (Math.log(256) / Math.log(base));
        int trailor = unitLength - value.length() % unitLength;

        if (trailor != 0) {
            value += StringUtils.repeat("0", trailor);
        }

        char[] buf = new char[value.length() / unitLength];

        for (int i = 0, pos = 0; pos < value.length(); i++, pos += unitLength) {
            buf[i] = (char) Short.parseShort(value.substring(pos, pos + unitLength), base);
        }

        return new OctetStringValue(buf);
    }

}
