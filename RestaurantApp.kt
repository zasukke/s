import kotlinx.datetime.*
import kotlinx.datetime.DayOfWeek
import kotlin.math.roundToInt

fun main() {
    RestaurantSystem().start()
}

// Перечисление типов блюд
enum class DishType { MEAT, FISH, OTHER }

// Перечисление городов
enum class City { MOSCOW, ST_PETERSBURG }

// Класс блюда
data class Dish(
    val name: String,
    val isVegetarian: Boolean,
    val calories: Int,
    val type: DishType,
    var basePrice: Double,
    val isChefSpecial: Boolean = false,
    val isLunchMenu: Boolean = false,
    val isBreakfastMenu: Boolean = false
)

// Класс напитка
data class Drink(
    val name: String,
    val calories: Int,
    val isAlcoholic: Boolean,
    var basePrice: Double
)

// Класс ресторана
class Restaurant(
    val city: City,
    val name: String,
    private val chefSpecial: Dish
) {
    private val menu = mutableListOf<Dish>()
    private val drinks = mutableListOf<Drink>()

    fun addDish(dish: Dish) = menu.add(dish)
    fun addDrink(drink: Drink) = drinks.add(drink)

    private fun calculatePrice(price: Double): Double {
        return if (city == City.MOSCOW) price * 1.10 else price
    }

    private fun hasFoodDiscount(currentTime: LocalTime, dayOfWeek: DayOfWeek): Boolean {
        return dayOfWeek != DayOfWeek.SATURDAY &&
                dayOfWeek != DayOfWeek.SUNDAY &&
                currentTime.hour in 15..17
    }

    fun showMenu(currentTime: LocalTime, dayOfWeek: DayOfWeek) {
        println("\n=== Меню ресторана '$name' ($city) ===")

        // Фильтрация блюд по времени
        val availableDishes = menu.filter { dish ->
            when {
                dish.isBreakfastMenu -> currentTime.hour in 7..10
                dish.isLunchMenu -> currentTime.hour in 12..14
                else -> true
            }
        } + chefSpecial

        val hasDiscount = hasFoodDiscount(currentTime, dayOfWeek)

        println("\nБлюда:")
        availableDishes.forEach { dish ->
            var price = calculatePrice(dish.basePrice)
            if (hasDiscount && !dish.isChefSpecial) {
                price *= 0.8
                println("  ${dish.name} - ${price.roundToInt()} руб. (скидка 20%)")
            } else {
                println("  ${dish.name} - ${price.roundToInt()} руб.")
            }
            println("    Тип: ${dish.type}, ${if (dish.isVegetarian) "Вегетарианское" else "Не вегетарианское"}")
            println("    Калории: ${dish.calories} ккал")
            if (dish.isChefSpecial) println("    ★ Фирменное блюдо шефа!")
        }

        println("\nНапитки:")
        drinks.forEach { drink ->
            val price = calculatePrice(drink.basePrice).roundToInt()
            println("  ${drink.name} - $price руб.")
            println("    ${if (drink.isAlcoholic) "Алкогольный" else "Безалкогольный"}, ${drink.calories} ккал")
        }
    }

    fun makeOrder(currentTime: LocalTime, dayOfWeek: DayOfWeek): Order {
        val order = Order()
        val availableDishes = menu.filter { dish ->
            when {
                dish.isBreakfastMenu -> currentTime.hour in 7..10
                dish.isLunchMenu -> currentTime.hour in 12..14
                else -> true
            }
        } + chefSpecial

        val hasDiscount = hasFoodDiscount(currentTime, dayOfWeek)

        while (true) {
            println("\n1. Добавить блюдо")
            println("2. Добавить напиток")
            println("3. Завершить заказ")
            print("Выберите действие: ")

            when (readlnOrNull()) {
                "1" -> {
                    println("\nДоступные блюда:")
                    availableDishes.forEachIndexed { index, dish ->
                        var price = calculatePrice(dish.basePrice)
                        if (hasDiscount && !dish.isChefSpecial) price *= 0.8
                        println("${index + 1}. ${dish.name} - ${price.roundToInt()} руб.")
                    }
                    print("Выберите блюдо (0 - назад): ")
                    readlnOrNull()?.toIntOrNull()?.let { choice ->
                        if (choice in 1..availableDishes.size) {
                            val dish = availableDishes[choice - 1]
                            var price = calculatePrice(dish.basePrice)
                            if (hasDiscount && !dish.isChefSpecial) price *= 0.8
                            order.addItem(dish, price)
                            println("Добавлено: ${dish.name}")
                        }
                    }
                }
                "2" -> {
                    println("\nДоступные напитки:")
                    drinks.forEachIndexed { index, drink ->
                        val price = calculatePrice(drink.basePrice).roundToInt()
                        println("${index + 1}. ${drink.name} - $price руб.")
                    }
                    print("Выберите напиток (0 - назад): ")
                    readlnOrNull()?.toIntOrNull()?.let { choice ->
                        if (choice in 1..drinks.size) {
                            val drink = drinks[choice - 1]
                            val price = calculatePrice(drink.basePrice)
                            order.addItem(drink, price)
                            println("Добавлено: ${drink.name}")
                        }
                    }
                }
                "3" -> return order
                else -> println("Неверный ввод")
            }
        }
    }
}

