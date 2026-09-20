package com.example

import com.example.calculator.CalculatorEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testBasicArithmetic() {
    val res1 = CalculatorEngine.evaluate("2 + 2")
    assertTrue(res1 is CalculatorEngine.EvalResult.Success)
    assertEquals("4", (res1 as CalculatorEngine.EvalResult.Success).formatted)

    val res2 = CalculatorEngine.evaluate("10 − 3 × 2")
    assertTrue(res2 is CalculatorEngine.EvalResult.Success)
    assertEquals("4", (res2 as CalculatorEngine.EvalResult.Success).formatted)

    val res3 = CalculatorEngine.evaluate("15 ÷ 3")
    assertTrue(res3 is CalculatorEngine.EvalResult.Success)
    assertEquals("5", (res3 as CalculatorEngine.EvalResult.Success).formatted)
  }

  @Test
  fun testParenthesesAndPrecedence() {
    val res = CalculatorEngine.evaluate("(10 - 3) × 2")
    assertTrue(res is CalculatorEngine.EvalResult.Success)
    assertEquals("14", (res as CalculatorEngine.EvalResult.Success).formatted)
  }

  @Test
  fun testDecimalsAndPrecision() {
    val res = CalculatorEngine.evaluate("0.1 + 0.2")
    assertTrue(res is CalculatorEngine.EvalResult.Success)
    assertEquals("0.3", (res as CalculatorEngine.EvalResult.Success).formatted)
  }

  @Test
  fun testDivisionByZero() {
    val res = CalculatorEngine.evaluate("5 ÷ 0")
    assertTrue(res is CalculatorEngine.EvalResult.Error)
  }

  @Test
  fun testPercent() {
    val res = CalculatorEngine.evaluate("50%")
    assertTrue(res is CalculatorEngine.EvalResult.Success)
    assertEquals("0.5", (res as CalculatorEngine.EvalResult.Success).formatted)
  }

  @Test
  fun testScientificFunctions() {
    val resSqrt = CalculatorEngine.evaluate("√(16)")
    assertTrue(resSqrt is CalculatorEngine.EvalResult.Success)
    assertEquals("4", (resSqrt as CalculatorEngine.EvalResult.Success).formatted)

    val resSin = CalculatorEngine.evaluate("sin(30)")
    assertTrue(resSin is CalculatorEngine.EvalResult.Success)
    assertEquals("0.5", (resSin as CalculatorEngine.EvalResult.Success).formatted)
  }
}
