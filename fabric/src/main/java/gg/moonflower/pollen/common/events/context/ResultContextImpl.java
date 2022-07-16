package gg.moonflower.pollen.common.events.context;

import gg.moonflower.pollen.api.event.EventResult;
import gg.moonflower.pollen.api.event.ResultContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ResultContextImpl implements ResultContext {
    private EventResult result;

    public ResultContextImpl() {
        this.setResult(EventResult.DEFAULT);
    }

    @Override
    public EventResult getResult() {
        return result;
    }

    @Override
    public void setResult(EventResult result) {
        this.result = result;
    }
}
