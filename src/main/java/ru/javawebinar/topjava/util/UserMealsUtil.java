package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

public class UserMealsUtil {

    static Map<LocalDateTime, Integer> caloriesPerMealsMap = new HashMap<>();

    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );

        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);



    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMealWithExceed> res = new ArrayList<>();

                                                   mealList
                                                           .stream()
                                                           .forEach((meal) -> caloriesPerMealsMap.put(meal.getDateTime(), meal.getCalories()));

        Map<LocalDateTime, UserMeal> filteredMap = mealList
                                                           .stream()
                                                           .filter(meal -> isBetween(meal.getDateTime(), startTime, endTime))
                                                           .collect(Collectors.toMap(UserMeal::getDateTime, UserMeal::clone));

                                                filteredMap
                                                           .forEach((localDate, userMeal)->
                                                                                               {
                                                                                                    boolean currentExceeded = false;
                                                                                                    if(caloriesPerDate(localDate) > caloriesPerDay) {
                                                                                                        currentExceeded = true;
                                                                                                    }
                                                                                                    res.add(createWithExceed(userMeal, currentExceeded));
                                                                                               });

        if (res.isEmpty()) { return Collections.emptyList();}
        return res;
    }

    private static List<UserMealWithExceed> getFilteredWithExceeded(Collection<UserMeal> meals, int caloriesPerDay, Predicate<UserMeal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))
                );

        return meals.stream()
                .filter(filter)
                .map(meal -> createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(toList());
    }


    //
    private static UserMealWithExceed createWithExceed(UserMeal meal, boolean exceeded) {
        return new UserMealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded);
    }

    public static boolean isBetween(LocalDateTime localDateTime, LocalTime startTime, LocalTime endTime) {
        LocalTime lt = localDateTime.toLocalTime();
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) <= 0;
    }

    public static int caloriesPerDate(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        int res = caloriesPerMealsMap.entrySet().stream()
                  .filter(currentMap -> !(currentMap.getKey().toLocalDate().equals(date)))
                  .mapToInt(currentMap -> currentMap.getValue()).sum();
        return res;
    }
}
