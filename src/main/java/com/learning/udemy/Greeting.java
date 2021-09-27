package com.learning.udemy;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Greeting {

    final String myText = "Hello World";
    final BigDecimal id = BigDecimal.ONE;
    final Instant timeUTC = Instant.now();
}
