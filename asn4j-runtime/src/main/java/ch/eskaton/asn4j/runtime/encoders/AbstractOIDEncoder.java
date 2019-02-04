package ch.eskaton.asn4j.runtime.encoders;

import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public abstract class AbstractOIDEncoder<T extends ASN1Type> implements TypeEncoder<T> {

    public void writeComponent(ByteArrayOutputStream value, int component) {
        LinkedList<Integer> list = new LinkedList<>();

        while (component > 127) {
            list.push(component & 0x7F);
            component >>= 7;
        }

        list.push(component);

        int listSize = list.size();

        for (int j = 0; j < listSize; j++) {
            if (j == listSize - 1) {
                value.write(list.get(j));
            } else {
                value.write(list.get(j) | 0x80);
            }
        }
    }

}