package com.company;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Reflection API

        // 1. new Person().getClass()
        // 2. Person.class


        // Class
        //  Constructor
        //      [Constructor] Parameter
        //      [action] -> newInstance()
        //      [action] -> newInstance(string, int)
        //  Method
        //      [Method] Parameter
        //      [action] -> invoke() -> call()
        //      [action] -> setAccessible(true)
        //  Field
        //      [action]
        //  Annotation

        Class<CalculatorTest> classType = CalculatorTest.class;
        // 1. all public methods
        // 2. methods void
        // 3. methods without parameters
        // 3. ends with 'Test' in the name

        System.out.println();
        // DECLARED vs non-DECLARED
        // get methods vs get declared methods
        // get methods          -> all PUBLIC + parent's methods (i.e. .toString(), .equals())
        // get DECLARED methods -> methods inside class (private, protected, public)

        Object calculatorTestInstance;
        try {
             calculatorTestInstance = classType.getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        List<Method> testMethods = Arrays.asList(classType.getDeclaredMethods())
                .stream()
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getReturnType() == void.class)
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getName().endsWith("Test"))
                .toList()
                ;

        testMethods.forEach(method -> {

            try {
                method.invoke(calculatorTestInstance);
            } catch (IllegalAccessException e) {
                // 1. Missing setAccessible(true) on non-public
                // 2. Java 16+
                //  - JDK internals
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {

                throw new RuntimeException(e);
            }
        });

    }

    private static void abc(@Annotation1 @Annotation2 String a, @Annotation1 @Annotation3 int b){}

    private static void createAndSetPerson() {
        Person person = null;
        try {
            person = (Person) Person.class.getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException e) {
            // Person(String, int)
            // newInstance(int, String)
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // 1.  no setAccessible(true)
            // 2. Java 16+
            //  - JDK internals

            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // Exception in constructor
            // ctor -> throw new SocketException
            // InvocationTargetException(SocketException)

            // e.getTargetException() -> SocketException
            throw new RuntimeException(e);
        }

        try {
            Field nameField = person.getClass().getDeclaredFields()[0];

            // override 'private'
            nameField.setAccessible(true);
            if (nameField.getType() == String.class) {
                nameField.set(person, "Peter");
            }

            Field ageField = Person.class.getDeclaredFields()[1];
            ageField.setAccessible(true);
            ageField.set(person, 46);
        } catch (IllegalAccessException e) {
            // 1. no setAccessible(true) on non-public field
            // 2. Java 16+
            //      JDK internals
            throw new RuntimeException(e);
        }


        System.out.println(person);
    }
}

@interface Annotation1 {}
@interface Annotation2 {}
@interface Annotation3 {}

class Person {
    private String name;
    private int age;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

class Calculator {
    public int sum(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        return a / b;
    }
}

class CalculatorTest {

    private Calculator calculator = new Calculator();

    public void shouldSumTwoPositiveDigitsTest() {
        int a = 2;
        int b = 2;
        int expected = 4;

        int actual = calculator.sum(a, b);

        if (expected != actual) {
            throw new AssertionError("Result mismatch");
        }
    }

    public void shouldSumTwoNegativeDigitsTest() {
        int a = -2;
        int b = -2;

        int expected = a + b;

        int actual = calculator.sum(a, b);

        if (expected != actual) {
            throw new AssertionError("Result mismatch");
        }
    }

    public void shouldDivideTwoDigitsTest() {
        int a = 4;
        int b = 2;

        int expected = a / b;

        int actual = calculator.divide(a, b);

        if (expected != actual) {
            throw new AssertionError("Result mismatch");
        }
    }

    public void shouldDivideTwoDigitsWhenSecondIsZeroTest() {
        int a = 4;
        int b = 0;

        try {
            calculator.divide(a, b);
        } catch (IllegalArgumentException e) {
            return;
        }

        throw new RuntimeException("Exception expected");
    }
}

