package us.kbase.test.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import us.kbase.common.service.UObject;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class UObjectTest {

	@Test
	public void testErrors() {
		try {
			new UObject(new ByteArrayOutputStream());
			Assert.fail();
		} catch (IllegalArgumentException ex) {
			Assert.assertTrue(ex.getMessage().contains("UObject can not serialize"));
		}
		try {
			new UObject(System.out);
			Assert.fail();
		} catch (IllegalArgumentException ex) {
			Assert.assertTrue(ex.getMessage().contains("UObject can not serialize"));
		}
	}
	
	@Test
	public void testBytesCharsArrays() {
		String json = "[1,2]";
		byte[] bytes = json.getBytes();
		Assert.assertEquals(json, new UObject(bytes).toJsonString());
		char[] chars = json.toCharArray();
		Assert.assertEquals(json, new UObject(chars).toJsonString());
		int[] array = {1,2};
		Assert.assertEquals(json, new UObject(array).toJsonString());
	}
	
	@Test
	public void testSerialization() throws Exception {
		IntStruct intStr = new IntStruct();
		Map<String, Long> map1 = new LinkedHashMap<String, Long>();
		map1.put("0", 0L);
		map1.put("1", 1L);
		Tuple2<Long, Long> tuple1 = new Tuple2<Long, Long>().withE1(1L).withE2(0L);
		intStr.withVal1(0L).withVal2(Arrays.asList(1L, 0L, 1L)).withVal3(map1).withVal4(tuple1);
		List<Object> forTest = Arrays.asList(5, "testing", 17.44, intStr);
		for (Object val : forTest) {
			ObjectStruct objStr = new ObjectStruct().withVal1(new UObject(val)).withVal2(Arrays.asList(new UObject(val)));
			Map<String, UObject> map2 = new LinkedHashMap<String, UObject>();
			map2.put("key", new UObject(val));
			Tuple2<UObject, UObject> tuple2 = new Tuple2<UObject, UObject>().withE1(new UObject(val)).withE2(null);
			objStr.withVal3(map2).withVal4(tuple2);
			Tuple2<UObject, ObjectStruct> ret2 = objectCheck(new UObject(val), objStr);
			checkObject(val, ret2.getE1());
			checkObject(val, ret2.getE2().getVal1());
			checkObject(val, ret2.getE2().getVal2().get(0));
			checkObject(val, ret2.getE2().getVal3().get("key"));
			checkObject(val, ret2.getE2().getVal4().getE1());
			Assert.assertNull(ret2.getE2().getVal4().getE2());
			if (val instanceof IntStruct) {
				checkIntStruct(ret2.getE1().asClassInstance(IntStruct.class)); 
				Tuple2<IntStruct, IntObjectStruct> ret3 = UObject.transformObjectToObject(ret2, new TypeReference<Tuple2<IntStruct, IntObjectStruct>>() {});
				checkIntStruct(ret3.getE1());
				checkIntObjectStruct(ret3.getE2());
				checkIntStruct(ret2.getE1().asClassInstance(IntStruct.class));
				checkJsonTreeForIntStruct(ret2.getE1().asJsonNode());
				// Transform into json tree test
			    JsonNode tree = new UObject(ret2).asJsonNode();
			    Assert.assertTrue(tree.isArray());
			    Assert.assertEquals(2, tree.size());
			    checkJsonTreeForObjectStruct(tree.get(1));
			}
		}
	}

	
    public Tuple2<UObject, ObjectStruct> objectCheck(UObject simple, ObjectStruct complex) throws IOException {
        List<Object> args = new ArrayList<Object>();
        args.add(simple);
        args.add(complex);
        TypeReference<Tuple2<UObject, ObjectStruct>> retType = new TypeReference<Tuple2<UObject, ObjectStruct>>() {};
        String json = UObject.transformObjectToString(args);
        Tuple2<UObject, ObjectStruct> res = UObject.transformStringToObject(json, retType);
        return res;
    }
    
	public void checkJsonTreeForIntStruct(JsonNode node) {
		Assert.assertEquals(0, node.get("val1").intValue());
		Assert.assertEquals(3, node.get("val2").size());
		Assert.assertEquals(1, node.get("val2").get(0).intValue());
		Assert.assertEquals(2, node.get("val3").size());
		Assert.assertEquals(0, node.get("val3").get("0").intValue());
		Assert.assertEquals(1, node.get("val4").get(0).intValue());
	}

	public void checkJsonTreeForObjectStruct(JsonNode node) {
		Assert.assertEquals(0, node.get("val1").get("val1").intValue());
		Assert.assertEquals(3, node.get("val2").get(0).get("val2").size());
		Assert.assertEquals(1, node.get("val2").get(0).get("val2").get(0).intValue());
		Assert.assertEquals(2, node.get("val3").get("key").get("val3").size());
		Assert.assertEquals(0, node.get("val3").get("key").get("val3").get("0").intValue());
		Assert.assertEquals(1, node.get("val4").get(0).get("val4").get(0).intValue());
	}

	public void checkIntObjectStruct(IntObjectStruct ios) {
		checkIntStruct(ios.getVal1());
		checkIntStruct(ios.getVal2().get(0));
		checkIntStruct(ios.getVal3().get("key"));
		checkIntStruct(ios.getVal4().getE1());
	}
	
	private static void checkIntStruct(IntStruct e2) {
		assertEquals(0, e2.getVal1());
		Assert.assertEquals(3, e2.getVal2().size());
		assertEquals(1, e2.getVal2().get(0));
		Assert.assertEquals(2, e2.getVal3().size());
		assertEquals(0, e2.getVal3().get("0"));
		assertEquals(1, e2.getVal4().getE1());
	}

	private static void assertEquals(int expected, Object actual) {
		if (actual instanceof Integer) {
			Assert.assertEquals(expected, (int)(Integer)actual);
		} else {
			Assert.assertEquals((long)expected, (long)(Long)actual);
		}
	}
	
	private static void checkObject(Object expected, UObject actual) throws JsonProcessingException {
		if (expected.getClass().getName().startsWith("java.lang.")) {
			Assert.assertEquals(expected, actual.asScalar());
		} else {
			Assert.assertTrue(actual.isMap());
			Map<String, UObject> map0 = actual.asMap();
			Assert.assertEquals(0, (int) map0.get("val1").asScalar());
			Assert.assertTrue(map0.get("val2").isList());
			List<Long> list1 = map0.get("val2").asInstance();
			Assert.assertEquals(3, list1.size());
			assertEquals(1, list1.get(0));
			Assert.assertTrue(map0.get("val3").isMap());
			Map<String, Long> map1 = map0.get("val3").asInstance();
			Assert.assertEquals(2, map1.size());
			assertEquals(0, map1.get("0"));
			Assert.assertTrue(map0.get("val4").isList());
			List<Long> list2 = map0.get("val4").asInstance();
			Assert.assertEquals(2, list2.size());
			assertEquals(1, list2.get(0));
		}
	}
		
	public static class IntObjectStruct {

	    @JsonProperty("val1")
	    private IntStruct val1;
	    @JsonProperty("val2")
	    private List<IntStruct> val2 = new ArrayList<IntStruct>();
	    @JsonProperty("val3")
	    private Map<String, IntStruct> val3;
	    @JsonProperty("val4")
	    private Tuple2 <IntStruct, IntStruct> val4;
	    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	    @JsonProperty("val1")
	    public IntStruct getVal1() {
	        return val1;
	    }

	    @JsonProperty("val1")
	    public void setVal1(IntStruct val1) {
	        this.val1 = val1;
	    }

	    public IntObjectStruct withVal1(IntStruct val1) {
	        this.val1 = val1;
	        return this;
	    }

	    @JsonProperty("val2")
	    public List<IntStruct> getVal2() {
	        return val2;
	    }

	    @JsonProperty("val2")
	    public void setVal2(List<IntStruct> val2) {
	        this.val2 = val2;
	    }

	    public IntObjectStruct withVal2(List<IntStruct> val2) {
	        this.val2 = val2;
	        return this;
	    }

	    @JsonProperty("val3")
	    public Map<String, IntStruct> getVal3() {
	        return val3;
	    }

	    @JsonProperty("val3")
	    public void setVal3(Map<String, IntStruct> val3) {
	        this.val3 = val3;
	    }

	    public IntObjectStruct withVal3(Map<String, IntStruct> val3) {
	        this.val3 = val3;
	        return this;
	    }

	    @JsonProperty("val4")
	    public Tuple2<IntStruct, IntStruct> getVal4() {
	        return val4;
	    }

	    @JsonProperty("val4")
	    public void setVal4(Tuple2<IntStruct, IntStruct> val4) {
	        this.val4 = val4;
	    }

	    public IntObjectStruct withVal4(Tuple2<IntStruct, IntStruct> val4) {
	        this.val4 = val4;
	        return this;
	    }

	    @JsonAnyGetter
	    public Map<java.lang.String, Object> getAdditionalProperties() {
	        return this.additionalProperties;
	    }

	    @JsonAnySetter
	    public void setAdditionalProperties(java.lang.String name, Object value) {
	        this.additionalProperties.put(name, value);
	    }

	}

}
