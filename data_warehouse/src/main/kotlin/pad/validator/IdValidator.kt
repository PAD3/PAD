package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler

class IdValidator constructor(val fieldName : String): ValidatorHandler<Int>(), Validator<Int> {

    override fun validate(context: ValidatorContext?, t: Int?): Boolean {
        if (t == null) {
            context?.addErrorMsg("No $fieldName provided!")
            return false
        }
        if (t < 1 || t >= Int.MAX_VALUE) {
            context?.addErrorMsg("Invalid $fieldName!")
            return false
        }
        return true
    }
}