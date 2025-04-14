1. Базовый синтаксис

1.1. Переменные и типы данных
Примитивные типы:
int (целые числа), double (дробные), boolean (true/false), char (символ).
Пример:
java
Copy
int age = 25;
double price = 99.99;
boolean isStudent = true;
char grade = 'A';
Ссылочные типы:
String, массивы, объекты.
Пример:
java
Copy
String name = "Alice";
int[] numbers = {1, 2, 3};
Разница между int и Integer:
int – примитив, хранит значение.
Integer – класс-обертка, нужен для коллекций (например, ArrayList<Integer>).

1.2. Управляющие конструкции
Цикл for (вывод чисел от 5 до 1):
java
Copy
for (int i = 5; i >= 1; i--) {
    System.out.println(i);
}
Разница while и do-while:
while – проверяет условие перед выполнением.
do-while – проверяет после (гарантирует хотя бы одну итерацию).
Оператор switch для строк:
java
Copy
String day = "Monday";
switch (day) {
    case "Monday" -> System.out.println("Понедельник");
    case "Tuesday" -> System.out.println("Вторник");
    default -> System.out.println("Другой день");
}

1.3. Строки и массивы
Сравнение строк:
== – сравнивает ссылки (не используй для строк!).
.equals() – сравнивает содержимое.
java
Copy
String s1 = "Java";
String s2 = new String("Java");
System.out.println(s1.equals(s2));  // true
System.out.println(s1 == s2);       // false
Создание массива double:
java
Copy
double[] prices = new double[5];  // Массив из 5 нулей
1.4. Методы
Метод для суммы массива:
java
Copy
public static int sumArray(int[] arr) {
    int sum = 0;
    for (int num : arr) {
        sum += num;
    }
    return sum;
}
Перегрузка методов:
Методы с одинаковым именем, но разными параметрами.
java
Copy
void print(int x) { System.out.println(x); }
void print(String s) { System.out.println(s); }
2. Java Core (ООП и продвинутое)
2.1. Классы и объекты
Конструктор:
Специальный метод для создания объекта.
Если не указан – Java создает конструктор по умолчанию (без параметров).
Как запретить создание объекта:
Сделать конструктор private:
java
Copy
class MyClass {
    private MyClass() {}  // Нельзя создать объект
}


2.2. Принципы ООП
Инкапсуляция:
Скрытие данных (private поля + геттеры/сеттеры).
java
Copy
class Person {
    private int age;
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
Разница extends и implements:
extends – для наследования класса (один родитель).
implements – для реализации интерфейса (можно много).

2.3. Коллекции
Когда использовать ArrayList и LinkedList:
ArrayList – быстрый доступ по индексу (get(0)).
LinkedList – быстрая вставка/удаление (addFirst(), removeLast()).
Проверка ключа в HashMap:
java
Copy
Map<String, Integer> map = new HashMap<>();
map.put("name", 25);
boolean hasKey = map.containsKey("name");  // true

2.4. Исключения
Виды исключений:
Error – критичные ошибки (например, OutOfMemoryError).
Exception – обрабатываемые (например, NullPointerException).
Блок finally:
Выполняется всегда, даже если было исключение.
java
Copy
try {
    int x = 10 / 0;
} catch (Exception e) {
    System.out.println("Ошибка!");
} finally {
    System.out.println("Это выполнится всегда");
}

2.5. Лямбда-выражения
Переписываем Runnable через лямбду:
java
Copy
Runnable r = () -> System.out.println("Hello");
