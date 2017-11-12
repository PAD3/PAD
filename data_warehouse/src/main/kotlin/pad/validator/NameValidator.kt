package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler


class NameValidator : ValidatorHandler<String>(), Validator<String> {

    override fun validate(context: ValidatorContext?, t: String?): Boolean {
        if (t == null) {
            context?.addErrorMsg("No name provided!")
            return false
        }
        if (t.length < 5 || t.length > 30) {
            context?.addErrorMsg("Name should contain from 5 to 30 characters")
            return false
        }
        if (!t.matches(Regex("[\\w ]+"))) {
            context?.addErrorMsg("Name should contain only numbers, letters and underscores")
            return false
        }
        return true
    }
}