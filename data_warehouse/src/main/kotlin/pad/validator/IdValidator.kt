package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler

class IdValidator constructor(val fieldName : String): ValidatorHandler<String>(), Validator<String> {

    override fun validate(context: ValidatorContext?, t: String?): Boolean {
        if (t == null || t.isEmpty()) {
            context?.addErrorMsg("No $fieldName provided!")
            return false
        }
        if (t.length != 32) {
            context?.addErrorMsg("Invalid $fieldName!")
            return false
        }
        return true
    }
}