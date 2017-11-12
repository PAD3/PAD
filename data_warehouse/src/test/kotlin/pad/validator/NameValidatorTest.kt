package pad.validator

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import org.junit.jupiter.api.Assertions.*

internal class NameValidatorTest {
    @org.junit.jupiter.api.Test
    fun validate() {
        val name = "Igoryyy"
        val result = FluentValidator.checkAll()
                .on(name,NameValidator())
                .doValidate()
                .result(toSimple())
        println(result.isSuccess)
    }

}