// Класс заказа
class Order {
    private val items = mutableListOf<Pair<Any, Double>>()

    fun addItem(item: Any, price: Double) {
        items.add(item to price)
    }

    fun printReceipt() {
        println("\n=== Ваш заказ ===")
        items.forEach { (item, price) ->
            when (item) {
                is Dish -> println("${item.name}: ${price.roundToInt()} руб.")
                is Drink -> println("${item.name}: ${price.roundToInt()} руб.")
            }
        }
        println("=================")
        println("Итого: ${items.sumOf { it.second }.roundToInt()} руб.")
        println("=================")
    }
}

// Главная система
class RestaurantSystem {
    private val restaurants = mutableListOf<Restaurant>()

    init {
        // Инициализация ресторанов Москвы
        val moscow1 = Restaurant(City.MOSCOW, "Москва Центр",
            Dish("Стейк от шефа", false, 550, DishType.MEAT, 1200.0, true))
        setupMenu(moscow1)
        restaurants.add(moscow1)

        val moscow2 = Restaurant(City.MOSCOW, "Москва Север",
            Dish("Утка по-пекински", false, 600, DishType.MEAT, 1500.0, true))
        setupMenu(moscow2)
        restaurants.add(moscow2)

        // Инициализация ресторанов Петербурга
        val petersburg1 = Restaurant(City.ST_PETERSBURG, "Петербург Центр",
            Dish("Борщ от шефа", true, 350, DishType.OTHER, 800.0, true))
        setupMenu(petersburg1)
        restaurants.add(petersburg1)

        val petersburg2 = Restaurant(City.ST_PETERSBURG, "Петербург Невский",
            Dish("Бефстроганов", false, 500, DishType.MEAT, 900.0, true))
        setupMenu(petersburg2)
        restaurants.add(petersburg2)
    }

    private fun setupMenu(restaurant: Restaurant) {
        // Общие блюда
        restaurant.addDish(Dish("Салат Цезарь", false, 300, DishType.OTHER, 500.0))
        restaurant.addDish(Dish("Греческий салат", true, 250, DishType.OTHER, 450.0))
        restaurant.addDish(Dish("Бизнес ланч", false, 400, DishType.MEAT, 600.0, isLunchMenu = true))
        restaurant.addDish(Dish("Омлет", true, 350, DishType.OTHER, 400.0, isBreakfastMenu = true))

        // Напитки
        restaurant.addDrink(Drink("Кола", 150, false, 150.0))
        restaurant.addDrink(Drink("Пиво", 200, true, 250.0))
        restaurant.addDrink(Drink("Сок", 120, false, 180.0))
    }

    fun start() {
        println("Добро пожаловать в систему заказов ресторанов!")

        while (true) {
            println("\nВыберите ресторан:")
            restaurants.forEachIndexed { index, restaurant ->
                println("${index + 1}. ${restaurant.name} (${restaurant.city})")
            }
            println("0. Выход")

            print("Ваш выбор: ")
            when (val choice = readlnOrNull()?.toIntOrNull()) {
                0 -> return
                in 1..restaurants.size -> {
                    val restaurant = restaurants[choice!! - 1]
                    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
                    val dayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

                    println("\nТекущее время: ${currentTime.toString().substring(0, 5)}")
                    restaurant.showMenu(currentTime, dayOfWeek)

                    val order = restaurant.makeOrder(currentTime, dayOfWeek)
                    order.printReceipt()
                }
                else -> println("Неверный ввод, попробуйте снова")
            }
        }
    }
}

https://chromewebstore.google.com/detail/%D0%B1%D0%B5%D1%81%D0%BF%D0%BB%D0%B0%D1%82%D0%BD%D1%8B%D0%B9-vpn-%D0%BF%D1%80%D0%BE%D0%BA%D1%81%D0%B8-%D0%B8-%D0%B1/hipncndjamdcmphkgngojegjblibadbe?hl=ru
1. Переменные и типы переменных
Переменная - именованная область памяти для хранения данных.

Типы переменных в Java:

Примитивные (хранят значения)

Ссылочные (хранят ссылки на объекты)

2. Компилятор и байт-код
Java-компилятор (javac) - преобразует исходный код (.java) в байт-код (.class).

