package dev.voxelbrew.api.model.parameters;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CadFormulaTest {
    @Test
    void geometryConstantsAndFunctionsWorkWithKeyboardSpellingsAndParameters() {
        var p = ParameterSet.empty().put("foo", ParameterUnit.NUMBER, "9", "");
        assertThat(p.value("sqrt($foo)", ParameterUnit.NUMBER)).isEqualTo(3);
        for (String expression :
                new String[] {"π-pi", "τ-tau", "φ-phi", "τ-2*pi", "ln(e)-1", "log(1000)-3", "φ^2-φ-1"}) {
            assertThat(p.value(expression, ParameterUnit.NUMBER)).isCloseTo(0, within(1e-12));
        }
        assertThat(p.value("2^3^2", ParameterUnit.NUMBER)).isEqualTo(512);
        assertThat(p.value("-2^2", ParameterUnit.NUMBER)).isEqualTo(-4);
        assertThat(p.value("1e-3", ParameterUnit.NUMBER)).isEqualTo(.001);
        var named = p.put("tau", ParameterUnit.NUMBER, "3", "").put("log", ParameterUnit.NUMBER, "log(100)+tau", "");
        assertThat(named.rename("log", "old").value("$old + tau + $tau", ParameterUnit.NUMBER))
                .isEqualTo(11);
        assertThat(named.value("τ", ParameterUnit.NUMBER)).isEqualTo(Math.TAU);
    }

    @Test
    void convertsQuantitiesAndFormatsTextInTheRequestedUnit() {
        var p = ParameterSet.empty().put("foo", ParameterUnit.CM, "125", "");
        assertThat(p.value("convertUnit($foo,m)", ParameterUnit.NUMBER)).isEqualTo(1.25);
        assertThat(p.value("convertUnit(180 deg,rad)", ParameterUnit.NUMBER)).isCloseTo(Math.PI, within(1e-12));
        assertThat(p.text("=\"Length: \" + formatUnit($foo,m)")).isEqualTo("Length: 1.25 m");
        assertThat(p.value("= 5 cm", ParameterUnit.MM)).isEqualTo(50);
        for (var input : new String[] {
            "log(0)",
            "ln(-1)",
            "log(1 mm)",
            "convertUnit(1 cm,deg)",
            "formatUnit(1 cm,kg)",
            "convertUnit(5 mm)",
            "1%0",
            "clamp(5,2,1)"
        }) {
            assertThatThrownBy(() -> p.text("=" + input)).isInstanceOf(IllegalArgumentException.class);
        }
        assertThatThrownBy(() -> p.value("formatUnit($foo,m)", ParameterUnit.MM))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moduloAndClampRespectUnitsAndRounding() {
        var p = ParameterSet.empty();
        assertThat(p.value("-7%5", ParameterUnit.NUMBER)).isEqualTo(3);
        assertThat(p.value("7%-5", ParameterUnit.NUMBER)).isEqualTo(-3);
        assertThat(p.value("3*2.5%2*4", ParameterUnit.NUMBER)).isEqualTo(6);
        assertThat(p.value("25 mm % 1 cm", ParameterUnit.MM)).isEqualTo(5);
        assertThat(p.value("clamp(12 mm,2 mm,1 cm)", ParameterUnit.MM)).isEqualTo(10);
        assertThat(p.value("clamp(12,3,3)", ParameterUnit.NUMBER)).isEqualTo(3);
        assertThat(p.value("floor(-2.3)+ceiling(-2.3)", ParameterUnit.NUMBER)).isEqualTo(-5);
    }
}
