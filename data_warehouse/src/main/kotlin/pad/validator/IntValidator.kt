package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler
import java.util.*

class IntValidator constructor(val min : Int, val max : Int): ValidatorHandler<Int>(), Validator<Int> {

    override fun validate(context: ValidatorContext?, t: Int?): Boolean {
        if (t == null || t == Int.MIN_VALUE) {
            context?.addErrorMsg("No year provided!")
            return false
        }
        if (t < min || t > max) {
            context?.addErrorMsg("Invalid year!")
            return false
        }
        return true
    }
}