Байт-код - промежуточное представление программы, которое выполняется JVM.

3. JVM, JRE, JDK
JVM (Java Virtual Machine) - выполняет байт-код

JRE (Java Runtime Environment) - среда выполнения (JVM + библиотеки)

JDK (Java Development Kit) - набор для разработки (JRE + компилятор + инструменты)

4. Целые и вещественные числа
Целые типы:

byte (8 бит)

short (16 бит)

int (32 бит)

long (64 бит)

Вещественные типы:

float (32 бит)

double (64 бит)

5. Логический тип Boolean
Может принимать значения true или false.

6. Логические операции
&& (И)

|| (ИЛИ)

! (НЕ)

^ (исключающее ИЛИ)

7. Сравнение ссылок
Оператор == сравнивает ссылки на объекты. Для сравнения содержимого объектов используется метод equals().

8. Класс String и работа со строками
String - неизменяемый класс для работы со строками. Основные методы:

length()

charAt()

substring()

indexOf()

equals()

concat()

toLowerCase()/toUpperCase()

9. Условный оператор
java
Copy
if (условие) {
    // код
} else if (другое условие) {
    // код
} else {
    // код
}
10. Switch
java
Copy
switch (переменная) {
    case значение1:
    // код
    break;
    case значение2:
    // код
    break;
    default:
    // код
}
11. Циклы
while:

java
Copy
while (условие) {
    // код
}
do-while:

java
Copy
do {
    // код
} while (условие);
for:

java
Copy
for (инициализация; условие; итерация) {
    // код
}
Прерывание цикла:
break - выход из цикла
continue - переход к следующей итерации
12. Массивы и класс Arrays
Массив - фиксированная структура данных:
java
Copy
int[] arr = new int[10];
Класс Arrays предоставляет утилитные методы:
sort()
binarySearch()
fill()
equals()
13. Методы
Синтаксис метода:
java
Copy
модификаторы тип_возвращаемого_значения имя(параметры) {
    // тело метода
    return значение;
}
Параметры передаются по значению (для примитивов) или по ссылке (для объектов).
14. Видимость переменных
Локальные - видны только в блоке кода
Поля класса - видны в классе (и наследниках, если не private)
15. Примитивные типы
byte, short, int, long, float, double, char, boolean
16. Приведение типов
Неявное (расширение)
Явное (сужение с указанием типа)
17. StringBuilder
Изменяемый аналог String для эффективной конкатенации строк.
Java Core
1. Объекты и классы
Класс - шаблон для создания объектов.
Объект - экземпляр класса.
2. Конструкторы
Специальный метод для инициализации объекта:
java
Copy
public class MyClass {
    public MyClass() { // конструктор
        // инициализация
    }
}
3. ООП
Основные концепции:
Абстракция
Инкапсуляция
Наследование
Полиморфизм
4. Принципы ООП
Инкапсуляция - скрытие деталей реализации
Полиморфизм - возможность объектов с одинаковой спецификацией иметь разную реализацию
Наследование - создание новых классов на основе существующих
5. Перегрузка методов
Создание методов с одинаковым именем, но разными параметрами.
6. Абстрактные классы
Классы, которые нельзя инстанцировать, могут содержать абстрактные методы.
7. Интерфейсы
Контракт, который классы должны реализовать. С Java 8 могут содержать методы по умолчанию.
8. Сравнение объектов
== - сравнение ссылок
equals() - сравнение содержимого
compareTo() - для упорядочивания
9. Модификаторы доступа
private - только внутри класса
default (package-private) - внутри пакета
protected - внутри пакета + наследники
public - везде
10. Статические элементы
Принадлежат классу, а не объекту:
статические переменные
статические методы
статические классы
11. Generic типы
Позволяют создавать типобезопасные коллекции:
java
Copy
List<String> list = new ArrayList<>();
Обертки для примитивов:
Integer, Double, Boolean и т.д.
12. Коллекции
ArrayList - динамический массив
LinkedList - двусвязный список
HashSet - множество
HashMap - ассоциативный массив
13. Enum
Перечисление фиксированных значений:
java
Copy
enum Season { WINTER, SPRING, SUMMER, AUTUMN }
14. Исключения
Иерархия:
Throwable
Error (непроверяемые)
Exception
RuntimeException (непроверяемые)
Другие Exception (проверяемые)
Обработка:

java
Copy
try {
    // код
} catch (Exception e) {
    // обработка
} finally {
    // код
}
Try-with-resources:

java
Copy
try (Resource res = new Resource()) {
    // работа с ресурсом
}
15. Лямбда-выражения
Анонимные функции:

java
Copy
(параметры) -> { тело }
Функциональный интерфейс - интерфейс с одним абстрактным методом.