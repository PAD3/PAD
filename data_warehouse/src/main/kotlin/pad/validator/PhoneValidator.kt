package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler

class PhoneValidator : ValidatorHandler<String>(), Validator<String> {

    override fun validate(context: ValidatorContext?, t: String?): Boolean {
        if (t == null) {
            context?.addErrorMsg("No phone provided!")
            return false
        }
        if (t.length < 6 || t.length > 20) {
            context?.addErrorMsg("Phone number should contain from 5 to 20 characters")
            return false
        }
        if (!t.matches(Regex("[+]?[\\d]{6,12}+"))) {
            context?.addErrorMsg("Phone should contain only numbers and can begin with +")
            return false
        }
        return true
    }
}