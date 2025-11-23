package pages;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class CalcPage {

    SelenideElement
            linkToSum = $x("//a[@data-name='type' and @data-value='2']"),
            creditSum = $x("//*[@name='credit_sum']"),
            period = $x("//*[@name='period']"),
            percent = $x("//*[@name='percent']"),
            periodType = $x("//select[@name='period_type']"),
            annuitent =  $("#payment-type-1"),
            diff = $("#payment-type-2"),
            calculateButton = $x("//input[@type='submit']");

    @Step("Переключиться на режим 'По сумме кредита'")
    public void switchToSum() {
        this.linkToSum.click();
        sleep(500);
    }

    @Step("Убедиться, что форма не содержит данных")
    public void isNoDataInFills() {
        this.creditSum.shouldBe(empty);
        this.period.shouldBe(empty);
        this.percent.shouldBe(empty);
    }

    @Step("Ввести '{sum}' в поле 'Сумма кредита'")
    public void setCreditSum(String sum) {
        this.creditSum.setValue(sum);
    }

    @Step("Заполнить параметры: срок '{period}', единица '{periodType}', ставка '{percent}%' и выполнить расчёт")
    public void setParametersAndCalculate(String period, String periodType, String percent) {
        this.period.setValue(period);

        // Выбор единицы измерения срока (годы)
        this.periodType.selectOptionByValue(periodType);

        // Ввести 8.5% в поле "Процентная ставка"
        this.percent.setValue(percent);

        // Убедиться, что выбран тип платежа "Аннуитетный"
        this.annuitent.shouldBe(checked);

        // Проверить, что не выбран тип платежа "Дифференцированный"
        this.diff.shouldNotBe(checked);

        // Нажать кнопку «Рассчитать»
        this.calculateButton
                .scrollTo()
                .shouldBe(clickable)
                .click();

    }
}
