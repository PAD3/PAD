package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler

class DescValidator : ValidatorHandler<String>(), Validator<String> {

    override fun validate(context: ValidatorContext?, t: String?): Boolean {
        if (t == null) {
            return true
        }
        if (t.length < 5) {
            context?.addErrorMsg("Description of a book should contain from 5 to 30 characters")
            return false
        }
        return true
    }
}