package com.jr.todo.util;

public class NameFormat {

  public static String format(String name) {
    String editName = name.strip();
    StringBuilder newName = new StringBuilder();
    newName.append(editName.substring(0, 1).toUpperCase());
    newName.append(editName.substring(1).toLowerCase());
    return newName.toString();
  }
}
