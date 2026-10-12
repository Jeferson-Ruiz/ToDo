package com.jr.todo.util;

import java.time.LocalDate;

public class DateValidationsHelper {

    public static void dateValidation(LocalDate dateInit, LocalDate dateFinish) {
        if (dateInit == null)
            throw new IllegalArgumentException("Fecha de inicio obligatoria");
        if (dateInit.isAfter(dateFinish))
            throw new IllegalArgumentException("Rango invalido");
        if (dateFinish == null) {
            dateFinish = dateInit;
        }
    }
}