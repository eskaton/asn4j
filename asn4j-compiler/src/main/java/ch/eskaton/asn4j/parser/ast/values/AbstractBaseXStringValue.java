package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.commons.utils.StringUtils;

import java.math.BigInteger;

public class AbstractBaseXStringValue extends AbstractValue {

    private int base;

    protected String value;

    public AbstractBaseXStringValue(Position position, String value, int base) {
        super(position);

        this.value = value;
        this.base = base;
    }

    public BitStringValue toBitString() {
        return new BitStringValue(getPosition(), new BigInteger(value, base).toByteArray(),
                value.length() != 0 ? 8 - value.length() % 8 : 0);
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

        return new OctetStringValue(getPosition(), buf);
    }

}
