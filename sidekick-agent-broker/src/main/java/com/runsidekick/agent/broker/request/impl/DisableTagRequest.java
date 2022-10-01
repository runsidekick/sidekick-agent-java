package com.runsidekick.agent.broker.request.impl;

/**
 * @author yasin.kalafat
 */
public class DisableTagRequest extends BaseRequest {

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
