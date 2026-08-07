package com.jr.todo.utils;

import org.junit.jupiter.api.Test;
import com.jr.todo.util.TextFormat;
import static org.junit.jupiter.api.Assertions.*;

class TextFormatTest {

	@Test
	void testNameFormat() {
		String name = "pedro pascal";
		String result = TextFormat.nameFormat(name);
		assertEquals("Pedro pascal", result);
		assertNotNull(result);
	}

	@Test
	void testValidaTextNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			TextFormat.validaTextNull(null);
		});
	}

	@Test
	void testValidaTextBlank() {
		String text = " ";
		assertThrows(IllegalArgumentException.class, () -> {
			TextFormat.validaTextNull(text);
		});
	}

	@Test
	void testNameFormatWithText() {
		String text = "Texto";
		String restulr = TextFormat.validaTextNull(text);

		assertNotNull(restulr);
		assertEquals("texto", restulr);
	}
}