package ch.eskaton.asn4j.runtime;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TagIdTest {

    @Test
    public void fromTag() {
        assertEquals(new TagId(Clazz.Application, 21), TagId.fromTag(getTag(TestA.class)));
    }

    @Test
    public void fromTags() {
        assertEquals(Arrays.asList(new TagId(Clazz.Private, 37), new TagId(Clazz.Application, 21)),
                TagId.fromTags(Arrays.asList(getTag(TestB.class), getTag(TestA.class))));
    }

    @Test
    public void equalsASN1Tag() {
        assertTrue(new TagId(Clazz.Application, 21).equalsASN1Tag(getTag(TestA.class)));
    }

    private ASN1Tag getTag(Class<?> clazz) {
        return clazz.getAnnotation(ASN1Tag.class);
    }

    @ASN1Tag(clazz = Clazz.Application, tag = 21, mode = ASN1Tag.Mode.Explicit, constructed = true)
    private static class TestA {

    }

    @ASN1Tag(clazz = Clazz.Private, tag = 37, mode = ASN1Tag.Mode.Explicit, constructed = true)
    private static class TestB {

    }

}
