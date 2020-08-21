package ch.eskaton.asn4j.runtime.encoders;

import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public interface AbstractOIDEncoder<T extends ASN1Type> extends TypeEncoder<T> {

    default void writeComponent(ByteArrayOutputStream value, int component) {
        var list = new LinkedList<Integer>();

        while (component > 127) {
            list.push(component & 0x7F);
            component >>= 7;
        }

        list.push(component);

        var listSize = list.size();

        for (var j = 0; j < listSize; j++) {
            if (j == listSize - 1) {
                value.write(list.get(j));
            } else {
                value.write(list.get(j) | 0x80);
            }
        }
    }

}
