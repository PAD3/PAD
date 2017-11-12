package pad.validator

import com.baidu.unbiz.fluentvalidator.Validator
import com.baidu.unbiz.fluentvalidator.ValidatorContext
import com.baidu.unbiz.fluentvalidator.ValidatorHandler
import java.util.*

class YearValidator : ValidatorHandler<Int>(), Validator<Int> {

    override fun validate(context: ValidatorContext?, t: Int?): Boolean {
        if (t == null) {
            context?.addErrorMsg("No year provided!")
            return false
        }
        if (t < 1900 || t > Calendar.getInstance().get(Calendar.YEAR)) {
            context?.addErrorMsg("Invalid year!")
            return false
        }
        return true
    }
}
