package org.meier.model;

import org.meier.bean.VariableBean;

import java.util.List;

public interface CodeContainer {

    List<VariableBean> getVariables();
    void addVariable(VariableBean variable);

}
