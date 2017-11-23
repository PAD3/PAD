package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler


class UUIDValidator(val param : String) : ValidatorHandler<String>(), Validator<String> {

    override fun validate(context: ValidatorContext?, t: String?): Boolean {
        if (t == null) {
            context?.addErrorMsg("No $param provided!")
            return false
        }

        if (!t.matches(Regex("[a-z0-9]{32}"))) {
            context?.addErrorMsg("$param should contain 32 characters including letters and digits")
            return false
        }
        return true
    }
}