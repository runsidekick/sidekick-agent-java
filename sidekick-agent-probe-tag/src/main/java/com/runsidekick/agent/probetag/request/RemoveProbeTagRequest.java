package com.runsidekick.agent.probetag.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

/**
 * @author yasin.kalafat
 */
public class RemoveProbeTagRequest extends BaseRequest {

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
