import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.CalcPage;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class IpotekaCalculatorAutoTests {
    @BeforeAll
    static void beforeAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
    // Предусловия:
    // Открыт калькулятор ипотеки: https://calcus.ru/kalkulyator-ipoteki
    // Страница загружена полностью
    @BeforeEach
    void beforeEach() {
        Configuration.pageLoadStrategy = "eager";
        Selenide.open("https://calcus.ru/kalkulyator-ipoteki");
        getWebDriver().manage().window().maximize();

        // Принять использование куки (если кнопка присутствует)
        if ($x("//button[contains(@class, 'js-accept-cookie')]").exists()) {
            $x("//button[contains(@class, 'js-accept-cookie')]").click();
        }
    }

    // Название: Расчёт ипотеки с валидными данными (аннуитетные платежи)
    @Test
    void test01AnnuityPayment() {
        CalcPage calcPage = new CalcPage();

        // Переключиться на режим "По сумме кредита"
        calcPage.switchToSum();

        // Убедиться что форма не содержит данных
        calcPage.isNoDataInFills();

        // Ввести 5 000 000 в поле "Сумма кредита"
        calcPage.setCreditSum("5000000");

        calcPage.setParametersAndCalculate("20", "Y", "8.5");

        // Ожидаемый результат:
        //– Все перечисленные значения появились после нажатия кнопки «Рассчитать»
        //– В блоке результатов отображается Ежемесячный платёж
        $x("//div[contains(@class, 'result-placeholder-monthly_payment')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        sleep(5000);
    }

    // Название: Расчёт ипотеки с дифференцированными платежами
    @Test
    void test02DifferentiatedPayment() {

        // Переключиться на режим "По сумме кредита"
        $x("//a[@data-name='type' and @data-value='2']").click();
        sleep(500); // Даем время на переключение вкладки

        // Убедиться что форма не содержит данных
        $x("//*[@name='credit_sum']").shouldBe(empty);
        $x("//*[@name='period']").shouldBe(empty);
        $x("//*[@name='percent']").shouldBe(empty);

        // Ввести 3 000 000 в поле "Сумма кредита"
        $x("//*[@name='credit_sum']").setValue("3000000");

        // Ввести 15 лет в поле "Срок кредита"
        $x("//*[@name='period']").setValue("15");

        // Выбор единицы измерения срока (годы)
        $x("//select[@name='period_type']").selectOptionByValue("Y");

        // Ввести 7.5% в поле "Процентная ставка"
        $x("//*[@name='percent']").setValue("7.5");

        // Выбрать тип платежа "Дифференцированный" (кликаем на label, так как input скрыт)
        $x("//label[@for='payment-type-2']").click();

        // Убедиться, что выбран тип платежа "Дифференцированный"
        $("#payment-type-2").shouldBe(checked);

        // Проверить, что не выбран тип платежа "Аннуитетный"
        $("#payment-type-1").shouldNotBe(checked);

        // Нажать кнопку «Рассчитать»
        $x("//input[@type='submit']")
                .scrollTo()
                .shouldBe(clickable)
                .click();

        // Ожидание появления результатов
        sleep(2000);

        // Ожидаемый результат:
        //– Все перечисленные значения появились после нажатия кнопки «Рассчитать»
        //– В блоке результатов отображается Ежемесячный платёж
        $x("//div[contains(@class, 'result-placeholder-monthly_payment')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        sleep(5000);
    }

    // Название: Расчёт ипотеки со сроком в месяцах
    @Test
    void test03MonthsPeriod() {

        // Переключиться на режим "По сумме кредита"
        $x("//a[@data-name='type' and @data-value='2']").click();
        sleep(500); // Даем время на переключение вкладки

        // Убедиться что форма не содержит данных
        $x("//*[@name='credit_sum']").shouldBe(empty);
        $x("//*[@name='period']").shouldBe(empty);
        $x("//*[@name='percent']").shouldBe(empty);

        // Ввести 2 000 000 в поле "Сумма кредита"
        $x("//*[@name='credit_sum']").setValue("2000000");

        // Ввести 120 месяцев в поле "Срок кредита"
        $x("//*[@name='period']").setValue("120");

        // Выбор единицы измерения срока (месяцы)
        $x("//select[@name='period_type']").selectOptionByValue("M");

        // Ввести 9% в поле "Процентная ставка"
        $x("//*[@name='percent']").setValue("9");

        // Убедиться, что выбран тип платежа "Аннуитетный"
        $("#payment-type-1").shouldBe(checked);

        // Проверить, что не выбран тип платежа "Дифференцированный"
        $("#payment-type-2").shouldNotBe(checked);

        // Нажать кнопку «Рассчитать»
        $x("//input[@type='submit']")
                .scrollTo()
                .shouldBe(clickable)
                .click();

        // Ожидаемый результат:
        //– Все перечисленные значения появились после нажатия кнопки «Рассчитать»
        //– В блоке результатов отображается Ежемесячный платёж, Начисленные проценты, Долг + проценты
        $x("//div[contains(@class, 'result-placeholder-monthly_payment')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        $x("//div[contains(@class, 'result-placeholder-interest')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        $x("//div[contains(@class, 'result-placeholder-total_paid')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        sleep(5000);
    }
}