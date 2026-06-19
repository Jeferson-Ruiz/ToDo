package com.jr.todo.util;

public class TextFormat {

  public static String nameFormat(String name) {
    String editName = name.strip();
    StringBuilder newName = new StringBuilder();
    newName.append(editName.substring(0, 1).toUpperCase());
    newName.append(editName.substring(1).toLowerCase());
    return newName.toString();
  }

  public static String validaTextNull(String text) {
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("Ingresar texto");
    }
    String newText = text.strip().toLowerCase();
    return newText;
  }
}
