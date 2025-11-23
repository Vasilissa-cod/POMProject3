import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
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
    @Step("Открыть калькулятор ипотеки и принять куки")
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
        checkMonthlyPaymentResult();

        sleep(5000);
    }

    // Название: Расчёт ипотеки с дифференцированными платежами
    @Test
    void test02DifferentiatedPayment() {

        // Переключиться на режим "По сумме кредита"
        switchToSumMode();

        // Убедиться что форма не содержит данных
        verifyFormIsEmpty();

        // Ввести 3 000 000 в поле "Сумма кредита"
        enterCreditSum("3000000");

        // Ввести 15 лет в поле "Срок кредита"
        enterPeriod("15");

        // Выбор единицы измерения срока (годы)
        selectPeriodType("Y");

        // Ввести 7.5% в поле "Процентная ставка"
        enterPercent("7.5");

        // Выбрать тип платежа "Дифференцированный"
        selectDifferentiatedPayment();

        // Нажать кнопку «Рассчитать»
        clickCalculateButton();

        // Ожидание появления результатов
        sleep(2000);

        // Ожидаемый результат:
        //– Все перечисленные значения появились после нажатия кнопки «Рассчитать»
        //– В блоке результатов отображается Ежемесячный платёж
        checkMonthlyPaymentResult();

        sleep(5000);
    }

    // Название: Расчёт ипотеки со сроком в месяцах
    @Test
    void test03MonthsPeriod() {

        // Переключиться на режим "По сумме кредита"
        switchToSumMode();

        // Убедиться что форма не содержит данных
        verifyFormIsEmpty();

        // Ввести 2 000 000 в поле "Сумма кредита"
        enterCreditSum("2000000");

        // Ввести 120 месяцев в поле "Срок кредита"
        enterPeriod("120");

        // Выбор единицы измерения срока (месяцы)
        selectPeriodType("M");

        // Ввести 9% в поле "Процентная ставка"
        enterPercent("9");

        // Убедиться, что выбран тип платежа "Аннуитетный"
        selectAnnuityPayment();

        // Нажать кнопку «Рассчитать»
        clickCalculateButton();

        // Ожидаемый результат:
        //– Все перечисленные значения появились после нажатия кнопки «Рассчитать»
        //– В блоке результатов отображается Ежемесячный платёж, Начисленные проценты, Долг + проценты
        checkFullPaymentResults();

        sleep(5000);
    }

    @Step("Проверить, что отображаются все результаты расчёта: ежемесячный платёж, начисленные проценты, общая сумма")
    private void checkFullPaymentResults() {
        $x("//div[contains(@class, 'result-placeholder-monthly_payment')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        $x("//div[contains(@class, 'result-placeholder-interest')]")
                .shouldBe(visible)
                .shouldNotBe(empty);

        $x("//div[contains(@class, 'result-placeholder-total_paid')]")
                .shouldBe(visible)
                .shouldNotBe(empty);
    }

    @Step("Проверить, что отображается ежемесячный платёж")
    private void checkMonthlyPaymentResult() {
        $x("//div[contains(@class, 'result-placeholder-monthly_payment')]")
                .shouldBe(visible)
                .shouldNotBe(empty);
    }

    @Step("Переключиться на режим 'По сумме кредита'")
    private void switchToSumMode() {
        $x("//a[@data-name='type' and @data-value='2']").click();
        sleep(500);
    }

    @Step("Убедиться, что форма не содержит данных")
    private void verifyFormIsEmpty() {
        $x("//*[@name='credit_sum']").shouldBe(empty);
        $x("//*[@name='period']").shouldBe(empty);
        $x("//*[@name='percent']").shouldBe(empty);
    }

    @Step("Ввести '{sum}' в поле 'Сумма кредита'")
    private void enterCreditSum(String sum) {
        $x("//*[@name='credit_sum']").setValue(sum);
    }

    @Step("Ввести '{period}' в поле 'Срок кредита'")
    private void enterPeriod(String period) {
        $x("//*[@name='period']").setValue(period);
    }

    @Step("Выбрать единицу измерения срока: '{periodType}'")
    private void selectPeriodType(String periodType) {
        $x("//select[@name='period_type']").selectOptionByValue(periodType);
    }

    @Step("Ввести '{percent}%' в поле 'Процентная ставка'")
    private void enterPercent(String percent) {
        $x("//*[@name='percent']").setValue(percent);
    }

    @Step("Выбрать тип платежа 'Дифференцированный'")
    private void selectDifferentiatedPayment() {
        $x("//label[@for='payment-type-2']").click();
        $("#payment-type-2").shouldBe(checked);
        $("#payment-type-1").shouldNotBe(checked);
    }

    @Step("Выбрать тип платежа 'Аннуитетный'")
    private void selectAnnuityPayment() {
        $("#payment-type-1").shouldBe(checked);
        $("#payment-type-2").shouldNotBe(checked);
    }

    @Step("Нажать кнопку 'Рассчитать'")
    private void clickCalculateButton() {
        $x("//input[@type='submit']")
                .scrollTo()
                .shouldBe(clickable)
                .click();
    }
}