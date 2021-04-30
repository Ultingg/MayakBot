package ru.kumkuat.application.GameModule.Service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimerService extends TimerTask {

    private IUseTimer iUseTimer;

    @Override
    public void run() {
        try {
            iUseTimer.TimerOperation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimerOperation(IUseTimer iUseTimer) {
        this.iUseTimer = iUseTimer;
    }
}
