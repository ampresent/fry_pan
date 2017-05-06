package sample.web.secure.jdbc.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Service.Inter.SystemSer;

/**
 * Created by wuyihao on 4/29/17.
 */
@Service
public class SystemSerImpl implements SystemSer {

    @Override
    public boolean lowPressure() {
        return true;
    }
}
