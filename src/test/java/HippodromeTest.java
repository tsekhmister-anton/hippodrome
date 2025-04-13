import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class HippodromeTest {
    @Test
    @DisplayName("Проверяем, что при имени = null выбрасывается исключение")
    void testWhenNameIsNullThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse(null, 10.0, 100.0)
        );
        assertEquals("Name cannot be null.", exception.getMessage());
    }


    @ParameterizedTest
    @DisplayName("Имя лошади не может быть пустым или содержать пробелы")
    @ValueSource(strings = {"", " ", "\t", "\n", " \t\n"})
    void testWhenNameIsBlankThrowsException(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse(name, 10.0, 100.0)
        );
        assertEquals("Name cannot be blank.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверяем, что скорость лошади не может быть отрицательной")
    void testWhenSpeedIsNegativeThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse("Звёздочка", -1.0, 100.0)
        );
        assertEquals("Speed cannot be negative.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверяем, что дистанция лошади не может быть отрицательной")
    void testWhenDistanceIsNegativeThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse("Звёздочка", 10.0, -1.0)
        );
        assertEquals("Distance cannot be negative.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверяем, что полученное имя лошади совпадает с тем, что передали в конструктор")
    void testGetNameReturnsNamePassedToConstructor() {
        String expectedName = "Звёздочка";
        Horse horse = new Horse(expectedName, 10.0, 100.0);
        String actualName = horse.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    @DisplayName("Проверяем, что полученная скорость лошади совпадает с тем, что передали в конструктор")
    void testGetSpeedReturnSpeedPassesToConstructor() {
        double expectedSpeed = 10.0;
        Horse horse = new Horse("Звёздочка", expectedSpeed, 100.0);
        double actualSpeed = horse.getSpeed();
        assertEquals(expectedSpeed, actualSpeed);
    }

    @Test
    @DisplayName("Проверяем, что полученная дистанция пройденая лошадью совпадает с тем, что передали в конструктор")
    void testGetDistanceReturnDistancePassedToConstructor() {
        double expectedDistance = 100.0;
        Horse horse = new Horse("Звёздочка", 10.0, expectedDistance);
        double actualDistance = horse.getDistance();
        assertEquals(expectedDistance, actualDistance);
    }

    @Test
    @DisplayName("Проверяем, полученная дистанция пройденая лошадь равна 0, если объект создан через конструктор с двумя параметрами")
    void testGetDistanceReturnZeroWhenCreatedWithTwoArgConstructor() {
        Horse horse = new Horse("Звёздочка", 10.0);
        double actualDistance = horse.getDistance();
        assertEquals(0.0, actualDistance);
    }

    @Test
    @DisplayName("Проверяем что, метод move вызывает getRandomDouble с параметрами (0.2,0.9)")
    void testShouldCallGetRandomDoubleWithCorrectParameters() {
        try (MockedStatic<Horse> mockedStatic = Mockito.mockStatic(Horse.class)) {
            Horse horse = new Horse("Звёздочка", 10.0);
            horse.move();
            mockedStatic.verify(
                    () -> Horse.getRandomDouble(0.2, 0.9)
            );
        }
    }

    @ParameterizedTest
    @DisplayName("Проверяем что метод move корректно вычисляет новую дистанцию")
    @CsvSource({
            "5.0, 0.2, 0.0, 1.0",
            "10.0, 0.9, 0.0, 9.0",
            "2.5, 0.5, 100.0, 101.25"
    })
    void testShouldUpdateDistanceCorrectly(double speed, double mockRandomValue, double initialDistance, double expectedDistance) {
        try (MockedStatic<Horse> mockedStatic = Mockito.mockStatic(Horse.class)) {
            mockedStatic.when(() -> Horse.getRandomDouble(0.2, 0.9))
                    .thenReturn(mockRandomValue);
            Horse horse = new Horse("Звёздочка", speed, initialDistance);
            horse.move();

            assertEquals(expectedDistance, horse.getDistance(), 0.001);
        }
    }

    @Test
    @DisplayName("Проверяем, что конструктор бросает исключения при передаче null")
    void testWhenNullPassedThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Hippodrome(null)
        );
        assertEquals("Horses cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверяем, что конструктор бросает исключение при передаче пустого списка")
    void testWhenEmptyListPassedThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Hippodrome(List.of())
        );
        assertEquals("Horses cannot be empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверяем, что метод getHorses возвращает список тех же лошадей в том же порядке")
    void testReturnSameHorsesInSameOrder() {
        List<Horse> expectedHorses = IntStream.range(0, 30)
                .mapToObj(i -> new Horse("Horse № " + i, 1.0 + i, 10.0 + i))
                .collect(Collectors.toList());

        Hippodrome hippodrome = new Hippodrome(expectedHorses);
        List<Horse> actualHorses = hippodrome.getHorses();

        /*assertEquals(expectedHorses.size(), actualHorses.size());
        IntStream.range(0, expectedHorses.size()).forEach(i -> {
            assertEquals(expectedHorses.get(i).getName(), actualHorses.get(i).getName());
            assertEquals(expectedHorses.get(i).getSpeed(), actualHorses.get(i).getSpeed());
            assertEquals(expectedHorses.get(i).getDistance(), actualHorses.get(i).getDistance());
        });*/

        assertIterableEquals(expectedHorses, actualHorses, "Список лошадей должен совпадать с переданным в конструктор");
    }

    @Test
    @DisplayName("Проверяем, что метод move вызывается у всех созданных лошадей. Создаю 50 лошадей.")
    void testShouldCallMoveForAllHorses() {
        List<Horse> mockedHorses = IntStream.range(0, 50)
                .mapToObj(i -> Mockito.mock(Horse.class))
                .collect(Collectors.toList());

        Hippodrome hippodrome = new Hippodrome(mockedHorses);
        hippodrome.move();

        mockedHorses.forEach(horse -> verify(horse).move());
    }

    @Test
    @DisplayName("Проверяем, что метод getWinner возвращает лошадь с максимальным значением Distance")
    void testgetWinnerShouldReturnHorseWithMaxDistance() {
        Horse horse1 = new Horse("Звёздочка", 3.0, 100.0);
        Horse horse2 = new Horse("Ночка", 3.0, 250.0);


        Hippodrome hippodrome = new Hippodrome(List.of(horse1, horse2));

        assertSame(horse2, hippodrome.getWinner());
        /*Horse winner = hippodrome.getWinner();
        assertEquals(horse2, winner);*/
    }

    @Test
    @Disabled
    @Timeout(value = 22)
    @DisplayName("Проверяем, что метод main выполняется не более 22 секунд")
    void testMainMethodShouldCompleteWithin22Seconds() throws Exception {
        Main.main(new String[0]);
    }
}
