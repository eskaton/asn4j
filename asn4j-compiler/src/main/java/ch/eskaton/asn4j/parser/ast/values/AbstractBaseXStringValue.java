package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.commons.utils.StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractBaseXStringValue extends AbstractValue {

    private int base;

    protected String value;

    protected AbstractBaseXStringValue(Position position, String value, int base) {
        super(position);

        this.value = value;
        this.base = base;
    }

    public BitStringValue toBitString() {
        byte[] bytes = value.length() == 0 ? new byte[] {} : new BigInteger(value, base).toByteArray();
        int unitLength = getUnitLength();
        int length = (int) Math.ceil((float) value.length() / unitLength);

        if (bytes.length > length) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }

        return new BitStringValue(getPosition(), bytes,
                value.length() % unitLength != 0 ? 8 - (value.length() * 8 / unitLength % 8) : 0);
    }

    public OctetStringValue toOctetString() {
        int unitLength = getUnitLength();
        int excess = value.length() % unitLength;
        int trailer = excess != 0 ? unitLength - excess : 0;

        if (trailer != 0) {
            value += StringUtils.repeat("0", trailer);
        }

        byte[] buf = new byte[value.length() / unitLength];

        for (int i = 0, pos = 0; pos < value.length(); i++, pos += unitLength) {
            buf[i] = (byte) Short.parseShort(value.substring(pos, pos + unitLength), base);
        }

        return new OctetStringValue(getPosition(), buf);
    }

    private int getUnitLength() {
        return (int) (Math.log(256) / Math.log(base));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractBaseXStringValue that = (AbstractBaseXStringValue) o;

        return base == that.base &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, value);
    }

}
