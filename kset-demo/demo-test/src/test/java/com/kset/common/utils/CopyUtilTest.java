package com.kset.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CopyUtilTest {

    @Test
    void shouldReturnNullForNullSource() {
        assertNull(CopyUtil.deepCopy(null, String.class));
        assertNull(CopyUtil.deepCopy(null, new TypeReference<List<String>>() {
        }));
    }

    @Test
    void shouldDeepCopyPojoWithoutSharingReferences() {
        record Address(String city) {
        }

        record User(String name, Address address, List<String> tags) {
        }

        User source = new User("alice", new Address("BJ"), new ArrayList<>(List.of("a", "b")));
        User copy = CopyUtil.deepCopy(source, User.class);

        assertEquals(source, copy);
        assertNotSame(source, copy);
        assertNotSame(source.address(), copy.address());
        assertNotSame(source.tags(), copy.tags());

        copy.tags().add("c");
        assertEquals(2, source.tags().size());
    }

    @Test
    void shouldDeepCopyGenericList() {
        List<Map<String, Integer>> source = List.of(Map.of("score", 90), Map.of("score", 80));
        List<Map<String, Integer>> copy = CopyUtil.deepCopy(source, new TypeReference<>() {
        });

        assertEquals(source, copy);
        assertNotSame(source, copy);
        assertNotSame(source.get(0), copy.get(0));
    }

    @Test
    void shouldDeepCopyPojoWithLocalDateTime() {
        record Event(String name, LocalDateTime createdAt, List<String> tags) {
        }

        LocalDateTime time = LocalDateTime.of(2024, 6, 7, 14, 5, 30);
        Event source = new Event("bob", time, new ArrayList<>(List.of("x", "y")));
        Event copy = CopyUtil.deepCopy(source, Event.class);

        assertEquals(source, copy);
        assertNotSame(source, copy);
        assertNotSame(source.tags(), copy.tags());

        copy.tags().add("z");
        assertEquals(2, source.tags().size());
    }

    @Test
    void shouldNotAffectSourceWhenCopyIsModified() {
        MutableAddress address = new MutableAddress("BJ");
        MutableUser source = new MutableUser("alice", address, new ArrayList<>(List.of("a", "b")));
        MutableUser copy = CopyUtil.deepCopy(source, MutableUser.class);

        // 修改拷贝的顶层字段
        copy.setName("bob");
        assertEquals("alice", source.getName());

        // 修改拷贝的嵌套对象
        copy.getAddress().setCity("SH");
        assertEquals("BJ", source.getAddress().getCity());

        // 修改拷贝的 List：增删改
        copy.getTags().add("c");
        copy.getTags().remove("a");
        assertEquals(List.of("a", "b"), source.getTags());

        // 源对象未被拷贝引用污染
        assertNotSame(source, copy);
        assertNotSame(source.getAddress(), copy.getAddress());
        assertNotSame(source.getTags(), copy.getTags());
    }

    @Test
    void shouldNotAffectSourceMapWhenCopyIsModified() {
        Map<String, List<String>> source = new LinkedHashMap<>();
        source.put("group-a", new ArrayList<>(List.of("x", "y")));
        source.put("group-b", new ArrayList<>(List.of("m")));

        Map<String, List<String>> copy = CopyUtil.deepCopy(source, new TypeReference<>() {
        });

        copy.put("group-c", new ArrayList<>(List.of("new")));
        copy.get("group-a").add("z");
        copy.get("group-a").clear();
        copy.remove("group-b");

        assertEquals(2, source.size());
        assertTrue(source.containsKey("group-a"));
        assertTrue(source.containsKey("group-b"));
        assertEquals(List.of("x", "y"), source.get("group-a"));
        assertEquals(List.of("m"), source.get("group-b"));
        assertNotSame(source, copy);
        assertNotSame(source.get("group-a"), copy.get("group-a"));
    }

    @Test
    void shouldNotAffectSourceWhenNestedListElementIsModified() {
        record Item(String code, List<String> attrs) {
        }

        List<Item> source = new ArrayList<>(List.of(
                new Item("A", new ArrayList<>(List.of("red", "blue"))),
                new Item("B", new ArrayList<>(List.of("green")))));
        List<Item> copy = CopyUtil.deepCopy(source, new TypeReference<>() {
        });

        copy.get(0).attrs().set(0, "black");
        copy.get(0).attrs().add("yellow");
        copy.get(1).attrs().clear();

        assertEquals("red", source.get(0).attrs().get(0));
        assertEquals(2, source.get(0).attrs().size());
        assertEquals(List.of("green"), source.get(1).attrs());
    }

    @Test
    void shouldRejectUnserializableSource() {
        assertThrows(IllegalArgumentException.class,
                () -> CopyUtil.deepCopy(Thread.currentThread(), Thread.class));
    }

    static class MutableAddress {

        private String city;

        public MutableAddress() {
        }

        public MutableAddress(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    static class MutableUser {

        private String name;
        private MutableAddress address;
        private List<String> tags;

        public MutableUser() {
        }

        public MutableUser(String name, MutableAddress address, List<String> tags) {
            this.name = name;
            this.address = address;
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public MutableAddress getAddress() {
            return address;
        }

        public void setAddress(MutableAddress address) {
            this.address = address;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}
