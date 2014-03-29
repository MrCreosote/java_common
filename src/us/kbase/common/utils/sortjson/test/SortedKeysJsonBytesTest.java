package us.kbase.common.utils.sortjson.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.common.utils.sortjson.KeyDuplicationException;
import us.kbase.common.utils.sortjson.SortedKeysJsonBytes;
import us.kbase.common.utils.sortjson.SortedKeysJsonFile;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SortedKeysJsonBytesTest {
	
	@Test
	public void testArrayWithMap() {
		assertSort(
				"[1, 2.0, \"4{\\\"\", {\"kkk\":\"vvv\",\n\"aaa\":\"bbb\"} , \"}3\\\\\", true]", 
				"[1,2.0,\"4{\\\"\",{\"aaa\":\"bbb\",\"kkk\":\"vvv\"},\"}3\\\\\",true]");
	}

	@Test
	public void testMapWithMaps() {
		assertSort(
				"{\"kkk\":[1,{\"k2\":\"vvv\",\"k1\":\"v1\"},null], \"aaa\":{\"bbb\":{}}}", 
				"{\"aaa\":{\"bbb\":{}},\"kkk\":[1,{\"k1\":\"v1\",\"k2\":\"vvv\"},null]}");
	}

	@Test
	public void testDoubleKeys() {
		assertSort("{\"kkk\":1, \"kkk\":2}", "{\"kkk\":1}");
	}

	@Test
	public void testDoubleKeysError() throws Exception {
		try {
			sort("{\"ro/ot\":[0,{\"kkk\":1, \"kkk\":2}]}", false);
			Assert.fail("Should be exception");
		} catch (KeyDuplicationException e) {
			Assert.assertEquals("Duplicated key 'kkk' was found at /ro\\/ot/1", e.getMessage());
		}
	}

	private static void assertSort(String before, String after) {
		try {
			String actual = sort(before, true);
			Assert.assertEquals(after, actual);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private static String sort(String json, boolean skipDoubleKeys) throws Exception {
		Charset ch = Charset.forName("UTF-8");
		byte[] data = json.getBytes(ch);
		return new String(new SortedKeysJsonBytes(data).getSorted(), ch);
	}
	
	private static void testSmallMap() throws Exception {
		System.out.println("Small map test (buffer=10k):");
		System.out.println("map_size, file_size, time_ms");
		Random rnd = new Random(1234567890L);
		int size = 600000;
		byte[] data = writeRandomMapIntoByteArray(rnd, size);
		long time = System.currentTimeMillis();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new SortedKeysJsonFile(data).setMaxBufferSize(10 * 1024).setSkipKeyDuplication(true).writeIntoStream(os);
		os.close();
		System.out.println(size + ", " + data.length + ", " + (System.currentTimeMillis() - time));
	}
	
	private static void testLargeMap() throws Exception {
		System.out.println("Large map test (buffer=10k):");
		System.out.println("map_size, file_size, time_ms");
		int baseSize = 1000000;
		Random rnd = new Random(1234567890L);
		int[] sizes = {baseSize, 2 * baseSize, 5 * baseSize, 10 * baseSize};
		File dir = new File("temp_files");
		boolean deleteDir = false;
		if (!dir.exists()) {
			dir.mkdir();
			deleteDir = true;
		}
		for (int size : sizes) {
			File f = new File(dir, "temp_large_map.json");
			writeRandomMapIntoFile(rnd, size, f);
			File f2 = new File(dir, "temp_large_map2.json");
			long time = System.currentTimeMillis();
			OutputStream os = new FileOutputStream(f2);
			new SortedKeysJsonFile(f).setMaxBufferSize(10 * 1024).setSkipKeyDuplication(true).writeIntoStream(os);
			os.close();
			System.out.println(size + ", " + f.length() + ", " + (System.currentTimeMillis() - time));
			f.delete();
			f2.delete();
		}
		if (deleteDir)
			dir.delete();
	}

	private static void testLargeMapBuffer() throws Exception {
		System.out.println("Large map buffer test (map=1M):");
		System.out.println("buffer_size, time_ms");
		int size = 1000000;
		Random rnd = new Random(1234567890L);
		File dir = new File("temp_files");
		boolean deleteDir = false;
		if (!dir.exists()) {
			dir.mkdir();
			deleteDir = true;
		}
		File f = new File(dir, "temp_large_map.json");
		writeRandomMapIntoFile(rnd, size, f);
		int bufSize = 1024;
		for (int n = 0; n <= 10; n++) {
			File f2 = new File(dir, "temp_large_map2.json");
			long time = System.currentTimeMillis();
			OutputStream os = new FileOutputStream(f2);
			new SortedKeysJsonFile(f).setMaxBufferSize(bufSize).setSkipKeyDuplication(true).writeIntoStream(os);
			os.close();
			System.out.println(bufSize + ", " + (System.currentTimeMillis() - time));
			f2.delete();
			bufSize *= 2;
		}
		f.delete();
		if (deleteDir)
			dir.delete();
	}

	private static void testLargeList() throws Exception {
		System.out.println("Large list test (buffer=10k)");
		System.out.println("list_size, file_size, time_ms");
		int baseSize = 1000000;
		Random rnd = new Random(1234567890L);
		int[] sizes = {baseSize, 2 * baseSize, 5 * baseSize, 10 * baseSize};
		File dir = new File("temp_files");
		boolean deleteDir = false;
		if (!dir.exists()) {
			dir.mkdir();
			deleteDir = true;
		}
		for (int size : sizes) {
			File f = new File(dir, "temp_large_list.json");
			writeRandomListIntoFile(rnd, size, f);
			File f2 = new File(dir, "temp_large_list2.json");
			long time = System.currentTimeMillis();
			OutputStream os = new FileOutputStream(f2);
			new SortedKeysJsonFile(f).setMaxBufferSize(10 * 1024).setSkipKeyDuplication(true).writeIntoStream(os);
			os.close();
			System.out.println(size + ", " + f.length() + ", " + (System.currentTimeMillis() - time));
			f.delete();
			f2.delete();
		}
		if (deleteDir)
			dir.delete();
	}

	private static void testLargeListBuffer() throws Exception {
		System.out.println("Large list buffer test (map=1M):");
		System.out.println("buffer_size, time_ms");
		int size = 1000000;
		Random rnd = new Random(1234567890L);
		File dir = new File("temp_files");
		boolean deleteDir = false;
		if (!dir.exists()) {
			dir.mkdir();
			deleteDir = true;
		}
		File f = new File(dir, "temp_large_list.json");
		writeRandomListIntoFile(rnd, size, f);
		int bufSize = 1024;
		for (int n = 0; n <= 10; n++) {
			File f2 = new File(dir, "temp_large_list2.json");
			long time = System.currentTimeMillis();
			OutputStream os = new FileOutputStream(f2);
			new SortedKeysJsonFile(f).setMaxBufferSize(bufSize).setSkipKeyDuplication(true).writeIntoStream(os);
			os.close();
			System.out.println(bufSize + ", " + (System.currentTimeMillis() - time));
			f2.delete();
			bufSize *= 2;
		}
		f.delete();
		if (deleteDir)
			dir.delete();
	}

	private static void writeRandomMapIntoFile(Random rnd, int size, File f)
			throws IOException, JsonGenerationException {
		writeRandomMapIntoFile(rnd, size, 8, new BufferedOutputStream(new FileOutputStream(f)));
	}
	
	private static void writeRandomMapIntoFile(Random rnd, int size, int valueRepeats, OutputStream os)
			throws IOException, JsonGenerationException {
		JsonGenerator jgen = new ObjectMapper().getFactory().createGenerator(os, JsonEncoding.UTF8);
		jgen.writeStartObject();
		for (int i = 0; i < size; i++) {
			int num = rnd.nextInt(size);
			jgen.writeFieldName("key" + num);
			String value = "";
			for (int j = 0; j < valueRepeats; j++)
				value += "value" + num;
			jgen.writeString(value);
		}
		jgen.writeEndObject();
		jgen.close();
	}

	private static byte[] writeRandomMapIntoByteArray(Random rnd, int size)
			throws IOException, JsonGenerationException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeRandomMapIntoFile(rnd, size, 1, os);
		return os.toByteArray();
	}

	private static void writeRandomListIntoFile(Random rnd, int size, File f)
			throws IOException, JsonGenerationException {
		JsonGenerator jgen = new ObjectMapper().getFactory().createGenerator(f, JsonEncoding.UTF8);
		jgen.writeStartArray();
		for (int i = 0; i < size; i++) {
			jgen.writeStartObject();
			int num = rnd.nextInt(size);
			jgen.writeFieldName("key" + num);
			String value = "";
			for (int j = 0; j < 10; j++)
				value += "value" + num;
			jgen.writeString(value);
			jgen.writeFieldName("a");
			jgen.writeString("b");
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
		jgen.close();
	}
}